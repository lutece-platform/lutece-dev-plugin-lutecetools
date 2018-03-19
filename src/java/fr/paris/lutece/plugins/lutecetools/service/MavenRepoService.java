/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
import fr.paris.lutece.plugins.lutecetools.service.version.VersionUtils;
import fr.paris.lutece.plugins.lutecetools.web.rs.Constants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
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
public final class MavenRepoService
{
    private static final String PROPERTY_MAVEN_REPO_URL = "lutecetools.maven.repository.url";
    private static final String URL_MAVEN_REPO = AppPropertiesService.getProperty( PROPERTY_MAVEN_REPO_URL );
    private static final String PROPERTY_MAVEN_PATH_PLUGINS = "lutecetools.maven.repository.path.plugins";
    private static final String URL_PLUGINS = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_PLUGINS );
    private static final String PROPERTY_MAVEN_PATH_SITE_POM = "lutecetools.maven.repository.path.site-pom";
    private static final String URL_SITE_POM = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_SITE_POM );
    private static final String PROPERTY_MAVEN_PATH_CORE = "lutecetools.maven.repository.path.core";
    private static final String PROPERTY_MAVEN_PATH_THEMES = "lutecetools.maven.repository.path.themes";

    private static final String URL_CORE = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_CORE );
    private static final String URL_THEMES = URL_MAVEN_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_THEMES );

    private static final String KEY_SITE_POM_VERSION = "lutecetools.pom.site.version";
    private static final String RELEASE_NOT_FOUND = "Release not found";

    // SNAPSHOT
    private static final String PROPERTY_SNAPSHOT_REPO_URL = "lutecetools.snapshot.repository.url";
    private static final String URL_SNAPSHOT_REPO = AppPropertiesService.getProperty( PROPERTY_SNAPSHOT_REPO_URL );
    private static final String URL_SNAPSHOT_PLUGINS = URL_SNAPSHOT_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_PLUGINS );
    private static final String URL_SNAPSHOT_CORE = URL_SNAPSHOT_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_CORE );
    private static final String URL_SNAPSHOT_THEMES = URL_SNAPSHOT_REPO + AppPropertiesService.getProperty( PROPERTY_MAVEN_PATH_THEMES );

    private static final String EXCEPTION_MESSAGE = "LuteceTools - MavenRepoService : Error retrieving pom infos : ";
    private static final String PROPERTY_NON_AVAILABLE = "lutecetools.nonAvailable";
    private static final String NON_AVAILABLE = AppPropertiesService.getProperty( PROPERTY_NON_AVAILABLE );
    private static final String PROPERTY_UPDATE_DELAY = "lutecetools.update.delay";
    private static final long DEFAULT_UPDATE_DELAY = 7200000L; // 2 hours
    private static final long UPDATE_DELAY = AppPropertiesService.getPropertyLong( PROPERTY_UPDATE_DELAY, DEFAULT_UPDATE_DELAY );

    // Keys
    private static final String KEY_NCLOC = "ncloc";
    private static final String KEY_SQALE_DEBT_RATIO = "sqale_debt_ratio";

    // Tags
    private static final String TAG_LUTECE_CORE = "lutece-core";

    private static MavenRepoService _singleton;
    private static StringBuilder _sbLogs = new StringBuilder( );
    private static List<ComponentInfoFiller> _listComponentFiller = new ArrayList<>( );

    /**
     * Private constructor
     */
    private MavenRepoService( )
    {
    }

    /**
     * Returns the unique instance
     * 
     * @return the unique instance
     */
    public static synchronized MavenRepoService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new MavenRepoService( );
            _listComponentFiller = SpringContextService.getBeansOfType( ComponentInfoFiller.class );
            AppLogService.info( "Lutecetools : registering info fillers" );
            for ( ComponentInfoFiller filler : _listComponentFiller )
            {
                AppLogService.info( " * " + filler.getName( ) );
            }
        }

        return _singleton;
    }

    /**
     * Set the component's version
     * 
     * @param component
     *            The component
     */
    public static void setReleaseVersion( Dependency component )
    {
        component.setVersion( getVersion( URL_PLUGINS + component.getArtifactId( ) ) );
    }

    /**
     * Set the POM site version
     */
    public static void setPomSiteVersion( )
    {
        String strVersion = getVersion( URL_SITE_POM );
        DatastoreService.setDataValue( KEY_SITE_POM_VERSION, strVersion );
    }

    /**
     * Retrieve a version from the maven repository
     * 
     * @param strUrl
     *            The maven repository URL
     * @return The version
     */
    public static String getVersion( String strUrl )
    {
        String strVersion = RELEASE_NOT_FOUND;

        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strHtml = httpAccess.doGet( strUrl );
            List<String> listElement = getAnchorsList( strHtml );
            List<String> listVersions = new ArrayList<String>( );
            for ( String strAnchor : listElement )
            {
                if ( strAnchor.matches( "^[\\d].*" ) )
                {
                    listVersions.add( strAnchor );
                }
            }

            return VersionUtils.getLatestVersion( listVersions );
        }
        catch( HttpAccessException e )
        {
            AppLogService.error( "LuteceTools - MavenRepoService : Error retrieving release version : " + e.getMessage( ), e );
        }

        return strVersion;
    }

    /**
     * Gets the component list
     * 
     * @return the component list
     */
    public ComponentsInfos getComponents( )
    {
        ComponentsInfos ciInfos = new ComponentsInfos( );
        List<Component> list = new ArrayList<>( );
        List<String> listComponents = getComponentsListFromRepository( );
        int nCount = 0;
        int nAvailable = 0;

        for ( String strArtifactId : listComponents )
        {
            Component component = getComponent( strArtifactId, false );
            list.add( component );
            nCount++;

            if ( !NON_AVAILABLE.equals( component.getVersion( ) ) )
            {
                nAvailable++;
            }
            else
            {
                AppLogService.info( "Component not processed : " + component.getArtifactId() );
            }
        }
        Collections.sort( list );

        ciInfos.setComponentCount( nCount );
        ciInfos.setComponentAvailable( nAvailable );
        ciInfos.setListComponents( list );

        return ciInfos;
    }

    /**
     * Get the components list
     * 
     * @return The list
     */
    public static List<String> getComponentsListFromRepository( )
    {
        List<String> list = new ArrayList<>( );

        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strHtml = httpAccess.doGet( URL_PLUGINS );
            list = getAnchorsList( strHtml );

            // remove the 4 first links
            // list.remove( 3 );
            // list.remove( 2 );
            // list.remove( 1 );
            list.remove( 0 );

            // add lutece-core
            list.add( 0, TAG_LUTECE_CORE );
        }
        catch( HttpAccessException e )
        {
            AppLogService.error( "LuteceTools - MavenRepoService : Error retrieving release version : " + e.getMessage( ), e );
        }

        return list;
    }

    /**
     * Gets a component using cache feature
     * 
     * @param strArtifactId
     *            The component name
     * @param bFetch
     * @return The component
     */
    public static Component getComponent( String strArtifactId, boolean bFetch )
    {
        return getComponent( strArtifactId, bFetch, false );
    }

    /**
     * Gets a component using cache feature
     * 
     * @param strArtifactId
     *            The component name
     * @param bFetch
     * @param bForceReload
     * @return The component
     */
    public static Component getComponent( String strArtifactId, boolean bFetch, boolean bForceReload )
    {
        return getComponent( strArtifactId, bFetch, bForceReload, null );
    }

    /**
     * Gets a component
     * 
     * @param strArtifactId
     * @param bFetch
     * @param bForceReload
     * @param strType
     *            the component type
     * @return
     */
    public static Component getComponent( String strArtifactId, boolean bFetch, boolean bForceReload, String strType )
    {

        Component component = bForceReload ? null : ComponentService.load( strArtifactId );

        if ( component == null )
        {
            if ( bFetch )
            {
                StringBuilder sbLogs = new StringBuilder( );
                component = fetchComponent( strArtifactId, strType, sbLogs );
                ComponentService.save( component );
            }
            else
            {
                component = new Component( );
                component.setArtifactId( strArtifactId );
                component.set( Component.CORE_VERSION, NON_AVAILABLE );
                component.set( Component.PARENT_POM_VERSION, NON_AVAILABLE );
                component.set( Component.SCM_URL, NON_AVAILABLE );
                component.set( Component.SNAPSHOT_VERSION, NON_AVAILABLE );
                component.set( Component.SNAPSHOT_CORE_VERSION, NON_AVAILABLE );
                component.set( Component.SNAPSHOT_PARENT_POM_VERSION, NON_AVAILABLE );
                component.set( Component.JIRA_KEY, NON_AVAILABLE );
                component.setVersion( NON_AVAILABLE );
                component.set( Component.IS_GIT_REPO, false );
            }
        }

        return component;

    }

    /**
     * Fetch the component from the Maven repository
     * 
     * @param strArtifactId
     *            The Artifact ID
     * 
     * @param strType
     *            the component type
     * @param sbLogs
     *            Logs
     * @return The component
     */
    private static Component fetchComponent( String strArtifactId, String strType, StringBuilder sbLogs )
    {
        Component component = new Component( );
        component.setArtifactId( strArtifactId );
        if ( Constants.MAVEN_REPO_LUTECE_CORE.equals( getMavenRepoDirectoryType( strArtifactId, strType ) ) )
        {
            component.setVersion( getVersion( URL_CORE ) );
        }
        else  if ( Constants.MAVEN_REPO_LUTECE_SITE.equals( getMavenRepoDirectoryType( strArtifactId, strType ) ) )
        {
            component.setVersion( getVersion( URL_THEMES + strArtifactId ) );
        }
        else
        {
            component.setVersion( getVersion( URL_PLUGINS + strArtifactId ) );
        }

        long lTime1 = new Date( ).getTime( );
        getPomInfos( component, strType, sbLogs );

        long lTime2 = new Date( ).getTime( );
        sbLogs.append( "\nLutece Tools - Fetching Maven Info for '" ).append( component.getArtifactId( ) ).append( "' - duration : " ).append( lTime2 - lTime1 )
                .append( "ms." );

        for ( ComponentInfoFiller filler : _listComponentFiller )
        {
            filler.fill( component, sbLogs );
        }

        return component;
    }

    /**
     * Fill component infos coming from the pom
     * 
     * @param component
     *            The component name
     * @param sbLogs
     *            Logs
     */
    private static void getPomInfos( Component component, String strType, StringBuilder sbLogs )
    {
        StringBuilder sbPomUrl;

        if ( !RELEASE_NOT_FOUND.equals( component.getVersion( ) ) )
        {
            if ( Constants.MAVEN_REPO_LUTECE_CORE.equals( getMavenRepoDirectoryType( component.getArtifactId( ), strType ) ) )
            {
                sbPomUrl = new StringBuilder( URL_CORE );
                sbPomUrl.append( component.getVersion( ) ).append( '/' );
            }
            else
                if ( Constants.MAVEN_REPO_LUTECE_SITE.equals( getMavenRepoDirectoryType( component.getArtifactId( ), strType ) ) )
                {
                    sbPomUrl = new StringBuilder( URL_THEMES );
                    sbPomUrl.append( component.getArtifactId( ) ).append( '/' ).append( component.getVersion( ) ).append( '/' );

                }
                else
                {
                    sbPomUrl = new StringBuilder( URL_PLUGINS );
                    sbPomUrl.append( component.getArtifactId( ) ).append( '/' ).append( component.getVersion( ) ).append( '/' );
                }

            sbPomUrl.append( component.getArtifactId( ) ).append( '-' ).append( component.getVersion( ) ).append( ".pom" );
            getPomInfos( component, sbPomUrl.toString( ), false, sbLogs );
        }
        String strSnapshotPomUrl = getSnapshotPomUrl( component, sbLogs, strType );

        if ( strSnapshotPomUrl != null )
        {
            getPomInfos( component, strSnapshotPomUrl, true, sbLogs );
        }
        else
        {
            sbLogs.append( "\n*** ERROR *** No snapshot pom found for plugin : " ).append( component.getArtifactId( ) );
        }
    }

    /**
     * Retreive POM infos for a given component
     * 
     * @param component
     *            The component
     * @param strPomUrl
     *            The POM URL
     * @param bSnapshot
     *            false for release, true for snapshot
     * @param sbLogs
     *            Logs
     */
    private static void getPomInfos( Component component, String strPomUrl, boolean bSnapshot, StringBuilder sbLogs )
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strPom = httpAccess.doGet( strPomUrl );
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance( );
            SAXParser saxParser = saxParserFactory.newSAXParser( );
            SaxPomHandler handler = new SaxPomHandler( );
            saxParser.parse( new InputSource( new StringReader( strPom ) ), handler );

            if ( bSnapshot )
            {
                component.set( Component.SNAPSHOT_PARENT_POM_VERSION, handler.getParentPomVersion( ) );
                component.set( Component.SNAPSHOT_CORE_VERSION, handler.getCoreVersion( ) );
                component.set( Component.SNAPSHOT_SCM_URL, handler.getScmUrl( ) );
            }
            else
            {
                component.set( Component.SNAPSHOT_PARENT_POM_VERSION, handler.getParentPomVersion( ) );
                component.set( Component.CORE_VERSION, handler.getCoreVersion( ) );
                component.set( Component.SCM_URL, handler.getScmUrl( ) );
            }
            component.set( Component.SCM_CONNECTION, handler.getScmConnection( ) );
            component.set( Component.SCM_DEVELOPER_CONNECTION, handler.getScmDeveloperConnection( ) );
            component.set( Component.JIRA_KEY, handler.getJiraKey( ) );
        }
        catch( IOException e )
        {
            AppLogService.error( EXCEPTION_MESSAGE + e.getMessage( ), e );
        }
        catch( HttpAccessException e )
        {
            sbLogs.append( "\n*** ERROR *** Error reading pom for component " ).append( component.getArtifactId( ) ).append( EXCEPTION_MESSAGE )
                    .append( e.getMessage( ) );
        }
        catch( ParserConfigurationException e )
        {
            AppLogService.error( EXCEPTION_MESSAGE + e.getMessage( ), e );
        }
        catch( SAXException e )
        {
            AppLogService.error( EXCEPTION_MESSAGE + e.getMessage( ), e );
        }
    }

    /**
     * Retrieve the POM URL for the latest snapshot
     * 
     * @param component
     *            The component
     * @param sbLogs
     *            The logs
     * @return The URL
     */
    private static String getSnapshotPomUrl( Component component, StringBuilder sbLogs, String strType )
    {
        String strPomUrl = null;
        String strSnapshotsDirUrl;

        if ( Constants.MAVEN_REPO_LUTECE_CORE.equals( getMavenRepoDirectoryType( component.getArtifactId( ), strType ) ) )
        {

            strSnapshotsDirUrl = URL_SNAPSHOT_CORE;
        }
        else
            if ( Constants.MAVEN_REPO_LUTECE_SITE.equals( getMavenRepoDirectoryType( component.getArtifactId( ), strType ) ) )
            {
                strSnapshotsDirUrl = URL_SNAPSHOT_THEMES + component.getArtifactId( );

            }
            else
            {
                strSnapshotsDirUrl = URL_SNAPSHOT_PLUGINS + component.getArtifactId( );
            }

        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strHtml = httpAccess.doGet( strSnapshotsDirUrl );
            List<String> listElement = getAnchorsList( strHtml );
            List<String> listVersions = new ArrayList<String>( );

            for ( String strAnchor : listElement )
            {
                if ( strAnchor.matches( "^[\\d].*" ) )
                {
                    listVersions.add( strAnchor );
                }
            }

            String strSnapshotVersion = VersionUtils.getLatestVersion( listVersions );
            component.set( Component.SNAPSHOT_VERSION, strSnapshotVersion );

            String strLastSnapshotDirUrl = strSnapshotsDirUrl + "/" + strSnapshotVersion;
            strHtml = httpAccess.doGet( strLastSnapshotDirUrl );
            listElement = getAnchorsList( strHtml );

            for ( String strFilename : listElement )
            {
                if ( strFilename.endsWith( ".pom" ) )
                {
                    strPomUrl = strLastSnapshotDirUrl + "/" + strFilename;
                }
            }
        }
        catch( HttpAccessException e )
        {
            sbLogs.append( "\n*** ERROR ***  Error retrieving release version : " ).append( e.getMessage( ) );
        }

        return strPomUrl;
    }

    /**
     * Gets anchor list using regexp
     * 
     * @param strHtml
     *            The HTML code
     * @return The list
     */
    static List<String> getAnchorsList2( String strHtml )
    {
        List<String> list = new ArrayList<String>( );
        String strPattern = "<a[^>]*>(.+?)</a>";
        Pattern pattern = Pattern.compile( strPattern, Pattern.DOTALL );
        Matcher matcher = pattern.matcher( strHtml );

        while ( matcher.find( ) )
        {
            list.add( strHtml.substring( matcher.start( ), matcher.end( ) ) );
        }

        return list;
    }

    /**
     * Gets anchor list using more optimized method
     * 
     * @param strHtml
     *            The HTML code
     * @return The list
     */
    private static List<String> getAnchorsList( String strHtml )
    {
        List<String> list = new ArrayList<String>( );
        String strCurrent = strHtml;

        int nPos = strCurrent.indexOf( "<a " );

        while ( nPos > 0 )
        {
            strCurrent = strCurrent.substring( nPos );

            int nEndTag = strCurrent.indexOf( ">" );
            int nTagEnd = strCurrent.indexOf( "</a>" );
            list.add( strCurrent.substring( nEndTag + 1, nTagEnd ).replaceAll( "\\/", "" ) );
            strCurrent = strCurrent.substring( nTagEnd + 4 );
            nPos = strCurrent.indexOf( "<a " );
        }

        return list;
    }

    private static String getMavenRepoDirectoryType( String strArtifactId, String strComponentType )
    {

        String strTypeRepo = null;
        if ( strComponentType != null )
        {
            switch( strComponentType )
            {
                case Constants.DEPENDENCY_TYPE_LUTECE_CORE:

                    strTypeRepo = Constants.MAVEN_REPO_LUTECE_CORE;
                    break;
                case Constants.DEPENDENCY_TYPE_LUTECE_SITE:

                    strTypeRepo = Constants.MAVEN_REPO_LUTECE_SITE;
                    break;

                default:
                    strTypeRepo = Constants.MAVEN_REPO_LUTECE_PLUGIN;
                    break;
            }
        }
        else
        {

            switch( strArtifactId )
            {
                case TAG_LUTECE_CORE:

                    strTypeRepo = Constants.MAVEN_REPO_LUTECE_CORE;
                    break;

                default:
                    strTypeRepo = Constants.MAVEN_REPO_LUTECE_PLUGIN;
                    break;
            }
        }

        return strTypeRepo;

    }

    /**
     * Update the cache (reset and rebuild)
     */
    public void updateCache( )
    {
        GitHubService.updateGitHubRepositoriesList( );

        List<String> listComponents = getComponentsListFromRepository( );

        for ( String strArtifactId : listComponents )
        {
            Component component = ComponentService.load( strArtifactId );

            if ( shouldBeUpdated( component ) )
            {
                component = fetchComponent( strArtifactId, null, _sbLogs );
                ComponentService.save( component );
            }
            else
            {
                _sbLogs.append( "\nComponent " ).append( strArtifactId ).append( " is up to date" );
            }
        }
    }

    public static String getLogs( )
    {
        return _sbLogs.toString( );
    }

    public static void clearLogs( )
    {
        _sbLogs = new StringBuilder( );
    }

    /**
     * Returns true if the component should be updated
     * 
     * @param component
     *            The component
     * @return true if the component should be updated
     */
    private boolean shouldBeUpdated( Component component )
    {
        // The component is missing
        if ( component == null )
        {
            return true;
        }

        // The last update is too far
        long lNow = new Date( ).getTime( );

        return ( lNow - component.getLastUpdate( ) ) > UPDATE_DELAY;
    }

    public static String getLatestCoreVersion( )
    {
        return getVersion( URL_CORE );
    }

}
