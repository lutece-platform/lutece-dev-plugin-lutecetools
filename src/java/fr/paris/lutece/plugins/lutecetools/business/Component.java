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
    private String _strJiraKey;
    private String _strScmUrl;
    private String _strSnapshotScmUrl;
    private boolean _bGitHubRepo;
    private long _lLastUpdate;
    private List<String> _listBranches;
    private String _strJiraLastReleasedVersion;
    private String _strJiraLastUnreleasedVersion;
    private int _nJiraIssuesCount;
    private int _nJiraUnresolvedIssuesCount;

    /**
     * Returns the CoreVersion
     * @return The CoreVersion
     */
    public String getCoreVersion(  )
    {
        return _strCoreVersion;
    }

    /**
     * Sets the CoreVersion
     * @param strCoreVersion The CoreVersion
     */
    public void setCoreVersion( String strCoreVersion )
    {
        _strCoreVersion = strCoreVersion;
    }

    /**
     * Returns the ParentPomVersion
     * @return The ParentPomVersion
     */
    public String getParentPomVersion(  )
    {
        return _strParentPomVersion;
    }

    /**
     * Sets the ParentPomVersion
     * @param strParentPomVersion The ParentPomVersion
     */
    public void setParentPomVersion( String strParentPomVersion )
    {
        _strParentPomVersion = strParentPomVersion;
    }

    /**
     * Returns the JiraKey
     * @return The JiraKey
     */
    public String getJiraKey(  )
    {
        return _strJiraKey;
    }

    /**
     * Sets the JiraKey
     * @param strJiraKey The JiraKey
     */
    public void setJiraKey( String strJiraKey )
    {
        _strJiraKey = strJiraKey;
    }

    /**
     * Returns the JiraLastReleasedVersion
     * @return The JiraLastReleasedVersion
     */
    public String getJiraLastReleasedVersion(  )
    {
        return _strJiraLastReleasedVersion;
    }

    /**
     * Sets the JiraLastReleasedVersion
     * @param strJiraLastReleasedVersion The JiraLastReleasedVersion
     */
    public void setJiraLastReleasedVersion( String strJiraLastReleasedVersion )
    {
        _strJiraLastReleasedVersion = strJiraLastReleasedVersion;
    }

    /**
     * Returns the JiraLastUnreleasedVersion
     * @return The JiraLastUnreleasedVersion
     */
    public String getJiraLastUnreleasedVersion(  )
    {
        return _strJiraLastUnreleasedVersion;
    }

    /**
     * Sets the JiraLastUnreleasedVersion
     * @param strJiraLastUnreleasedVersion The JiraLastUnreleasedVersion
     */
    public void setJiraLastUnreleasedVersion( String strJiraLastUnreleasedVersion )
    {
        _strJiraLastUnreleasedVersion = strJiraLastUnreleasedVersion;
    }

    /**
     * Returns the JiraIssuesCount
     * @return The JiraIssuesCount
     */
    public int getJiraIssuesCount(  )
    {
        return _nJiraIssuesCount;
    }

    /**
     * Sets the JiraIssuesCount
     * @param nJiraIssuesCount The JiraIssuesCount
     */
    public void setJiraIssuesCount( int nJiraIssuesCount )
    {
        _nJiraIssuesCount = nJiraIssuesCount;
    }

    /**
     * Returns the JiraUnresolvedIssuesCount
     * @return The JiraUnresolvedIssuesCount
     */
    public int getJiraUnresolvedIssuesCount(  )
    {
        return _nJiraUnresolvedIssuesCount;
    }

    /**
     * Sets the JiraUnresolvedIssuesCount
     * @param nJiraUnresolvedIssuesCount The JiraUnresolvedIssuesCount
     */
    public void setJiraUnresolvedIssuesCount( int nJiraUnresolvedIssuesCount )
    {
        _nJiraUnresolvedIssuesCount = nJiraUnresolvedIssuesCount;
    }

    /**
     * Returns the SnapshotVersion
     * @return The SnapshotVersion
     */
    public String getSnapshotVersion(  )
    {
        return _strSnapshotVersion;
    }

    /**
     * Sets the SnapshotVersion
     * @param strSnapshotVersion The SnapshotVersion
     */
    public void setSnapshotVersion( String strSnapshotVersion )
    {
        _strSnapshotVersion = strSnapshotVersion;
    }

    /**
     * Returns the SnapshotCoreVersion
     * @return The SnapshotCoreVersion
     */
    public String getSnapshotCoreVersion(  )
    {
        return _strSnapshotCoreVersion;
    }

    /**
     * Sets the SnapshotCoreVersion
     * @param strSnapshotCoreVersion The SnapshotCoreVersion
     */
    public void setSnapshotCoreVersion( String strSnapshotCoreVersion )
    {
        _strSnapshotCoreVersion = strSnapshotCoreVersion;
    }

    /**
     * Returns the SnapshotParentPomVersion
     * @return The SnapshotParentPomVersion
     */
    public String getSnapshotParentPomVersion(  )
    {
        return _strSnapshotParentPomVersion;
    }

    /**
     * Sets the SnapshotParentPomVersion
     * @param strSnapshotParentPomVersion The SnapshotParentPomVersion
     */
    public void setSnapshotParentPomVersion( String strSnapshotParentPomVersion )
    {
        _strSnapshotParentPomVersion = strSnapshotParentPomVersion;
    }

    /**
     *
     * @param bGitHub The GitHUb status
     */
    public void setGitHubRepo( boolean bGitHub )
    {
        _bGitHubRepo = bGitHub;
    }

    /**
     *
     * @return The GitHUb status
     */
    public boolean getGitHubRepo(  )
    {
        return _bGitHubRepo;
    }

    /**
     * @return the Scm Url
     */
    public String getScmUrl(  )
    {
        return ( _strScmUrl == null ) ? "" : _strScmUrl;
    }

    /**
     * @param strScmUrl the Scm Url to set
     */
    public void setScmUrl( String strScmUrl )
    {
        _strScmUrl = strScmUrl;
    }

    /**
     * @return the Scm Url
     */
    public String getSnapshotScmUrl(  )
    {
        return ( _strSnapshotScmUrl == null ) ? "" : _strSnapshotScmUrl;
    }

    /**
     * @param strScmUrl the Scm Url to set
     */
    public void setSnapshotScmUrl( String strScmUrl )
    {
        _strSnapshotScmUrl = strScmUrl;
    }

    /**
     * Sets the branches list
     * @param listBranches branches list
     */
    public void setBranchesList( List<String> listBranches )
    {
        _listBranches = listBranches;
    }

    /**
     * Returns the branches list
     * @return branches list
     */
    public List<String> getBranchesList(  )
    {
        return _listBranches;
    }

    /**
     * @return the GitHub Status
     */
    @JsonIgnore
    public int getGitHubStatus(  )
    {
        int nStatus = 0;

        if ( getGitHubRepo(  ) )
        {
            nStatus++;
        }

        if ( getScmUrl(  ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( getSnapshotScmUrl(  ).contains( "github" ) )
        {
            nStatus++;
        }

        if ( ( _listBranches != null ) && ( _listBranches.contains( "develop" ) ) )
        {
            nStatus++;
        }

        return nStatus;
    }

    /**
     * @return the GitHub Status
     */
    @JsonIgnore
    public String getGitHubErrors(  )
    {
        StringBuilder sbErrors = new StringBuilder(  );

        if ( getGitHubRepo(  ) )
        {
            if ( !getScmUrl(  ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the released POM. \n" );
            }

            if ( !getSnapshotScmUrl(  ).contains( ".git" ) )
            {
                sbErrors.append( "Bad SCM info in the snapshot POM. \n" );
            }

            if ( !"3.0".equals( _strParentPomVersion ) )
            {
                sbErrors.append( "Bad parent POM in release POM. should be global-pom 3.0. \n" );
            }

            if ( !"3.0".equals( _strSnapshotParentPomVersion ) )
            {
                sbErrors.append( "Bad parent POM in snapshot POM. should be global-pom 3.0. \n" );
            }

            if ( ( _listBranches != null ) && ( !_listBranches.contains( "develop" ) ) )
            {
                sbErrors.append( "Branch 'develop' is missing. \n" );
            }
        }

        return sbErrors.toString(  );
    }

    
    /**
     * @return the Jira Status
     */
    @JsonIgnore
    public int getJiraStatus(  )
    {
        int nStatus = 0;

        if ( ( getVersion() != null ) && getVersion().equals( _strJiraLastReleasedVersion ) )
        {
            nStatus++;
        }
        if ( ( getSnapshotVersion() != null ) &&  (_strJiraLastUnreleasedVersion != null) && getSnapshotVersion().startsWith(_strJiraLastUnreleasedVersion ) )
        {
            nStatus++;
        }

        return nStatus;
    }

    /**
     * @return the Jira Status
     */
    @JsonIgnore
    public String getJiraErrors(  )
    {
        StringBuilder sbErrors = new StringBuilder(  );

        if ( ( getVersion() != null ) && ! getVersion().equals( _strJiraLastReleasedVersion ) )
        {
             sbErrors.append( "Last Jira released version is not matching the last version in maven repository. \n" );
        }
        if ( ( getSnapshotVersion() != null ) &&  (_strJiraLastUnreleasedVersion != null) && ! getSnapshotVersion() .startsWith(_strJiraLastUnreleasedVersion ) )
        {
             sbErrors.append( "Current Jira roadmap version is not matching current snapshot version. \n" );
        }

        return sbErrors.toString(  );
    }
   
    
    /**
     * @return the LastUpdate
     */
    public long getLastUpdate(  )
    {
        return _lLastUpdate;
    }

    /**
     * @param lLastUpdate the Last Update to set
     */
    public void setLastUpdate( long lLastUpdate )
    {
        _lLastUpdate = lLastUpdate;
    }
}
