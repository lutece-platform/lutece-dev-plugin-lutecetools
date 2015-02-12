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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;


/**
 *
 * @author pierre
 */
public class GitHubService
{
    private static final String PROPERTY_GITHUB_ACCOUNT_NAME = "lutecetools.github.account.name";
    private static final String PROPERTY_GITHUB_ACCOUNT_TOKEN = "lutecetools.github.account.token";

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

    private static void init(  )
    {
        updateGitHubRepositoriesList();
    }
    
    public static void updateGitHubRepositoriesList()
    {
        _mapRepositories = getRepositories( "lutece-platform" );
    }

    public void setGitHubInfos( Component component )
    {
        if ( _mapRepositories != null )
        {
            for ( String strRepo : _mapRepositories.keySet(  ) )
            {
                if ( strRepo.contains( component.getArtifactId(  ) ) )
                {
                    component.setGitHubRepo( true );
                    GHRepository repo = _mapRepositories.get( strRepo );
                    try
                    {
                        Map<String, GHBranch> mapBraches = repo.getBranches();
                        List<String> listBranches = new ArrayList<String>();
                        for( String strBranch : mapBraches.keySet() )
                        {
                            listBranches.add( strBranch );
                        }
                        component.setBranchesList(listBranches);
                    }
                    catch (IOException ex)
                    {
                        AppLogService.error( "Error retrieving GH branches " + ex.getMessage() , ex );
                    }
                }
            }
        }
    }

    static Map<String, GHRepository> getRepositories( String strOrganization )
    {
        Map<String, GHRepository> mapRepositories = null;

        try
        {
            String strAccount = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_NAME );
            String strToken = AppPropertiesService.getProperty( PROPERTY_GITHUB_ACCOUNT_TOKEN );
            GitHub github = GitHub.connect( strAccount , strToken );
            GHOrganization organization = github.getOrganization( strOrganization );
            mapRepositories = organization.getRepositories(  );
            AppLogService.info( "GitHub Service initialized - " + mapRepositories.size(  ) + " repositories found." );
        }
        catch ( IOException ex )
        {
            AppLogService.error( "Unable to access GitHub repositories", ex );
        }

        return mapRepositories;
    }
}
