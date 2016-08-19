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

    public static String updatePOM( FileItem _inputFile )
    {
        String strUpdated = "";
        try
        {
            strUpdated = process( _inputFile );
        }
        catch( IOException | ParserConfigurationException | SAXException | TransformerException e )
        {
            AppLogService.error( e.getMessage( ) );
        }

        return strUpdated;
    }

    private static String process( FileItem _inputFile ) throws ParserConfigurationException, SAXException, IOException, TransformerException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance( );
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder( );
        Document doc = dBuilder.parse( _inputFile.getInputStream( ) );
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
                    strLastRelease = MavenRepoService.getVersion( URL_CORE );
                }
                else
                {
                    strLastRelease = MavenRepoService.getVersion( URL_PLUGINS + strArtifactId );
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
        String strOutput = writer.toString( );

        return strOutput;
    }
}
