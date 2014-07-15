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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Version Service
 */
public final class MavenRepoService extends AbstractCacheableService
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
    private static final String CACHE_SERVICE_NAME = "LuteceToolsMavenCacheService";

    // SNAPSHOT
    private static final String PROPERTY_SNAPSHOT_REPO_URL = "lutecetools.snapshot.repository.url";
    private static final String URL_SNAPSHOT_REPO = AppPropertiesService.getProperty(PROPERTY_SNAPSHOT_REPO_URL);
    private static final String URL_SNAPSHOT_PLUGINS = URL_SNAPSHOT_REPO
            + AppPropertiesService.getProperty(PROPERTY_MAVEN_PATH_PLUGINS);
    private static final String EXCEPTION_MESSAGE = "LuteceTools - MavenRepoService : Error retrieving pom infos : ";
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
    public static synchronized MavenRepoService instance()
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
            List<String> listElement = getAnchorsList(strHtml);

            for( String strAnchor : listElement )
            {
                if( strAnchor.matches( "^[\\d].*"))
                {
                    strVersion = strAnchor;
                }
            }
            return strVersion;
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
    public List<Component> getComponents()
    {
        List<Component> list = new ArrayList<Component>();

        for (String strArtifactId : getComponentsList())
        {
            list.add(getComponent(strArtifactId));
        }

        Collections.sort(list);

        return list;
    }

    public static List<String> getComponentsList()
    {
        List<String> list = new ArrayList();
        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strHtml = httpAccess.doGet(URL_PLUGINS);
            list = getAnchorsList(strHtml);

            // remove the 4 first links
            list.remove(3);
            list.remove(2);
            list.remove(1);
            list.remove(0);

        }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving release version : "
                    + e.getMessage(), e);
        }
        return list;

    }

    /**
     * Gets a component using cache feature
     *
     * @param strArtifactId The component name
     * @return The component
     */
    public Component getComponent(String strArtifactId)
    {
        Component component = (Component) getFromCache(strArtifactId);

        if (component == null)
        {
            component = new Component();
            component.setArtifactId(strArtifactId);
            component.setVersion(getVersion(URL_PLUGINS + strArtifactId));

            long t1 = new Date().getTime();
            getPomInfos(component);

            long t2 = new Date().getTime();
            AppLogService.debug("Lutece Tools - Fetching Maven Info for '" + component.getArtifactId()
                    + "' - duration : " + (t2 - t1 + "ms"));
            putInCache(strArtifactId, component);
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
        getPomInfos(component, sbPomUrl.toString(), false);

        String strSnapshotPomUrl = getSnapshotPomUrl(component);

        if (strSnapshotPomUrl != null)
        {
            getPomInfos(component, strSnapshotPomUrl, true);
        }
        else
        {
            AppLogService.info("No snapshot pom found for plugin : " + component.getArtifactId());
        }
    }

    /**
     * Retreive POM infos for a given component
     *
     * @param component The component
     * @param strPomUrl The POM URL
     * @param bSnapshot false for release, true for snapshot
     */
    private void getPomInfos(Component component, String strPomUrl, boolean bSnapshot)
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strPom = httpAccess.doGet(strPomUrl);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SaxPomHandler handler = new SaxPomHandler();
            saxParser.parse(new InputSource(new StringReader(strPom)), handler);

            if (bSnapshot)
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
            AppLogService.error(EXCEPTION_MESSAGE + e.getMessage(), e);
        }
        catch (HttpAccessException e)
        {
            AppLogService.error(EXCEPTION_MESSAGE + e.getMessage(), e);
        }
        catch (ParserConfigurationException e)
        {
            AppLogService.error(EXCEPTION_MESSAGE + e.getMessage(), e);
        }
        catch (SAXException e)
        {
            AppLogService.error(EXCEPTION_MESSAGE + e.getMessage(), e);
        }
    }

    /**
     * Retrieve the POM URL for the latest snapshot
     *
     * @param component THe component
     * @return The URL
     */
    private String getSnapshotPomUrl(Component component)
    {
        String strPomUrl = null;
        String strSnapshotsDirUrl = URL_SNAPSHOT_PLUGINS + component.getArtifactId();

        try
        {
            HttpAccess httpAccess = new HttpAccess();
            String strHtml = httpAccess.doGet(strSnapshotsDirUrl);
            List<String> listElement = getAnchorsList(strHtml);
            String strSnapshotVersion = listElement.get(listElement.size() - 1);
            component.setSnapshotVersion(strSnapshotVersion);

            String strLastSnapshotDirUrl = strSnapshotsDirUrl + "/" + strSnapshotVersion;
            strHtml = httpAccess.doGet(strLastSnapshotDirUrl);
            listElement = getAnchorsList(strHtml);

            for (String strFilename : listElement)
            {
                if (strFilename.endsWith(".pom"))
                {
                    strPomUrl = strLastSnapshotDirUrl + "/" + strFilename;
                }
            }
        }
        catch (HttpAccessException e)
        {
            AppLogService.error("LuteceTools - MavenRepoService : Error retrieving release version : "
                    + e.getMessage(), e);
        }

        return strPomUrl;
    }

    /**
     * Gets anchor list using regexp
     *
     * @param strHtml The HTML code
     * @return The list
     */
    static List<String> getAnchorsList2(String strHtml)
    {
        List<String> list = new ArrayList<String>();
        String strPattern = "<a[^>]*>(.+?)</a>";
        Pattern p = Pattern.compile(strPattern, Pattern.DOTALL);
        Matcher m = p.matcher(strHtml);

        while (m.find())
        {
            list.add(strHtml.substring(m.start(), m.end()));
        }

        return list;
    }

    /**
     * Gets anchor list using more optimized method
     *
     * @param strHtml The HTML code
     * @return The list
     */
    private static List<String> getAnchorsList(String strHtml)
    {
        List<String> list = new ArrayList<String>();
        String strCurrent = strHtml;

        int nPos = strCurrent.indexOf("<a ");

        while (nPos > 0)
        {
            strCurrent = strCurrent.substring(nPos);

            int nEndTag = strCurrent.indexOf(">");
            int nTagEnd = strCurrent.indexOf("</a>");
            list.add(strCurrent.substring(nEndTag + 1, nTagEnd).replaceAll("\\/", ""));
            strCurrent = strCurrent.substring(nTagEnd + 4);
            nPos = strCurrent.indexOf("<a ");
        }

        return list;
    }

    /**
     * Update the cache (reset and rebuild)
     */
    public void updateCache()
    {
        resetCache();
        getComponents();
    }
}
