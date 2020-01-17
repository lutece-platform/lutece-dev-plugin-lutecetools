/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.lutecetools.service.version;

/**
 * Version
 */
public class Version implements Comparable
{
    private int _nMajor;
    private int _nMinor;
    private int _nPatch;
    private String _strQualifier;

    /**
     * @return the nMajor
     */
    public int getMajor( )
    {
        return _nMajor;
    }

    /**
     * @param nMajor
     *            the nMajor to set
     */
    public void setMajor( int nMajor )
    {
        _nMajor = nMajor;
    }

    /**
     * @return the nMinor
     */
    public int getMinor( )
    {
        return _nMinor;
    }

    /**
     * @param nMinor
     *            the nMinor to set
     */
    public void setMinor( int nMinor )
    {
        _nMinor = nMinor;
    }

    /**
     * @return the nPatch
     */
    public int getPatch( )
    {
        return _nPatch;
    }

    /**
     * @param nPatch
     *            the nPatch to set
     */
    public void setPatch( int nPatch )
    {
        _nPatch = nPatch;
    }

    /**
     * @return the Qualifier
     */
    public String getQualifier( )
    {
        return _strQualifier;
    }

    /**
     * @param strQualifier
     *            the Qualifier to set
     */
    public void setQualifier( String strQualifier )
    {
        _strQualifier = strQualifier;
    }

    @Override
    public int compareTo( Object object )
    {
        Version version = (Version) object;
        int nDiff = _nMajor - version.getMajor( );
        if ( nDiff != 0 )
        {
            return nDiff;
        }
        nDiff = _nMinor - version.getMinor( );
        if ( nDiff != 0 )
        {
            return nDiff;
        }
        nDiff = _nPatch - version.getPatch( );
        return nDiff;
    }

    public String getVersion( )
    {
        StringBuilder sbVersion = new StringBuilder( );
        sbVersion.append( _nMajor ).append( '.' ).append( _nMinor ).append( '.' ).append( _nPatch );
        if ( _strQualifier != null )
        {
            sbVersion.append( '-' ).append( _strQualifier );
        }
        return sbVersion.toString( );
    }

    public static Version parse( String strSource ) throws VersionParsingException
    {
        Version version = new Version( );

        try
        {
            String strCurrent = strSource.trim( );

            // Search for qualifier
            int nPos = strCurrent.indexOf( '-' );
            if ( nPos != -1 )
            {
                version.setQualifier( strCurrent.substring( nPos + 1 ) );
                strCurrent = strCurrent.substring( 0, nPos );
            }

            // Search for major digits
            nPos = strCurrent.indexOf( '.' );

            String strMajor = strCurrent.substring( 0, nPos );
            version.setMajor( Integer.parseInt( strMajor ) );

            // Search for minor digits
            strCurrent = strCurrent.substring( nPos + 1 );
            nPos = strCurrent.indexOf( '.' );

            if ( nPos != -1 )
            {
                String strMinor = strCurrent.substring( 0, nPos );
                version.setMinor( Integer.parseInt( strMinor ) );

                strCurrent = strCurrent.substring( nPos + 1 );
                version.setPatch( Integer.parseInt( strCurrent ) );
            }
            else
            {
                version.setMinor( Integer.parseInt( strCurrent ) );
            }
        }
        catch( Exception e )
        {
            throw new VersionParsingException( "Error parsing version : '" + strSource + "' : " + e.getMessage( ), e );
        }
        return version;
    }

}
