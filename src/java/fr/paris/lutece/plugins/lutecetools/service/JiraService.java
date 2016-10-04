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

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.VersionRestClient;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.VersionRelatedIssuesCount;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.lang.StringUtils;

/**
 * JIRA Service name
 */
public class JiraService implements ComponentInfoFiller
{
    private static final String URL_JIRA_SERVER = "http://dev.lutece.paris.fr/jira/";
    private static final String URL_API_VERSION = URL_JIRA_SERVER + "rest/api/2/version/";
    private static final String SERVICE_NAME = "JIRA Info filler service registered";
    private static final int JIRAKEY_ERROR_MISSING = 1;
    private static final int JIRAKEY_ERROR_INVALID = 2;

    private static AsynchronousJiraRestClientFactory _factory;
    private static AnonymousAuthenticationHandler _auth;

    /**
     * Constructor
     */
    public JiraService( )
    {

        String strProxyHost = AppPropertiesService.getProperty( "httpAccess.proxyHost" );
        String strProxyPort = AppPropertiesService.getProperty( "httpAccess.proxyPort" );
        String strProxyUserName = AppPropertiesService.getProperty( "httpAccess.proxyUserName" );
        String strProxyPassword = AppPropertiesService.getProperty( "httpAccess.proxyPassword" );
        
        
        if ( !StringUtils.isEmpty( strProxyHost ) )
        {
            System.getProperties( ).put( "https.proxyHost", strProxyHost );
            System.getProperties( ).put( "https.proxyPort", strProxyPort );
            System.getProperties( ).put( "https.proxyUser", strProxyUserName );
            System.getProperties( ).put( "https.proxyPassword", strProxyPassword );
            System.getProperties( ).put( "https.proxySet", "true");
            AppLogService.info( "LuteceTools : Using httpaccess.properties defined proxy to connect to JIRA." );
        }
        
        _factory = new AsynchronousJiraRestClientFactory( );
        _auth = new AnonymousAuthenticationHandler( );
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void fill( Component component, StringBuilder sbLogs )
    {
        JiraRestClient client = null;
        String strJiraKey = component.getJiraKey( );

        if ( strJiraKey != null )
        {
            try
            {
                client = _factory.create( new URI( URL_JIRA_SERVER ), _auth );

                Project project = client.getProjectClient( ).getProject( strJiraKey ).claim( );
                Version versionLastReleased = null;
                Version versionLastUnreleased = null;
                for ( Version version : project.getVersions( ) )
                {
                    if ( !version.isReleased( ) )
                    {
                        versionLastUnreleased = version;
                    }
                    else
                    {
                        versionLastReleased = version;
                    }
                }
                if ( versionLastReleased != null )
                {
                    component.setJiraLastReleasedVersion( versionLastReleased.getName( ) );
                }
                if ( versionLastUnreleased != null )
                {
                    component.setJiraLastUnreleasedVersion( versionLastUnreleased.getName( ) );
                    String strURI = URL_API_VERSION + versionLastUnreleased.getId( );
                    URI uriVersion = new URI( strURI );
                    VersionRestClient clientVersion = client.getVersionRestClient( );
                    int nUnresolvedIssues = clientVersion.getNumUnresolvedIssues( uriVersion ).claim( );
                    component.setJiraUnresolvedIssuesCount( nUnresolvedIssues );
                    VersionRelatedIssuesCount vRelatedIssues = clientVersion.getVersionRelatedIssuesCount( uriVersion ).claim( );
                    component.setJiraIssuesCount( vRelatedIssues.getNumFixedIssues( ) );
                    if ( AppLogService.isDebugEnabled( ) )
                    {
                        StringBuilder sbDebug = new StringBuilder( );
                        sbDebug.append( "LuteceTools : JiraService - Project " ).append( strJiraKey ).append( " AffectedIssues : " )
                                .append( vRelatedIssues.getNumAffectedIssues( ) ).append( " FixedIssues : " ).append( vRelatedIssues.getNumFixedIssues( ) )
                                .append( " Unresolved : " ).append( nUnresolvedIssues );
                        AppLogService.debug( sbDebug.toString( ) );
                    }
                }
                component.setJiraStatus( getJiraStatus( component ) );
                component.setJiraErrors( getJiraErrors( component ) );
            }
            catch( RestClientException ex )
            {
                component.setJiraKeyError( JIRAKEY_ERROR_INVALID );
                sbLogs.append( "\n*** ERROR *** Invalid Jira Key '" ).append( strJiraKey ).append( " for component " ).append( component.getArtifactId( ) );
            }

            catch( Exception ex )
            {
                sbLogs.append( "\n*** ERROR *** Error getting Jira Infos for Key : '" ).append( strJiraKey ).append( "' : " ).append( ex.getMessage( ) )
                        .append( " for component " ).append( component.getArtifactId( ) );
            }
            finally
            {
                if ( client != null )
                {
                    try
                    {
                        client.close( );
                    }
                    catch( IOException ex )
                    {
                        AppLogService.error( "LuteceTools : Error using Jira Client API : " + ex.getMessage( ), ex );
                    }
                }
            }
        }
        else
        {
            component.setJiraKeyError( JIRAKEY_ERROR_MISSING );
            sbLogs.append( "\n*** ERROR *** Error no Jira key defined for component " ).append( component.getArtifactId( ) );
        }

    }

    /**
     * Returns Jira Errors
     * 
     * @param component
     *            The component
     * @return The errors
     */
    public String getJiraErrors( Component component )
    {
        StringBuilder sbErrors = new StringBuilder( );

        if ( ( component.getVersion( ) != null ) && !component.getVersion( ).equals( component.getJiraLastReleasedVersion( ) ) )
        {
            sbErrors.append( "Last Jira released version is not matching the last version in maven repository. \n" );
        }
        if ( ( component.getSnapshotVersion( ) != null ) && ( component.getJiraLastUnreleasedVersion( ) != null )
                && !component.getSnapshotVersion( ).startsWith( component.getJiraLastUnreleasedVersion( ) ) )
        {
            sbErrors.append( "Current Jira roadmap version is not matching current snapshot version. \n" );
        }
        if ( component.getJiraKey( ) == null )
        {
            sbErrors.append( "JIRA key is missing in the pom.xml. \n" );
        }
        if ( component.getJiraKeyError( ) == JIRAKEY_ERROR_INVALID )
        {
            sbErrors.append( "JIRA key '" ).append( component.getJiraKey( ) ).append( "' is invalid. \n" );
        }

        return sbErrors.toString( );
    }

    /**
     * Gets Jira status
     * 
     * @param component
     *            The component
     * @return The status
     */
    public int getJiraStatus( Component component )
    {
        int nStatus = 0;

        if ( ( component.getVersion( ) != null ) && component.getVersion( ).equals( component.getJiraLastReleasedVersion( ) ) )
        {
            nStatus++;
        }
        if ( ( component.getSnapshotVersion( ) != null ) && ( component.getJiraLastUnreleasedVersion( ) != null )
                && component.getSnapshotVersion( ).startsWith( component.getJiraLastUnreleasedVersion( ) ) )
        {
            nStatus++;
        }

        return nStatus;
    }

}
