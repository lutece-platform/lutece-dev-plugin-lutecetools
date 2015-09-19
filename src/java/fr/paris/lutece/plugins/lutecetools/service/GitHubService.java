/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import java.io.FileNotFoundException;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * GitHub Service
 */
public class GitHubService
{
    private static final String PROPERTY_GITHUB_ACCOUNT_NAME = "lutecetools.github.account.name";
    private static final String PROPERTY_GITHUB_ACCOUNT_TOKEN = "lutecetools.github.account.token";
    private static final String PROPERTY_GITHUB_ORGANIZATIONS = "lutecetools.github.organization";
    private static GitHubService _singleton;
    private static Map<String, GHRepository> _mapRepositories;

    public static synchronized GitHubService instance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new GitHubService(  );
            init(  );
        }

        return _singleton;
    }

    /**
     * Initialization
     */
    private static void init(  )
    {
        updateGitHubRepositoriesList(  );
    }

    /**
     * Update repositories info from GitHub
     */
    public static void updateGitHubRepositoriesList(  )
    {
        _mapRepositories = getRepositories();
    }

    /**
     * Set GitHub infos to a component
     * @param component The component
     */
    public void setGitHubInfos( Component component )
    {
        if ( _mapRepositories != null )
        {
            for ( String strRepo : _mapRepositories.keySet(  ) )
            {
                if ( strRepo.endsWith(component.getArtifactId(  ) ) )
                {
                    component.setGitHubRepo( true );

                    GHRepository repo = _mapRepositories.get( strRepo );

                    try
                    {
                        component.setGitHubOwner( repo.getOwner().getLogin() );
                        Map<String, GHBranch> mapBraches = repo.getBranches(  );
                        List<String> listBranches = new ArrayList<String>(  );

                        for ( String strBranch : mapBraches.keySet(  ) )
                        {
                            listBranches.add( strBranch );
                        }

                        component.setBranchesList( listBranches );
                    }
                    catch ( Exception ex )
                    {
                        AppLogService.error( "Error retrieving GitHub infos (branches , readme, ...) " + ex.getMessage(  ), ex );
                    }
                    
                    try 
                    {
                        repo.getReadme();
                        component.setGitHubReadme( true );
                    }
                    catch( Exception e )
                    {
                        if( e instanceof FileNotFoundException )
                        {
                            component.setGitHubReadme( false );
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all repositories of a given organization
     * @return A map that contains repositories
     */
    static Map<String, GHRepository> getRepositories()
    {
        String strOrganizations = AppPropertiesService.getProperty( PROPERTY_GITHUB_ORGANIZATIONS );
        
        String[] organizations = strOrganizations.split( "," );
        
        Map<String, GHRepository> mapRepositories = new HashMap<String, GHRepository>();
        
        for( String strOrganization : organizations )
        {
            strOrganization = strOrganization.trim();
            try
            {
                String strAccount = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_NAME );
                String strToken = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_TOKEN );
                GitHub github = GitHub.connect( strAccount, strToken );
                GHOrganization organization = github.getOrganization( strOrganization );
                mapRepositories.putAll( organization.getRepositories(  ) );
                int nSize = organization.getRepositories(  ).size();
                AppLogService.info( "GitHub Service initialized - " + nSize + " repositories found for organization " + strOrganization );
            }
            catch ( IOException ex )
            {
                AppLogService.error( "Unable to access GitHub repositories", ex );
            }
        }
        return mapRepositories;
    }
    
    /**
     * Returns GitHub errors
     * @param component The components
     * @return The errors
     */
    public static String getGitHubErrors( Component component )
    {
        StringBuilder sbErrors = new StringBuilder(  );

        if ( component.getGitHubRepo(  ) )
        {
            if ( !component.getScmUrl(  ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the released POM. \n" );
            }

            if ( !component.getSnapshotScmUrl(  ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the snapshot POM. \n" );
            }

            if ( !"3.0.3".equals( component.getParentPomVersion() ) )
            {
                sbErrors.append( "Bad parent POM in release POM. should be global-pom 3.0. \n" );
            }

            if ( !"3.0.3".equals( component.getSnapshotParentPomVersion() ) )
            {
                sbErrors.append( "Bad parent POM in snapshot POM. should be global-pom 3.0. \n" );
            }

            if ( ( component.getBranchesList() != null ) && ( !component.getBranchesList().contains( "develop" ) ) )
            {
                sbErrors.append( "Branch 'develop' is missing. \n" );
            }
        }

        return sbErrors.toString(  );
    }
    
    /**
     * Calculate GitHub status
     * @param component The component
     * @return  The status
     */
    public static int getGitHubStatus( Component component )
    {
        int nStatus = 0;

        if ( component.getGitHubRepo(  ) )
        {
            nStatus++;
        }

        if ( component.getScmUrl(  ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( component.getSnapshotScmUrl(  ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( ( component.getBranchesList() != null ) && ( component.getBranchesList().contains( "develop" ) ) )
        {
            nStatus++;
        }

        return nStatus;
    }


}
