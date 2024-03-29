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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.lutecetools.service;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author leridons
 */
public final class PomService
{

    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_DEPENDENCY_GROUPID = "groupId";
    private static final String TAG_DEPENDENCY_ARTIFACT_ID = "artifactId";
    private static final String TAG_DEPENDENCY_VERSION = "version";
    private static final String TAG_DEPENDENCY_TYPE = "type";

    private static final String CONSTANT_LUTECE_GROUP = "fr.paris.lutece";

    private static final String EXCEPTION_MESSAGE = "LuteceTools - PomService : Error retrieving Pom tags : ";

    private static PomService _singleton;

    /**
     * Private constructor
     */
    private PomService( )
    {
    }

    /**
     * Returns the unique instance
     * 
     * @return the unique instance
     */
    public static synchronized PomService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new PomService( );
        }

        return _singleton;
    }

    /**
     * Retreive SITE infos for a given component
     * 
     * @param component The component
     * @param strPomUrl
     * @param sbLogs    Logs
     */
    public void getLuteceDependencies( Component component, String strPomUrl, boolean bSnapshot, StringBuilder sbLogs )
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strPom = httpAccess.doGet( strPomUrl );

            DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance( );
            InputStream is = new ByteArrayInputStream( strPom.getBytes( ) ); // use inputstream to ignore BOM when
                                                                             // parsing

            DocumentBuilder builder = documentBuilder.newDocumentBuilder( );
            Document document = builder.parse( new InputSource( is ) );
            Element root = document.getDocumentElement( );

            List<HashMap<String, String>> dependencyList = new ArrayList<>( );

            NodeList nodeDependencyList = root.getElementsByTagName( TAG_DEPENDENCY );
            for ( int i = 0; i < nodeDependencyList.getLength( ); i++ )
            {
                Element elmt = (Element) nodeDependencyList.item( i );
                NodeList groupList = elmt.getElementsByTagName( TAG_DEPENDENCY_GROUPID );
                String strGroupId = null;
                if ( groupList != null && groupList.getLength( ) > 0 )
                {
                    Element groupElement = (Element) groupList.item( 0 );
                    strGroupId = groupElement.getTextContent( );
                }
                if ( strGroupId == null || !strGroupId.startsWith( CONSTANT_LUTECE_GROUP ) )
                {
                    continue;
                }
                NodeList artifactIdList = elmt.getElementsByTagName( TAG_DEPENDENCY_ARTIFACT_ID );
                String strArtifactId = null;
                if ( artifactIdList != null && artifactIdList.getLength( ) > 0 )
                {
                    Element artifactElement = (Element) artifactIdList.item( 0 );
                    strArtifactId = artifactElement.getTextContent( );
                }

                NodeList versionList = elmt.getElementsByTagName( TAG_DEPENDENCY_VERSION );
                String strVersion = null;
                if ( versionList != null && versionList.getLength( ) > 0 )
                {
                    Element versionElement = (Element) versionList.item( 0 );
                    strVersion = versionElement.getTextContent( );
                }

                NodeList typeList = elmt.getElementsByTagName( TAG_DEPENDENCY_TYPE );
                String strType = null;
                if ( typeList != null && typeList.getLength( ) > 0 )
                {
                    Element typeElement = (Element) typeList.item( 0 );
                    strType = typeElement.getTextContent( );
                }

                HashMap<String, String> dependencyMap = new HashMap<>( );
                dependencyMap.put( TAG_DEPENDENCY_GROUPID, strGroupId );
                dependencyMap.put( TAG_DEPENDENCY_ARTIFACT_ID, strArtifactId );
                dependencyMap.put( TAG_DEPENDENCY_VERSION, strVersion );
                dependencyMap.put( TAG_DEPENDENCY_TYPE, strType );

                dependencyList.add( dependencyMap );
            }

            component.set( ( bSnapshot ? "SNAPSHOT_" : "" ) + Component.DEPENDENCY_LIST, dependencyList );

        }
        catch ( IOException | ParserConfigurationException | DOMException | SAXException e )
        {
            AppLogService.error( EXCEPTION_MESSAGE + component.getArtifactId( ) + " : " + e.getMessage( ), e );
        }
        catch ( HttpAccessException e )
        {
            sbLogs.append( "\n*** ERROR *** Error reading pom for component :" ).append( component.getArtifactId( ) )
                    .append( " [url : " ).append( strPomUrl ).append( "]" ).append( EXCEPTION_MESSAGE );
        }
    }

}
