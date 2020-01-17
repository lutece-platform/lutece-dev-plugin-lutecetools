/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.lutecetools.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Browser to select a directory
 */
public class XMLParser
{
    private static final String PROPERTY_MAVEN_REPO_URL = "lutecetools.maven.repository.url";
    private static final String URL_MAVEN_REPO = AppPropertiesService.getProperty( PROPERTY_MAVEN_REPO_URL );
    private static final String PROPERTY_MAVEN_PATH_PLUGINS = "lutecetools.maven.repository.path.plugins";
    private static final String URL_PLUGINS = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_PLUGINS );
    private static final String PROPERTY_MAVEN_PATH_CORE = "lutecetools.maven.repository.path.core";
    private static final String URL_CORE = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_CORE );

    // Tags
    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_VERSION = "version";
    private static final String TAG_MAIN_NODE = "project";

    // Messages
    private static final String DIALOG_MESS_PART_1 = "Les dépendances :";
    private static final String DIALOG_MESS_PART_2 = "ont conservé leur version car la nouvelle n'a pas pu être récupérée.";

    // Errors
    private static final String RELEASE_NOT_FOUND = "Release not found";

    private static final String LUTECE_CORE = "lutece-core";
    
    private XMLParser( )
    {
    }

    public static String updatePOM( FileItem inputFile )
    {
        String strUpdated = "";
        try
        {
            strUpdated = process( inputFile );
        }
        catch( IOException | ParserConfigurationException | SAXException | TransformerException e )
        {
            AppLogService.error( e.getMessage( ) );
        }

        return strUpdated;
    }

    private static String process( FileItem inputFile ) throws ParserConfigurationException, SAXException, IOException, TransformerException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance( );
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder( );
        Document doc = dBuilder.parse( inputFile.getInputStream( ) );
        doc.getDocumentElement( ).normalize( );
        NodeList nList = doc.getElementsByTagName( TAG_DEPENDENCY );

        ArrayList<String> arr = new ArrayList<>( );
        for ( Integer tmp = 0; tmp < nList.getLength( ); tmp++ )
        {
            Node nNode = nList.item( tmp );

            if ( nNode.getNodeType( ) == Node.ELEMENT_NODE && nNode.getParentNode( ).getParentNode( ).getNodeName( ).equals( TAG_MAIN_NODE ) )
            {
                Element eElement = (Element) nNode;
                String strArtifactId = eElement.getElementsByTagName( TAG_ARTIFACT_ID ).item( 0 ).getTextContent( );
                String strLastRelease;
                if ( strArtifactId.equals( LUTECE_CORE ) )
                {
                    strLastRelease = MavenRepoService.instance( ).getVersion( URL_CORE );
                }
                else
                {
                    strLastRelease = MavenRepoService.instance( ).getVersion( URL_PLUGINS + strArtifactId );
                }
                if ( !strLastRelease.equals( RELEASE_NOT_FOUND ) )
                {
                    eElement.getElementsByTagName( TAG_VERSION ).item( 0 ).setTextContent( strLastRelease );
                }
                else
                {
                    arr.add( strArtifactId );
                }
            }
        }

        String artifactIdList = "";
        if ( !arr.isEmpty( ) )
        {
            for ( String list : arr )
            {
                artifactIdList = artifactIdList.concat( "- " ).concat( list ).concat( "<br />" );
            }
            Globals._strWarningPomMessage = DIALOG_MESS_PART_1.concat( "<br />" ).concat( artifactIdList ).concat( DIALOG_MESS_PART_2 );
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance( );
        Transformer transformer = transformerFactory.newTransformer( );
        DOMSource domSource = new DOMSource( doc );
        StringWriter writer = new StringWriter( );
        StreamResult streamResult = new StreamResult( writer );
        transformer.transform( domSource, streamResult );
        return writer.toString( );
    }
}
