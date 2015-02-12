/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    Map<String, GHRepository> _mapRepositories;

    public static synchronized GitHubService instance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new GitHubService(  );
            _singleton.init(  );
        }

        return _singleton;
    }

    private void init(  )
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

                    /*
                    GHRepository repo = _mapRepositories.get( strRepo );
                    try
                    {
                        Map<String, GHBranch> mapBraches = repo.getBranches();
                    }
                    catch (IOException ex)
                    {
                        AppLogService.error( "Error retrieving GH branches " + ex.getMessage() , ex );
                    }
                    */
                }
            }
        }
    }

    public Map<String, GHRepository> getRepositories( String strOrganization )
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
