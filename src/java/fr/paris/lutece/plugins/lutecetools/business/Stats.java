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

/**
 * Stats
 */
public class Stats
{
    private int _nMavenCount;
    private int _nGithubCount;
    private int _nGithubOK;
    private int _nJiraOK;
    private int _nReadmeOK;

    /**
     * @return the mavenCount
     */
    public int getMavenCount( )
    {
        return _nMavenCount;
    }

    /**
     * @param nMavenCount
     *            the mavenCount to set
     */
    public void setMavenCount( int nMavenCount )
    {
        _nMavenCount = nMavenCount;
    }

    /**
     * @return the nGithubCount
     */
    public int getGithubCount( )
    {
        return _nGithubCount;
    }

    /**
     * @param nGithubCount
     *            the nGithubCount to set
     */
    public void setGithubCount( int nGithubCount )
    {
        _nGithubCount = nGithubCount;
    }

    /**
     * @return the nGithubOK
     */
    public int getGithubOK( )
    {
        return _nGithubOK;
    }

    /**
     * @param nGithubOK
     *            the nGithubOK to set
     */
    public void setGithubOK( int nGithubOK )
    {
        _nGithubOK = nGithubOK;
    }

    /**
     * @return the nJiraOK
     */
    public int getJiraOK( )
    {
        return _nJiraOK;
    }

    /**
     * @param nJiraOK
     *            the nJiraOK to set
     */
    public void setJiraOK( int nJiraOK )
    {
        _nJiraOK = nJiraOK;
    }

    /**
     * @return the nReadmeOK
     */
    public int getReadmeOK( )
    {
        return _nReadmeOK;
    }

    /**
     * @param nReadmeOK
     *            the nReadmeOK to set
     */
    public void setReadmeOK( int nReadmeOK )
    {
        _nReadmeOK = nReadmeOK;
    }
}
