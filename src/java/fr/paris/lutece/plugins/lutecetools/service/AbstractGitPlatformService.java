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

/**
 * GitPlatform
 */
public abstract class AbstractGitPlatformService implements ComponentInfoFiller
{
    public static final String GIT_PLATFORM = "gitPlatform";
    public static final String GIT_GROUP = "gitGroup";
    public static final String HAS_README = "hasReadme";
    public static final String PULL_REQUEST_COUNT = "pullRequests";
    public static final String GIT_REPO_ERRORS = "gitRepoErrors";
    public static final String GIT_REPO_STATUS = "gitRepoStatus";
    public static final String OLDEST_PULL_REQUEST = "oldestPullRequest";
    public static final String BRANCHES_LIST = "branchesList";

    private String _strGitPlatform;
    private int _nItemCount;
    private int _nItemOk;

    /**
     * Returns the GitPlatform
     *
     * @return The GitPlatform
     */
    public String getGitPlatform( )
    {
        return _strGitPlatform;
    }

    /**
     * Sets the GitPlatform
     *
     * @param strGitPlatform
     *            The GitPlatform
     */
    public void setGitPlatform( String strGitPlatform )
    {
        _strGitPlatform = strGitPlatform;
    }

    /**
     * Returns the ItemCount
     *
     * @return The ItemCount
     */
    public int getItemCount( )
    {
        return _nItemCount;
    }

    /**
     * Sets the ItemCount
     *
     * @param nItemCount
     *            The ItemCount
     */
    public void setItemCount( int nItemCount )
    {
        _nItemCount = nItemCount;
    }

    /**
     * Returns the ItemOk
     *
     * @return The ItemOk
     */
    public int getItemOk( )
    {
        return _nItemOk;
    }

    /**
     * Sets the ItemOk
     *
     * @param nItemOk
     *            The ItemOk
     */
    public void setItemOk( int nItemOk )
    {
        _nItemOk = nItemOk;
    }

    /**
     * Increment item count
     */
    public void incrementItemCount( )
    {
        _nItemCount++;
    }

    /**
     * Increment item OK
     */
    public void incrementItemOk( )
    {
        _nItemOk++;
    }

}
