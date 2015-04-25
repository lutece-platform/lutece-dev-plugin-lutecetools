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
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.VersionRelatedIssuesCount;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author pierre
 */
public class JiraService
{
    private static final String URL_JIRA_SERVER = "http://dev.lutece.paris.fr/jira/";
    private static final String URL_API_VERSION = URL_JIRA_SERVER + "rest/api/2/version/";
    private static JiraService _singleton;
    private static AsynchronousJiraRestClientFactory _factory;
    private static AnonymousAuthenticationHandler _auth;
    public static synchronized JiraService instance()
    {
        if (_singleton == null)
        {
            _singleton = new JiraService();
            init();
        }   

        return _singleton;
    }

    private static void init()
    {
        _factory = new AsynchronousJiraRestClientFactory();
        _auth = new AnonymousAuthenticationHandler();
    }
    
    public void setJiraInfos( Component component )
    {        
        JiraRestClient client = null;
        try
        {
            client = _factory.create( new URI(URL_JIRA_SERVER), _auth );
            Project project = client.getProjectClient().getProject( component.getJiraKey()).claim();
            Version versionLastReleased = null;
            Version versionLastUnreleased = null;
            for( Version version : project.getVersions())
            {
                if( ! version.isReleased() )
                {
                    versionLastUnreleased = version;
                }
                else
                {
                    versionLastReleased = version;
                }
            }
            if( versionLastReleased != null )
            {
                component.setJiraLastReleasedVersion( versionLastReleased.getName() );
            }
            if( versionLastUnreleased != null )
            {
                component.setJiraLastUnreleasedVersion( versionLastUnreleased.getName() );
                String strURI = URL_API_VERSION + versionLastUnreleased.getId();
                URI uriVersion = new URI( strURI );
                VersionRestClient clientVersion = client.getVersionRestClient();
                int nUnresolvedIssues = clientVersion.getNumUnresolvedIssues( uriVersion ).claim();
                component.setJiraUnresolvedIssuesCount(nUnresolvedIssues);
                VersionRelatedIssuesCount vRelatedIssues = clientVersion.getVersionRelatedIssuesCount( uriVersion ).claim();
                component.setJiraIssuesCount(vRelatedIssues.getNumFixedIssues());
                System.out.println( "AffectedIssues : " + vRelatedIssues.getNumAffectedIssues() );
                System.out.println( "FixedIssues : " + vRelatedIssues.getNumFixedIssues() );
                System.out.println( "Unresolved : " + nUnresolvedIssues );
            }
        } 
        catch( Exception ex )
        {
            AppLogService.error( "Error getting Jira Infos for Key : '" + component.getJiraKey() + "' : "+ ex.getMessage(), ex);
        }
        finally
        {
            if( client != null )
            {
                try
                {
                    client.close();
                } catch (IOException ex)
                {
                    AppLogService.error( "Error using Jira Client API : " + ex.getMessage(), ex);
                }
            }
        }
   
    }

}