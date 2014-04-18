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
package fr.paris.lutece.plugins.lutecetools.business;


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
}
