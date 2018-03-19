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
package fr.paris.lutece.plugins.lutecetools.business;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Component
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class Component extends AbstractComponent implements Comparable
{

    public static final String CORE_VERSION = "coreVersion";
    public static final String PARENT_POM_VERSION = "parentPomVersion";
    public static final String SNAPSHOT_VERSION = "snapshotVersion";
    public static final String SNAPSHOT_CORE_VERSION = "snapshotCoreVersion";
    public static final String SNAPSHOT_PARENT_POM_VERSION = "snapshotParentPomVersion";
    public static final String JIRA_KEY = "jiraKey";
    public static final String SCM_URL = "scmUrl";
    public static final String SCM_CONNECTION = "scmConnection";
    public static final String SCM_DEVELOPER_CONNECTION = "scmDeveloperConnection";
    public static final String SNAPSHOT_SCM_URL = "snapshotScmUrl";
    public static final String IS_GIT_REPO = "isGitRepo";

    private long _lLastUpdate;
    private Map<String, Object> _mapAttributes = new HashMap<>( );

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
        sbTrace.append( "Component : " ).append( getArtifactId( ) ).append( "\n  [release] Version: " ).append( getVersion( ) )
                .append( "\n  [release] Core version: " ).append( get( CORE_VERSION ) ).append( "\n  [release] SCM URL: " ).append( get( SCM_URL ) )
                .append( "\n  [release] Parent POM Version: " ).append( get( PARENT_POM_VERSION ) ).append( "\n  [snapshot] Core version: " )
                .append( get( SNAPSHOT_CORE_VERSION ) ).append( "\n  [snapshot] SCM URL: " ).append( get( SNAPSHOT_SCM_URL ) )
                .append( "\n  [snapshot] Parent POM Version: " ).append( get( SNAPSHOT_PARENT_POM_VERSION ) );

        return sbTrace.toString( );
    }

    /**
     * Get an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @return The value
     */
    public String get( String strAttributeKey )
    {
        return (String) _mapAttributes.get( strAttributeKey );
    }

    /**
     * Get an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @return The value
     */
    public Integer getInt( String strAttributeKey )
    {
        return (Integer) _mapAttributes.get( strAttributeKey );
    }

    /**
     * Get an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @return The value
     */
    public Boolean getBoolean( String strAttributeKey )
    {
        return (Boolean) _mapAttributes.get( strAttributeKey );
    }

    /**
     * Get an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @return The value
     */
    public Long getLong( String strAttributeKey )
    {
        return (Long) _mapAttributes.get( strAttributeKey );
    }

    /**
     * Get an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @return The value
     */
    public Object getObject( String strAttributeKey )
    {
        return _mapAttributes.get( strAttributeKey );
    }

    /**
     * Set an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @param strValue
     *            The attribute value
     */
    public void set( String strAttributeKey, String strValue )
    {
        _mapAttributes.put( strAttributeKey, strValue );
    }

    /**
     * Set an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @param bValue
     *            The attribute value
     */
    public void set( String strAttributeKey, boolean bValue )
    {
        _mapAttributes.put( strAttributeKey, bValue );
    }

    /**
     * Set an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @param nValue
     *            The attribute value
     */
    public void set( String strAttributeKey, int nValue )
    {
        _mapAttributes.put( strAttributeKey, nValue );
    }

    /**
     * Set an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @param lValue
     *            The attribute value
     */
    public void set( String strAttributeKey, long lValue )
    {
        _mapAttributes.put( strAttributeKey, lValue );
    }

    /**
     * Set an attribute value
     *
     * @param strAttributeKey
     *            The attribute key
     * @param listValue
     *            The attribute value
     */
    public void set( String strAttributeKey, List listValue )
    {
        _mapAttributes.put( strAttributeKey, listValue );
    }

    /**
     * /** get Attributes
     *
     * @return Attributes
     */
    public Map getAttributes( )
    {
        return _mapAttributes;
    }

}
