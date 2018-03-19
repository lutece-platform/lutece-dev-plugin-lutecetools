/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.lutecetools.web.rs;

import fr.paris.lutece.plugins.lutecetools.business.Stats;
import fr.paris.lutece.plugins.lutecetools.service.StatsService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.xml.XmlUtil;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author pierre
 */
@Path( RestConstants.BASE_PATH + Constants.PATH_PLUGIN + Constants.PATH_STATS )
public class StatsRest
{
    private static final String KEY_STATS = "stats";
    private static final String KEY_MAVEN_COUNT = "maven-count";
    private static final String KEY_GITHUB_COUNT = "github-count";
    private static final String KEY_GITHUB_OK = "github-ok";
    private static final String KEY_JIRA_OK = "jira-ok";
    private static final String KEY_README_OK = "readme-ok";

    private static final ObjectMapper _mapper = new ObjectMapper( );

    @GET
    @Path( "/" )
    public Response getStats( @HeaderParam( HttpHeaders.ACCEPT ) String strAccept, @QueryParam( Constants.PARAMETER_FORMAT ) String strFormat )
            throws IOException
    {
        String entity;
        String mediaType = MediaType.APPLICATION_JSON;

        if ( strFormat != null )
        {
            if ( strFormat.equals( Constants.MEDIA_TYPE_XML ) )
            {
                entity = getStatsXml( );
                mediaType = MediaType.APPLICATION_XML;
            }
            else
            {
                entity = getStatsJson( );
            }
        }
        else
            if ( ( strAccept != null ) && strAccept.contains( MediaType.APPLICATION_XML ) )
            {
                entity = getStatsXml( );
                mediaType = MediaType.APPLICATION_XML;
            }
            else
            {
                entity = getStatsJson( );
            }

        return Response.ok( entity, mediaType ).build( );
    }

    private String getStatsJson( ) throws IOException
    {
        Stats stats = StatsService.getStats( );
        return _mapper.writeValueAsString( stats );
    }

    private String getStatsXml( ) throws IOException
    {
        Stats stats = StatsService.getStats( );
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader( ) );
        XmlUtil.beginElement( sbXML, KEY_STATS );
        XmlUtil.addElement( sbXML, KEY_MAVEN_COUNT, stats.getMavenCount( ) );
        XmlUtil.addElement( sbXML, KEY_GITHUB_COUNT, stats.getGithubCount( ) );
        XmlUtil.addElement( sbXML, KEY_GITHUB_OK, stats.getGithubOK( ) );
        XmlUtil.addElement( sbXML, KEY_JIRA_OK, stats.getJiraOK( ) );
        XmlUtil.addElement( sbXML, KEY_README_OK, stats.getReadmeOK( ) );
        XmlUtil.endElement( sbXML, KEY_STATS );

        return sbXML.toString( );
    }

}
