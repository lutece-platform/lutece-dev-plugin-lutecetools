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
package fr.paris.lutece.plugins.lutecetools.business;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Component
 */
public class Component extends AbstractComponent implements Comparable
{
    private String _strCoreVersion;
    private String _strParentPomVersion;
    private String _strSnapshotVersion;
    private String _strSnapshotCoreVersion;
    private String _strSnapshotParentPomVersion;
    private String _strSonarNbLines;
    private String _strSonarRCI;
    private String _strJiraKey;
    private String _strScmUrl;
    private String _strScmConnection;
    private String _strScmDeveloperConnection;
    private String _strSnapshotScmUrl;
    private boolean _bGitHubRepo;
    private String _strGitHubOwner;
    private boolean _bGitHubReadme;
    private int _nGithubPullRequests;
    private String _strGitHubErrors;
    private int _nGitHubStatus;
    private long _nOldestPullRequest;
    private long _lLastUpdate;
    private List<String> _listBranches;
    private String _strJiraLastReleasedVersion;
    private String _strJiraLastUnreleasedVersion;
    private int _nJiraIssuesCount;
    private int _nJiraUnresolvedIssuesCount;
    private int _nJiraKeyError;
    private int _nJiraStatus;
    private String _strJiraErrors;
    private String _strJenkinsJobBuildUrl;
    private String _strJenkinsJobBadgeIconUrl;
    private String _strJenkinsStatus;

    /**
     * Returns the CoreVersion
     * 
     * @return The CoreVersion
     */
    public String getCoreVersion( )
    {
        return _strCoreVersion;
    }

    /**
     * Sets the CoreVersion
     * 
     * @param strCoreVersion
     *            The CoreVersion
     */
    public void setCoreVersion( String strCoreVersion )
    {
        _strCoreVersion = strCoreVersion;
    }

    /**
     * Returns the ParentPomVersion
     * 
     * @return The ParentPomVersion
     */
    public String getParentPomVersion( )
    {
        return _strParentPomVersion;
    }

    /**
     * Sets the ParentPomVersion
     * 
     * @param strParentPomVersion
     *            The ParentPomVersion
     */
    public void setParentPomVersion( String strParentPomVersion )
    {
        _strParentPomVersion = strParentPomVersion;
    }

    /**
     * Returns the SonarNbLines
     * 
     * @return The SonarNbLines
     */
    public String getSonarNbLines( )
    {
        return _strSonarNbLines;
    }

    /**
     * Sets the SonarNbLines
     * 
     * @param strSonarNbLines
     *            The SonarNbLines
     */
    public void setSonarNbLines( String strSonarNbLines )
    {
        _strSonarNbLines = strSonarNbLines;
    }

    /**
     * Returns the SonarRCI
     * 
     * @return The SonarRCI
     */
    public String getSonarRCI( )
    {
        return _strSonarRCI;
    }

    /**
     * Sets the SonarRCI
     * 
     * @param strSonarRCI
     *            The SonarRCI
     */
    public void setSonarRCI( String strSonarRCI )
    {
        _strSonarRCI = strSonarRCI;
    }

    /**
     * Returns the JiraKey
     * 
     * @return The JiraKey
     */
    public String getJiraKey( )
    {
        return _strJiraKey;
    }

    /**
     * Sets the JiraKey
     * 
     * @param strJiraKey
     *            The JiraKey
     */
    public void setJiraKey( String strJiraKey )
    {
        _strJiraKey = strJiraKey;
    }

    /**
     * Returns the JiraLastReleasedVersion
     * 
     * @return The JiraLastReleasedVersion
     */
    public String getJiraLastReleasedVersion( )
    {
        return _strJiraLastReleasedVersion;
    }

    /**
     * Sets the JiraLastReleasedVersion
     * 
     * @param strJiraLastReleasedVersion
     *            The JiraLastReleasedVersion
     */
    public void setJiraLastReleasedVersion( String strJiraLastReleasedVersion )
    {
        _strJiraLastReleasedVersion = strJiraLastReleasedVersion;
    }

