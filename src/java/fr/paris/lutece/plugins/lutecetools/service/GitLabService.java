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
package fr.paris.lutece.plugins.lutecetools.service;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.gitlab.api.models.GitlabTag;

/**
 * GitlabService
 */
public class GitLabService extends AbstractGitPlatformService
{

    private static final String SERVICE_NAME = "Gitlab Info filler service";
    private static final String PROPERTY_GITLAB_URL = "lutecetools.gitlab.url";
    private static final String PROPERTY_GITLAB_ACCOUNT_TOKEN = "lutecetools.gitlab.account.token";

    private static final String SITE_INDEX_PATH_PART1 = "/raw/develop/src/site/";
    private static final String SITE_INDEX_PATH_PART2 = "xdoc/index.xml";
    private static final String DEVELOP_BRANCH_NAME = "develop";
    private static final String STATE_ACTIVE = "active";

    private static GitlabAPI _gitLabApi;
    private static Map<String, GitlabProject> _mapRepositories;

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
        String strRepository = getGitLabRepository( component );
        if ( strRepository != null )
        {
            GitlabProject project = _mapRepositories.get( strRepository );
            component.set( Component.IS_GIT_REPO, true );
            component.set( GIT_PLATFORM, getGitPlatform( ) );
            component.set( GIT_GROUP, getGroup( project ) );

            component.set( GIT_REPO_STATUS, 4 ); // FIXME 4 = OK
            component.set( GIT_REPO_ERRORS, "" );

            incrementItemCount( );
            incrementItemOk( );

            fillCommitsInfos( component, project.getId( ) );
            fillSiteInfos( component, sbLogs );
        }

    }

    private static String getGitLabRepository( Component component )
    {
        try
        {
            if ( _mapRepositories == null )
            {
                _mapRepositories = getRepositories( );
            }
            for ( String strRepository : _mapRepositories.keySet( ) )
            {
                if ( strRepository.endsWith( component.getArtifactId( ) ) )
                {
                    return strRepository;
                }
            }

        }
        catch( IOException ex )
        {
            AppLogService.error( "GitlabService - Error getting repositories : " + ex.getMessage( ), ex );
        }
        return null;

    }

    /**
     * Fetch all repositories hosted by the platform
     *
     * @return The repositories map
     * @throws IOException
     *         if an error occurs
     */
    public static Map<String, GitlabProject> getRepositories( ) throws IOException
    {
        connectToGitlab( );
        List<GitlabProject> listProjects = _gitLabApi.getProjects( );
        AppLogService.debug( "GitlabService - fetching Gitlab repositories " + listProjects.size( ) );
        Map<String, GitlabProject> mapRepositories = new HashMap<>( );
        for ( GitlabProject project : listProjects )
        {
            String strGroup = getGroup( project );
            AppLogService.debug( "GitlabService - fetching repository : " + project.getName( ) + " group : " + strGroup );
            mapRepositories.put( project.getName( ), project );
        }
        return mapRepositories;
    }

    /**
     * Gets the group from a given GitLab project
     *
     * @param project
     *         The project
     * @return The group
     */
    static String getGroup( GitlabProject project )
    {
        String strNameWithNamespace = project.getNameWithNamespace( );

        int nPos = strNameWithNamespace.indexOf( '/' );
        if ( nPos > 0 )
        {
            return strNameWithNamespace.substring( 0, nPos );
        }
        AppLogService.error( "Error no group found for repository : " + strNameWithNamespace );

        return "";
    }

    /**
     * fill site infos from xdox site index
     *
     * @param component
     *         The component
     */
    private void fillSiteInfos( Component component, StringBuilder sbLogs )
    {
        String strScmUrl = component.get( Component.SCM_URL );
        if ( strScmUrl != null )
        {
            if ( strScmUrl.endsWith( ".git" ) )
            {
                strScmUrl = strScmUrl.substring( 0, strScmUrl.length( ) - 4 );
            }

            String strXdocSiteIndexUrl = strScmUrl + SITE_INDEX_PATH_PART1 + SITE_INDEX_PATH_PART2;
            SiteInfoService.instance( ).getSiteInfos( component, strXdocSiteIndexUrl, "en", sbLogs );

            strXdocSiteIndexUrl = strScmUrl + SITE_INDEX_PATH_PART1 + "fr/" + SITE_INDEX_PATH_PART2;
            SiteInfoService.instance( ).getSiteInfos( component, strXdocSiteIndexUrl, "fr", sbLogs );
        }
    }

    /**
     * fill the component with commits informations
     *
     * @param component The component
     * @param nProjectId the project id
     */
    private void fillCommitsInfos( Component component, Integer nProjectId ) 
    {
        try
        {
            if ( !isSessionActive( ) )
            {
                connectToGitlab( );
            }

            List<GitlabCommit> commitsList = _gitLabApi.getAllCommits( nProjectId );

            if ( CollectionUtils.isNotEmpty( commitsList ) )
            {
            	component.set( COMMITS_COUNT_SINCE_PROJECT_START, commitsList.size( ) );
            	component.set( COMMITS_COUNT_SINCE_LAST_RELEASE, getNumberOfCommitsSinceLastRelease( nProjectId ) );
                int nContributors = commitsList.stream( ).map( GitlabCommit::getAuthorName ).collect( Collectors.toSet( ) ).size( );
                component.set( CONTRIBUTORS_COUNT, nContributors );
            }
		}
        catch ( IOException ex )
    	{
			AppLogService.error( "GitlabService - error during the filling of commits infos of project id : {} " , nProjectId, ex );
		}
    }

    /**
     * Get the number of commits since the last release
     *
     * @param nProjectId the project id
     *
     * @return The number of commits
     */
    private int getNumberOfCommitsSinceLastRelease( Integer nProjectId ) throws IOException
    {
        if ( !isSessionActive( ) )
        {
            connectToGitlab( );
        }

        int nCommitsCountSinceLastRelease = NumberUtils.INTEGER_MINUS_ONE;

        List<GitlabTag> tagsList = _gitLabApi.getTags( nProjectId );

        if ( CollectionUtils.isNotEmpty( tagsList ) )
        {
            Collections.sort( tagsList, new TagCommittedDateComparator( ) );

            String strTagName = tagsList.get( 0 ).getName( );
            int nCommitsCountUntilLastRelease = _gitLabApi.getAllCommits( nProjectId, strTagName ).size( );
            int nCommitsCountOfBranchDevelop = _gitLabApi.getAllCommits( nProjectId, DEVELOP_BRANCH_NAME ).size( );

            nCommitsCountSinceLastRelease = nCommitsCountOfBranchDevelop - nCommitsCountUntilLastRelease;
        }

        return nCommitsCountSinceLastRelease;
    }

    /**
     * connect to gitLab
     * 
     */
    private static void connectToGitlab( )
    {
    	String strUrl = AppPropertiesService.getProperty( PROPERTY_GITLAB_URL );
        String strToken = AppPropertiesService.getProperty( PROPERTY_GITLAB_ACCOUNT_TOKEN );
        _gitLabApi = GitlabAPI.connect( strUrl, strToken );
    }

    /**
     * check if the session is active
     * 
     * @return true if the session is active, false otherwise 
     * @throws IOException if an error occurs
     */
    private static boolean isSessionActive( ) throws IOException
    {
        try 
        {
            GitlabSession session = _gitLabApi.getCurrentSession( );

            return session != null && STATE_ACTIVE.equals( session.getState( ) );
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    /**
     * Comparator to sort gitlab tags by committed date in decremental order
     */
    private static class TagCommittedDateComparator implements Comparator<GitlabTag>
    {
        /**
         * {@inheritDoc }
         */
        @Override
        public int compare( GitlabTag tag1, GitlabTag tag2 )
        {
            return tag2.getCommit( ).getCommittedDate( ).compareTo( tag1.getCommit( ).getCommittedDate( ) );
        }
    }
}