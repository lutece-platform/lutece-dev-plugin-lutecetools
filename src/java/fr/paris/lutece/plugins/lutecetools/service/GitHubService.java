/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GitHub Service
 */
public class GitHubService implements ComponentInfoFiller
{
    private static final String SERVICE_NAME = "GitHub Info filler service registered";
    private static final String PROPERTY_GITHUB_ACCOUNT_NAME = "lutecetools.github.account.name";
    private static final String PROPERTY_GITHUB_ACCOUNT_TOKEN = "lutecetools.github.account.token";
    private static final String PROPERTY_GITHUB_ORGANIZATIONS = "lutecetools.github.organization";
    private static final String DSKEY_PARENT_POM_VERSION = "lutecetools.site_property.globalPom.version";

    private static String _strParentPomVersion;
    private static Map<String, GHRepository> _mapRepositories;

    /**
     * Initialization
     */
    public GitHubService( )
    {
        updateGitHubRepositoriesList( );
        _strParentPomVersion = DatastoreService.getDataValue( DSKEY_PARENT_POM_VERSION, "3.0.3" );
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
     * Update repositories info from GitHub
     */
    public static void updateGitHubRepositoriesList( )
    {
        _mapRepositories = getRepositories( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void fill( Component component, StringBuilder sbLogs )
    {
        String strRepository = getGitHubRepository( component );
        if( strRepository != null )
        {
            component.setGitHubRepo( true );

            GHRepository repo = _mapRepositories.get( strRepository );

            try
            {
                component.setGitHubOwner( repo.getOwner( ).getLogin( ) );
                Map<String, GHBranch> mapBranches = repo.getBranches( );
                List<String> listBranches = new ArrayList<String>( );

                for ( String strBranch : mapBranches.keySet( ) )
                {
                    listBranches.add( strBranch );
                }

                component.setBranchesList( listBranches );
            }
            catch( Exception ex )
            {
                sbLogs.append( "\n*** ERROR *** Retrieving GitHub infos (branches , readme, ...) for component " ).append( component.getArtifactId( ) )
                        .append( " : " ).append( ex.getMessage( ) );
            }
            try
            {
                repo.getReadme( );
                component.setGitHubReadme( true );
            }
            catch( Exception e )
            {
                if ( e instanceof FileNotFoundException )
                {
                    component.setGitHubReadme( false );
                }
            }
            try
            {
                List<GHPullRequest> prs = repo.getPullRequests( GHIssueState.OPEN );
                component.setGitHubPullRequests( prs.size( ) );
                long oldest = Long.MAX_VALUE;
                for ( GHPullRequest pr : prs )
                {
                    if ( pr.getUpdatedAt( ).getTime( ) < oldest )
                    {
                        oldest = pr.getUpdatedAt( ).getTime( );
                    }
                }
                component.setOldestPullRequest( oldest );
            }
            catch( IOException e )
            {
                sbLogs.append( "\n*** ERROR *** Retreiving Github pull requests for component " ).append( component.getArtifactId( ) ).append( " : " )
                        .append( e.getMessage( ) );
            }
            fillGitHubStatus( component );
            fillGitHubErrors( component );
        }
    }

    private String getGitHubRepository( Component component )
    {
        if ( _mapRepositories != null )
        {
            for ( String strRepository : _mapRepositories.keySet( ) )
            {
                if ( strRepository.endsWith( component.getArtifactId( ) ) )
                {
                    return strRepository;
                }
            }
        }
        return null;
        
    }
            
    
    
    /**
     * Gets all repositories of a given organization
     *
     * @return A map that contains repositories
     */
    static Map<String, GHRepository> getRepositories( )
    {
        String strOrganizations = AppPropertiesService.getProperty( PROPERTY_GITHUB_ORGANIZATIONS );

        String [ ] organizations = strOrganizations.split( "," );

        Map<String, GHRepository> mapRepositories = new ConcurrentHashMap<String, GHRepository>( );

        for ( String strOrganization : organizations )
        {
            strOrganization = strOrganization.trim( );
            try
            {
                GitHub github = getGitHub( );
                GHOrganization organization = github.getOrganization( strOrganization );
                mapRepositories.putAll( organization.getRepositories( ) );
                int nSize = organization.getRepositories( ).size( );
                AppLogService.info( "LuteceTools : GitHub Service initialized - " + nSize + " repositories found for organization " + strOrganization );
            }
            catch( IOException ex )
            {
                AppLogService.error( "LuteceTools : Unable to access GitHub repositories", ex );
            }
        }
        return mapRepositories;
    }

    /**
     * Gets a GitHub object to request repositories
     * @return GitHub object
     * @throws IOException if an exception occurs
     */
    private static GitHub getGitHub( ) throws IOException
    {
        GitHub github;

        String strAccount = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_NAME );
        String strToken = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_TOKEN );
        String strProxyHost = AppPropertiesService.getProperty( "httpAccess.proxyHost" );
        int nProxyPort = AppPropertiesService.getPropertyInt( "httpAccess.proxyPort", 80 );
        if ( !StringUtils.isEmpty( strProxyHost ) )
        {
            GitHubBuilder builder = new GitHubBuilder( );
            SocketAddress address = new InetSocketAddress( strProxyHost, nProxyPort );
            Proxy proxy = new Proxy( Proxy.Type.HTTP, address );
            builder.withProxy( proxy );
            builder.withOAuthToken( strToken, strAccount );
            github = builder.build( );
            AppLogService.info( "LuteceTools : Using httpaccess.properties defined proxy to connect to GitHub." );
        }
        else
        {
            github = GitHub.connect( strAccount, strToken );
        }

        return github;

    }

    /**
     * Returns GitHub errors
     *
     * @param component The component
     */
    private void fillGitHubErrors( Component component )
    {
        StringBuilder sbErrors = new StringBuilder( "" );

        if ( component.getGitHubRepo( ) )
        {
            if ( !component.getScmUrl( ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the released POM. \n" );
            }

            if ( !component.getSnapshotScmUrl( ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the snapshot POM. \n" );
            }

            if ( !_strParentPomVersion.equals( component.getParentPomVersion( ) ) )
            {
                sbErrors.append( "Bad parent POM in release POM. should be global-pom version " ).append( _strParentPomVersion ).append( '\n' );
            }

            if ( !_strParentPomVersion.equals( component.getSnapshotParentPomVersion( ) ) )
            {
                sbErrors.append( "Bad parent POM in snapshot POM. should be global-pom version " ).append( _strParentPomVersion ).append( '\n' );
            }

            if ( ( component.getBranchesList( ) != null ) && ( !component.getBranchesList( ).contains( "develop" ) ) )
            {
                sbErrors.append( "Branch 'develop' is missing. \n" );
            }
        }

        component.setGitHubErrors( sbErrors.toString( ) );
    }

    /**
     * Calculate GitHub status
     *
     * @param component  The component
     */
    private void fillGitHubStatus( Component component )
    {
        int nStatus = 0;

        if ( component.getGitHubRepo( ) )
        {
            nStatus++;
        }

        if ( component.getScmUrl( ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( component.getSnapshotScmUrl( ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( ( component.getBranchesList( ) != null ) && ( component.getBranchesList( ).contains( "develop" ) ) )
        {
            nStatus++;
        }
        component.setGitHubStatus( nStatus );
    }

}
