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
 * Component
 */
public abstract class AbstractComponent implements Comparable
{
    private String _strArtifactId;
    private String _strVersion;

    /**
     * Returns the ArtifactId
     * 
     * @return The ArtifactId
     */
    public String getArtifactId( )
    {
        return _strArtifactId;
    }

    /**
     * Sets the ArtifactId
     * 
     * @param strArtifactId
     *            The ArtifactId
     */
    public void setArtifactId( String strArtifactId )
    {
        _strArtifactId = strArtifactId;
    }

    /**
     * Returns the Version
     * 
     * @return The Version
     */
    public String getVersion( )
    {
        return _strVersion;
    }

    /**
     * Sets the Version
     * 
     * @param strVersion
     *            The Version
     */
    public void setVersion( String strVersion )
    {
        _strVersion = strVersion;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int compareTo( Object o )
    {
        AbstractComponent c = (AbstractComponent) o;
        String strName1 = getArtifactId( ).substring( getArtifactId( ).indexOf( "-" ) + 1 );
        String strName2 = c.getArtifactId( ).substring( c.getArtifactId( ).indexOf( "-" ) + 1 );

        return strName1.compareTo( strName2 );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals( Object o )
    {
        AbstractComponent c = (AbstractComponent) o;

        return getArtifactId( ).equals( c.getArtifactId( ) );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode( )
    {
        return super.hashCode( );
    }
}
