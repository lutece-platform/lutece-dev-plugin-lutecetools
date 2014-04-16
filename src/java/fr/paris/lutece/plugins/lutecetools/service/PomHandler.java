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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PomHandler extends DefaultHandler
{
    private static final String TAG_PARENT = "parent";
    private static final String TAG_VERSION = "version";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_JIRA = "jiraProjectName";
    private String _strParentPomVersion;
    private String _strCoreVersion;
    private String _strJiraKey;
    boolean bPomParent = false;
    boolean bVersion = false;
    boolean bArtifactId = false;
    boolean bCore = false;
    boolean bJira = false;

    public String getParentPomVersion(  )
    {
        return _strParentPomVersion;
    }

    public String getCoreVersion(  )
    {
        return _strCoreVersion;
    }

    public String getJiraKey(  )
    {
        return _strJiraKey;
    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_PARENT ) )
        {
            bPomParent = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_VERSION ) )
        {
            bVersion = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_ARTIFACT_ID ) )
        {
            bArtifactId = true;
        }
        else if ( qName.equalsIgnoreCase( TAG_JIRA ) )
        {
            bJira = true;
        }
    }

    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_PARENT ) )
        {
            bPomParent = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_VERSION ) )
        {
            bVersion = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_ARTIFACT_ID ) )
        {
            bArtifactId = false;
        }
        else if ( qName.equalsIgnoreCase( TAG_JIRA ) )
        {
            bJira = false;
        }
    }

    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        if ( bPomParent && bVersion )
        {
            _strParentPomVersion = new String( ch, start, length );
        }
        else if ( bArtifactId )
        {
            String strArtifactId = new String( ch, start, length );
            bCore = strArtifactId.equals( "lutece-core" );
        }
        else if ( bCore && bVersion )
        {
            _strCoreVersion = new String( ch, start, length );
        }
        else if ( bJira )
        {
            _strJiraKey = new String( ch, start, length );
        }
    }
}
