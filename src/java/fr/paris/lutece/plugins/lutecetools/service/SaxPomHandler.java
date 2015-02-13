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
package fr.paris.lutece.plugins.lutecetools.service;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * SaxPomHandler
 */
public class SaxPomHandler extends DefaultHandler
{
    private static final String TAG_PARENT = "parent";
    private static final String TAG_VERSION = "version";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_JIRA = "jiraProjectName";
    private static final String TAG_SCM = "scm";
    private static final String TAG_URL = "url";
    private String _strParentPomVersion;
    private String _strCoreVersion;
    private String _strJiraKey;
    private String _strScmUrl;
    private boolean _bPomParent;
    private boolean _bVersion;
    private boolean _bArtifactId;
    private boolean _bCore;
    private boolean _bJira;
    private boolean _bSCM;
    private boolean _bURL;

    /**
     * Returns Parent Pom version
     * @return The Parent Pom version
     */
    public String getParentPomVersion(  )
    {
        return _strParentPomVersion;
    }

    /**
     * Returns Core version
     * @return The Core version
     */
    public String getCoreVersion(  )
    {
        return _strCoreVersion;
    }

    /**
     * Returns JIRA key
     * @return The JIRA key
     */
    public String getJiraKey(  )
    {
        return _strJiraKey;
    }

    /**
     * Returns the SCM URL
     * @return The SCM URL
     */
    public String getScmUrl(  )
    {
        return _strScmUrl;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_PARENT ) )
        {
            _bPomParent = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_VERSION ) )
        {
            _bVersion = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_ARTIFACT_ID ) )
        {
            _bArtifactId = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_JIRA ) )
        {
            _bJira = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_SCM ) )
        {
            _bSCM = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_URL ) )
        {
            _bURL = true;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_PARENT ) )
        {
            _bPomParent = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_VERSION ) )
        {
            _bVersion = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_ARTIFACT_ID ) )
        {
            _bArtifactId = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_JIRA ) )
        {
            _bJira = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_SCM ) )
        {
            _bSCM = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_URL ) )
        {
            _bURL = false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        if ( _bPomParent && _bVersion )
        {
            _strParentPomVersion = new String( ch, start, length );
        }
        else if ( _bArtifactId )
        {
            String strArtifactId = new String( ch, start, length );
            _bCore = strArtifactId.equals( "lutece-core" );
        }
        else if ( _bCore && _bVersion )
        {
            _strCoreVersion = new String( ch, start, length );
        }
        else if ( _bJira )
        {
            _strJiraKey = new String( ch, start, length );
        }
        else if ( _bSCM && _bURL )
        {
            _strScmUrl = new String( ch, start, length );
        }
    }
}
