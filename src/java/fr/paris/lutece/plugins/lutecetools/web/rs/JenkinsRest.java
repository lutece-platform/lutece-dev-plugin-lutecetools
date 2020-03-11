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
package fr.paris.lutece.plugins.lutecetools.web.rs;

import java.net.URI;
import java.time.LocalDateTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import fr.paris.lutece.plugins.lutecetools.business.JenkinsBuildResult;
import fr.paris.lutece.plugins.lutecetools.business.JenkinsBuildResultHome;
import fr.paris.lutece.plugins.lutecetools.service.JenkinsService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * JenkinsRest
 */
@Path( RestConstants.BASE_PATH + Constants.PATH_PLUGIN + Constants.PATH_JENKINS )
public class JenkinsRest
{
    private static JenkinsService _jenkinsService;

    /**
     * Set the Jenkins service
     * 
     * @param jenkinsService
     *            The service
     */
    public void setJenkinsService( JenkinsService jenkinsService )
    {
        _jenkinsService = jenkinsService;
    }

    /**
     * Returns Jenkins Badge URL
     * 
     * @param strUrl
     *            The Jenkins Job URL
     * @return The badge URL
     */
    @GET
    @Path( Constants.PATH_JENKINS_BADGE )
    @Produces( "image/svg+xml" )
    public Response getJenkinsBadge( @QueryParam( Constants.PARAMETER_URL ) String strUrl )
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strContent = httpAccess.doGet( strUrl, _jenkinsService.getJenkinsAuthenticator( ), null );
            return Response.ok( strContent, "image/svg+xml" ).build( );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "LuteceTools : Bad Jenkins Job URL : " + strUrl );
            String strRedirectURI = JenkinsService.DEFAULT_BADGE_URL;
            return Response.temporaryRedirect( URI.create( strRedirectURI ) ).build( );
        }

    }
    
    /**
     * Update the build result of an artifact
     * @param strArtifact
     * @param strResult
     */
    @POST
    @Path( "/updateBuildResult" )
    @Consumes
    public void updateBuildResult( @QueryParam( "artifact" ) String strArtifact, @QueryParam( "result" ) String strResult  )
    {
        JenkinsBuildResult result = JenkinsBuildResultHome.findByPrimaryKey( strArtifact );
        AppLogService.debug( "updateBuildResult for artifact " + strArtifact + " and result " + strResult );
        if ( result == null )
        {
            result = new JenkinsBuildResult( );
            result.setArtifactName( strArtifact );
            result.setBuildResult( strResult );
            result.setBuildDate( LocalDateTime.now( ) );
            JenkinsBuildResultHome.create( result );
        }
        else
        {
            result.setBuildResult( strResult );
            result.setBuildDate( LocalDateTime.now( ) );
            JenkinsBuildResultHome.update( result );
        }
    }
}