    /**
     * Returns the JiraLastUnreleasedVersion
     * 
     * @return The JiraLastUnreleasedVersion
     */
    public String getJiraLastUnreleasedVersion( )
    {
        return _strJiraLastUnreleasedVersion;
    }

    /**
     * Sets the JiraLastUnreleasedVersion
     * 
     * @param strJiraLastUnreleasedVersion
     *            The JiraLastUnreleasedVersion
     */
    public void setJiraLastUnreleasedVersion( String strJiraLastUnreleasedVersion )
    {
        _strJiraLastUnreleasedVersion = strJiraLastUnreleasedVersion;
    }

    /**
     * Returns the JiraIssuesCount
     * 
     * @return The JiraIssuesCount
     */
    public int getJiraIssuesCount( )
    {
        return _nJiraIssuesCount;
    }

    /**
     * Sets the JiraIssuesCount
     * 
     * @param nJiraIssuesCount
     *            The JiraIssuesCount
     */
    public void setJiraIssuesCount( int nJiraIssuesCount )
    {
        _nJiraIssuesCount = nJiraIssuesCount;
    }

    /**
     * Returns the JiraUnresolvedIssuesCount
     * 
     * @return The JiraUnresolvedIssuesCount
     */
    public int getJiraUnresolvedIssuesCount( )
    {
        return _nJiraUnresolvedIssuesCount;
    }

    /**
     * Sets the JiraUnresolvedIssuesCount
     * 
     * @param nJiraUnresolvedIssuesCount
     *            The JiraUnresolvedIssuesCount
     */
    public void setJiraUnresolvedIssuesCount( int nJiraUnresolvedIssuesCount )
    {
        _nJiraUnresolvedIssuesCount = nJiraUnresolvedIssuesCount;
    }

    /**
     * Returns the JiraKeyError
     * 
     * @return The JiraKeyError
     */
    public int getJiraKeyError( )
    {
        return _nJiraKeyError;
    }

    /**
     * Sets the JiraKeyError
     * 
     * @param nJiraKeyError
     *            The JiraKeyError
     */
    public void setJiraKeyError( int nJiraKeyError )
    {
        _nJiraKeyError = nJiraKeyError;
    }

    /**
     * Returns the SnapshotVersion
     * 
     * @return The SnapshotVersion
     */
    public String getSnapshotVersion( )
    {
        return _strSnapshotVersion;
    }

    /**
     * Sets the SnapshotVersion
     * 
     * @param strSnapshotVersion
     *            The SnapshotVersion
     */
    public void setSnapshotVersion( String strSnapshotVersion )
    {
        _strSnapshotVersion = strSnapshotVersion;
    }

    /**
     * Returns the SnapshotCoreVersion
     * 
     * @return The SnapshotCoreVersion
     */
    public String getSnapshotCoreVersion( )
    {
        return _strSnapshotCoreVersion;
    }

    /**
     * Sets the SnapshotCoreVersion
     * 
     * @param strSnapshotCoreVersion
     *            The SnapshotCoreVersion
     */
    public void setSnapshotCoreVersion( String strSnapshotCoreVersion )
    {
        _strSnapshotCoreVersion = strSnapshotCoreVersion;
    }

    /**
     * Returns the SnapshotParentPomVersion
     * 
     * @return The SnapshotParentPomVersion
     */
    public String getSnapshotParentPomVersion( )
    {
        return _strSnapshotParentPomVersion;
    }

    /**
     * Sets the SnapshotParentPomVersion
     * 
     * @param strSnapshotParentPomVersion
     *            The SnapshotParentPomVersion
     */
    public void setSnapshotParentPomVersion( String strSnapshotParentPomVersion )
    {
        _strSnapshotParentPomVersion = strSnapshotParentPomVersion;
    }

