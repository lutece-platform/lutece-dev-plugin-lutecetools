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
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private String getStatsXml( )
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
