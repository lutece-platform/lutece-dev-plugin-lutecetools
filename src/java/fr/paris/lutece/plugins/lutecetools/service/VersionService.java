/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.lutecetools.business.Dependency;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;


/**
 * Version Service
 */
public class VersionService
{
    private static final String PROPERTY_MAVEN_REPO_URL = "lutecetools.maven.repository.url";
    private static final String URL_MAVEN_REPO = AppPropertiesService.getProperty( PROPERTY_MAVEN_REPO_URL );
    private static final String PROPERTY_MAVEN_PATH_PLUGINS = "lutecetools.maven.repository.path.plugins";
    private static final String URL_PLUGINS = URL_MAVEN_REPO +
        AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_PLUGINS );
    private static final String PROPERTY_MAVEN_PATH_SITE_POM = "lutecetools.maven.repository.path.site-pom";
    private static final String URL_SITE_POM = URL_MAVEN_REPO +
        AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_SITE_POM );
    private static final String KEY_SITE_POM_VERSION = "lutecetools.pom.site.version";
    private static final String RELEASE_NOT_FOUND = "Release not found";

    /**
     * Set the component's version
     * @param component The component
     */
    public static void setReleaseVersion( Dependency component )
    {
        component.setVersion( getVersion( URL_PLUGINS + component.getArtifactId(  ) ) );
    }

    /**
     * Set the POM site version
     */
    public static void setPomSiteVersion(  )
    {
        String strVersion = getVersion( URL_SITE_POM );
        DatastoreService.setDataValue( KEY_SITE_POM_VERSION, strVersion );
    }

    /**
     * Retrieve a version from the maven repository
     * @param strUrl The maven repository URL
     * @return The version
     */
    private static String getVersion( String strUrl )
    {
        String strVersion = RELEASE_NOT_FOUND;

        try
        {
            HttpAccess httpAccess = new HttpAccess(  );
            String strHtml = httpAccess.doGet( strUrl );
            Document jarList = Jsoup.parse( strHtml );
            Elements trList = jarList.select( "td a" );

            for ( int i = 4; i < trList.size(  ); i++ )
            {
                Element tr = trList.get( i );
                strVersion = tr.text(  ).replaceAll( "\\/", "" );
            }
        }
        catch ( HttpAccessException e )
        {
            AppLogService.error( "AppStore - ComponentInfoService : Error retrieving release version : " +
                e.getMessage(  ), e );
        }

        return strVersion;
    }
}