    /**
     *
     * @param bGitHub
     *            The GitHUb status
     */
    public void setGitHubRepo( boolean bGitHub )
    {
        _bGitHubRepo = bGitHub;
    }

    /**
     *
     * @return The GitHUb status
     */
    public boolean getGitHubRepo( )
    {
        return _bGitHubRepo;
    }

    /**
     *
     * @param strGitHubOwner
     *            The GitHub owner
     */
    public void setGitHubOwner( String strGitHubOwner )
    {
        _strGitHubOwner = strGitHubOwner;
    }

    /**
     *
     * @return The GitHub owner
     */
    public String getGitHubOwner( )
    {
        return _strGitHubOwner;
    }

    /**
     *
     * @param bReadme
     *            The GitHub readme status
     */
    public void setGitHubReadme( boolean bReadme )
    {
        _bGitHubReadme = bReadme;
    }

    /**
     *
     * @return The GitHub readme status
     */
    public boolean getGitHubReadme( )
    {
        return _bGitHubReadme;
    }

    /**
     * Set status
     * 
     * @param nStatus
     *            The status
     */
    public void setGitHubStatus( int nStatus )
    {
        _nGitHubStatus = nStatus;
    }

    /**
     * Set Errors
     * 
     * @param strGitHubErrors
     *            errors
     */
    public void setGitHubErrors( String strGitHubErrors )
    {
        _strGitHubErrors = strGitHubErrors;
    }

    /**
     * Sets the number of open pull requests for the component
     * 
     * @param nPullRequests
     *            number of open pull requests
     */
    public void setGitHubPullRequests( int nPullRequests )
    {
        _nGithubPullRequests = nPullRequests;
    }

    /**
     * Gets the number of open pull requests for the component
     * 
     * @return number of open pull requests
     */
    public int getGitHubPullRequests( )
    {
        return _nGithubPullRequests;
    }

    /**
     * Sets the oldest pull request update date
     * 
     * @param nOldestPullRequest
     *            oldest pull request update date
     */
    public void setOldestPullRequest( long nOldestPullRequest )
    {
        _nOldestPullRequest = nOldestPullRequest;
    }

    /**
     * Gets the oldest pull request update date
     * 
     * @return oldest pull request update date
     */
    public long getOldestPullRequest( )
    {
        return _nOldestPullRequest;
    }

    /**
     * @return the Scm Url
     */
    public String getScmUrl( )
    {
        return ( _strScmUrl == null ) ? "" : _strScmUrl;
    }

    /**
     * @param strScmUrl
     *            the Scm Url to set
     */
    public void setScmUrl( String strScmUrl )
    {
        _strScmUrl = strScmUrl;
    }

    /**
     * @return the Scm Url
     */
    public String getSnapshotScmUrl( )
    {
        return ( _strSnapshotScmUrl == null ) ? "" : _strSnapshotScmUrl;
    }

    /**
     * @param strScmUrl
     *            the Scm Url to set
     */
    public void setSnapshotScmUrl( String strScmUrl )
    {
        _strSnapshotScmUrl = strScmUrl;
    }

    /**
     * Sets the branches list
     * 
     * @param listBranches
     *            branches list
     */
    public void setBranchesList( List<String> listBranches )
    {
        _listBranches = listBranches;
    }

    /**
     * Returns the branches list
     * 
     * @return branches list
     */
    public List<String> getBranchesList( )
    {
        return _listBranches;
    }

    /**
     * @return the GitHub Status
     */
    public int getGitHubStatus( )
    {
        return _nGitHubStatus;
    }

    /**
     * @return the GitHub Status
     */
    public String getGitHubErrors( )
    {
        return _strGitHubErrors;
    }

    /**
     * Set status
     * 
     * @param nStatus
     *            the status
     */
    public void setJiraStatus( int nStatus )
    {
        _nJiraStatus = nStatus;
    }

    /**
     * @return the Jira Status
     */
    public int getJiraStatus( )
    {
        return _nJiraStatus;
    }

