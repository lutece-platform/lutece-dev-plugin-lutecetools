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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.business.Site;
import fr.paris.lutece.plugins.lutecetools.business.dto.SiteBuilderConfDto;
import fr.paris.lutece.plugins.lutecetools.service.DependenciesService;
import fr.paris.lutece.plugins.lutecetools.service.MavenRepoService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Rest service for generating pom files or part of pom files.
 */
@Path(RestConstants.BASE_PATH + Constants.PATH_PLUGIN + Constants.PATH_POM_BUILDER)
public class PomBuilderRest
{
    private static final ObjectMapper _mapper = new ObjectMapper( );

    /**
     * Returns an XML representing the pom.xml file of the site whose informations
     * are provided in the siteBuilderConfigDto object
     * 
     * @param strSiteBuilderConfigDto
     * @return The badge URL
     */
    @POST
    @Path(Constants.PATH_SITE)
    @Produces("application/xml")
    @Consumes("application/json")
    public Response getSitePom( String strSiteBuilderConfigDto )
    {
        try
        {
            SiteBuilderConfDto siteBuilderConfDto = _mapper.readValue( strSiteBuilderConfigDto,
                    SiteBuilderConfDto.class );
            List<Component> listFullComponent = new ArrayList<>( );
            // Fill given components if there are missing infos in the config
            for ( Component comp : siteBuilderConfDto.getListComponents( ) )
            {
                if ( StringUtils.isEmpty( comp.getVersion( ) ) || StringUtils.isEmpty( comp.getComponentType( ) ) )
                {
                    Component fullCompo = MavenRepoService.instance( ).getComponent( comp.getArtifactId( ), true,
                            false );
                    listFullComponent.add( fullCompo );
                }
            }

            Site siteToBuild = new Site( );
            siteToBuild.setArtifactId( siteBuilderConfDto.getArtifactId( ) );
            siteToBuild.setComponentType( siteBuilderConfDto.getComponentType( ) );
            siteToBuild.setGroupId( siteBuilderConfDto.getGroupId( ) );
            siteToBuild.setName( siteBuilderConfDto.getSiteName( ) );
            siteToBuild.setVersion( siteBuilderConfDto.getVersion( ) );

            return Response.ok( DependenciesService.process( listFullComponent, "pom", siteToBuild ) ).build( );
        }
        catch ( IOException e )
        {
            AppLogService.error( " Wrong site builder config format ", e );
            return Response.serverError( ).build( );
        }

    }
}
