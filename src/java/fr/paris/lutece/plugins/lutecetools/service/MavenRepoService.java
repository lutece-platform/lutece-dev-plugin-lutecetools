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

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.business.Dependency;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Version Service
 */
public class MavenRepoService extends AbstractCacheableService
{

    private static final String PROPERTY_MAVEN_REPO_URL = "lutecetools.maven.repository.url";
    private static final String URL_MAVEN_REPO = AppPropertiesService.getProperty(PROPERTY_MAVEN_REPO_URL);
    private static final String PROPERTY_MAVEN_PATH_PLUGINS = "lutecetools.maven.repository.path.plugins";
    private static final String URL_PLUGINS = URL_MAVEN_REPO
            + AppPropertiesService.getProperty(PROPERTY_MAVEN_PATH_PLUGINS);
    private static final String PROPERTY_MAVEN_PATH_SITE_POM = "lutecetools.maven.repository.path.site-pom";
    private static final String URL_SITE_POM = URL_MAVEN_REPO
            + AppPropertiesService.getProperty(PROPERTY_MAVEN_PATH_SITE_POM);
    private static final String KEY_SITE_POM_VERSION = "lutecetools.pom.site.version";
    private static final String RELEASE_NOT_FOUND = "Release not found";
    private static final String CACHE_SERVICE_NAME = "LuteceTools Maven Repository Cache Service";

    // SNAPSHOT
    private static final String PROPERTY_SNAPSHOT_REPO_URL = "lutecetools.snapshot.repository.url";
    private static final String URL_SNAPSHOT_REPO = AppPropertiesService.getProperty(PROPERTY_SNAPSHOT_REPO_URL);
    private static final String URL_SNAPSHOT_PLUGINS = URL_SNAPSHOT_REPO + AppPropertiesService.getProperty(PROPERTY_MAVEN_PATH_PLUGINS);

    private static MavenRepoService _singleton;

    /**
     * Private constructor
     */
    private MavenRepoService()
    {
    }

    /**
     * Returns the unique instance
     *
     * @return the unique instance
     */
    public static MavenRepoService instance()
    {
        if (_singleton == null)
        {
            _singleton = new MavenRepoService();
            _singleton.initCache();
        }

        return _singleton;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName()
    {
        return CACHE_SERVICE_NAME;
    }

    /**
     * Set the component's version
     *
     * @param component The component
     */
    public static void setReleaseVersion(Dependency component)
    {
        component.setVersion(getVersion(URL_PLUGINS + component.getArtifactId()));
    }

    /**
     * Set the POM site version
     */
    public static void setPomSiteVersion()
    {
        String strVersion = getVersion(URL_SITE_POM);
        DatastoreService.setDataValue(KEY_SITE_POM_VERSION, strVersion);
    }

    /**
     * Retrieve a version from the maven repository
     *
     * @param strUrl The maven repository URL
     * @return The version
     */
    private static String getVersion(String strUrl)
    {
        String strVersion = RELEASE_NOT_FOUND;

        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strHtml = httpAccess.doGet(strUrl);
            Document jarList = Jsoup.parse(strHtml);
            Elements trList = jarList.select("td a");

            for (int i = 4; i < trList.size(); i++)
            {
                Element tr = trList.get(i);
                strVersion = tr.text().replaceAll("\\/", "");
            }
        }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving release version : "
                    + e.getMessage(), e);
        }

        return strVersion;
    }

    /**
     * Gets the component list
     *
     * @return the component list
     */
    public List<Component> getComponentsList()
    {
        List<Component> list = new ArrayList<Component>();

        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strHtml = httpAccess.doGet(URL_PLUGINS);
            Document document = Jsoup.parse(strHtml);
            Elements trList = document.select("td a");

            for (int i = 4; i < trList.size(); i++)
            {
                Element tr = trList.get(i);
                String strComponent = tr.text().replaceAll("\\/", "");
                list.add(getComponent(strComponent));
            }
        }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving release version : "
                    + e.getMessage(), e);
        }

        Collections.sort(list);

        return list;
    }

    /**
     * Gets a component using cache feature
     *
     * @param strComponent The component name
     * @return The component
     */
    private Component getComponent(String strComponent)
    {
        Component component = (Component) getFromCache(strComponent);

        if (component == null)
        {
            component = new Component();
            component.setArtifactId(strComponent);
            component.setVersion(getVersion(URL_PLUGINS + strComponent));
            getPomInfos(component);
            putInCache(strComponent, component);
        }

        return component;
    }

    /**
     * Fill component infos coming from the pom
     *
     * @param component The component name
     */
    private void getPomInfos(Component component)
    {
        StringBuilder sbPomUrl = new StringBuilder(URL_PLUGINS);
        sbPomUrl.append(component.getArtifactId()).append("/").append(component.getVersion()).append("/");
        sbPomUrl.append(component.getArtifactId()).append("-").append(component.getVersion()).append(".pom");
        getPomInfos( component, sbPomUrl.toString() , false );
        
        String strSnapshotPomUrl = getSnapshotPomUrl( component );
        getPomInfos( component, strSnapshotPomUrl , true );

    }

    private void getPomInfos(Component component, String strPomUrl , boolean bSnapshot )
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strPom = httpAccess.doGet( strPomUrl );
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            PomHandler handler = new PomHandler();
            saxParser.parse(new InputSource(new StringReader(strPom)), handler);
            if( bSnapshot )
            {
                component.setSnapshotParentPomVersion(handler.getParentPomVersion());
                component.setSnapshotCoreVersion(handler.getCoreVersion());
            }
            else
            {
                component.setParentPomVersion(handler.getParentPomVersion());
                component.setCoreVersion(handler.getCoreVersion());
            }
            component.setJiraKey(handler.getJiraKey());
        }
        catch (IOException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving pom infos : " + e.getMessage(), e);
        }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving pom infos : " + e.getMessage(), e);
        }
        catch (ParserConfigurationException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving pom infos : " + e.getMessage(), e);
        }
        catch (SAXException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving pom infos : " + e.getMessage(), e);
        }

    }

    private String getSnapshotPomUrl(Component component)
    {
        String strSnapshotsDirUrl = URL_SNAPSHOT_PLUGINS + component.getArtifactId();
        System.out.println( "strSnapshotsDirUrl : " + strSnapshotsDirUrl);
        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strHtml = httpAccess.doGet(strSnapshotsDirUrl);
            Document document = Jsoup.parse(strHtml);
            Elements trList = document.select("td a");
            Element tr = trList.last();
            String strSnapshotVersion = tr.text().replaceAll("\\/", "");
            component.setSnapshotVersion(strSnapshotVersion);
            String strLastSnapshotDirUrl = strSnapshotsDirUrl + "/" + strSnapshotVersion;
            System.out.println( "strLastSnapshotDirUrl : " + strLastSnapshotDirUrl);
            strHtml = httpAccess.doGet(strLastSnapshotDirUrl);
            document = Jsoup.parse(strHtml);
            trList = document.select("td a");
            for( Element e : trList )
            {
                String strFilename = e.text().replaceAll("\\/", "");
                if( strFilename.endsWith( ".pom" ))
                {
                    System.out.println( "POM Url : " + strLastSnapshotDirUrl + "/" + strFilename );
                    return strLastSnapshotDirUrl + "/" + strFilename;
                }
            }
       }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving release version : "
                    + e.getMessage(), e);
        }
        AppLogService.info( "No snapshot pom found for plugin : " + component.getArtifactId() );
        return "";
    }
}