    /**
     * Set errors
     * 
     * @param strErrors
     *            the errors
     */
    public void setJiraErrors( String strErrors )
    {
        _strJiraErrors = strErrors;
    }

    /**
     * @return the Jira Status
     */
    public String getJiraErrors( )
    {
        return _strJiraErrors;
    }

    /**
     * @return the Jenkins Status
     */
    public String getJenkinsStatus( )
    {
        return _strJenkinsStatus;
    }

    /**
     * @return the Jenkins job build url
     */
    public String getJenkinsJobBuildUrl( )
    {
        return _strJenkinsJobBuildUrl;
    }

    /**
     * @return the Jenkins job badge icon url
     */
    public String getJenkinsJobBadgeIconUrl( )
    {
        return _strJenkinsJobBadgeIconUrl;
    }

    /**
     * @return the LastUpdate
     */
    public long getLastUpdate( )
    {
        return _lLastUpdate;
    }

    /**
     * @param lLastUpdate
     *            the Last Update to set
     */
    public void setLastUpdate( long lLastUpdate )
    {
        _lLastUpdate = lLastUpdate;
    }

    /**
     * Readable implementation
     * 
     * @return The component as a string
     */
    @JsonIgnore
    @Override
    public String toString( )
    {
        StringBuilder sbTrace = new StringBuilder( );
        sbTrace.append( "Component : " ).append( getArtifactId() )
                .append( "\n  GitHub status: " ).append( getGitHubStatus() )
                .append( "\n  [release] Version: " ).append( getVersion() )
                .append( "\n  [release] Core version: " ).append( getCoreVersion() )
                .append( "\n  [release] SCM URL: " ).append( getScmUrl() )
                .append( "\n  [release] Parent POM Version: " ).append( getParentPomVersion() )
                .append( "\n  [snapshot] Core version: " ).append( getSnapshotCoreVersion() )
                .append( "\n  [snapshot] SCM URL: " ).append( getSnapshotScmUrl() )
                .append( "\n  [snapshot] Parent POM Version: " ).append( getSnapshotParentPomVersion() );

        List<String> listBranches = getBranchesList( );

        if ( listBranches != null )
        {
            sbTrace.append( "\n  Branches : " );

            for ( String strBranch : listBranches )
            {
                sbTrace.append( strBranch ).append( ' ' );
            }
        }

        return sbTrace.toString( );
    }

    /**
     * Sets the status
     * 
     * @param strJenkinsStatus
     *            The status
     */
    public void setJenkinsStatus( String strJenkinsStatus )
    {
        _strJenkinsStatus = strJenkinsStatus;
    }

    /**
     * Sets the build job URL
     * 
     * @param strJenkinsJobBuildUrl
     *            the build job URL
     */
    public void setJenkinsJobBuildUrl( String strJenkinsJobBuildUrl )
    {
        _strJenkinsJobBuildUrl = strJenkinsJobBuildUrl;
    }

    /**
     * Sets the badge icon URL
     * 
     * @param strJenkinsJobBadgeIconUrl
     *            the badge icon URL
     */
    public void setJenkinsJobBadgeIconUrl( String strJenkinsJobBadgeIconUrl )
    {
        _strJenkinsJobBadgeIconUrl = strJenkinsJobBadgeIconUrl;
    }

    /**
     * 
     * @return scm connection url
     */
    public String getScmConnection( )
    {
        return _strScmConnection;
    }
    /**
     * 
     * @param _strScmConnection scm connection url
     */
    public void setScmConnection( String _strScmConnection )
    {
        this._strScmConnection = _strScmConnection;
    }
    /**
     * 
     * @return scm developer connection url
     */
    public String getScmDeveloperConnection( )
    {
        return _strScmDeveloperConnection;
    }
    /**
     * 
     * @param _strScmDeveloperConnection scm developer connection url
     */
    public void setScmDeveloperConnection( String _strScmDeveloperConnection )
    {
        this._strScmDeveloperConnection = _strScmDeveloperConnection;
    }
}
