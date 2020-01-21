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
package fr.paris.lutece.plugins.lutecetools.service;

import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * Class to test SonarService
 */
public class SonarServiceTest extends LuteceTestCase
{
    private static final Object KEY_NCLOC = "ncloc";
    private static final String KEY_SQALE_DENSITY_RATIO = "sqale_density_ratio";
    public static String ARTIFACTID = "module-mylutece-persona";

    /**
     * Test method GetSonarMetrics()
     */
    @Test
    public void testGetSonarMetrics( )
    {
        System.out.println( "getSonarMetrics" );
        SonarService instance = new SonarService( );
        Map<String, String> result = instance.getSonarMetrics( ARTIFACTID );
        for ( Map.Entry<String, String> metric : result.entrySet( ) )
        {
            System.out.println( metric.getKey( ) + " : " + metric.getValue( ) );
            if ( metric.getKey( ) != null && metric.getKey( ).equals( KEY_NCLOC ) )
            {
                assertEquals( "Error: Is not integer", true, isInteger( metric.getValue( ), 10 ) );
            }
            else
                if ( metric.getKey( ) != null && metric.getKey( ).equals( KEY_SQALE_DENSITY_RATIO ) )
                {
                    assertEquals( "Error: is not percent", true, isPercent( metric.getValue( ) ) );
                }
        }
    }

    private boolean isPercent( String s )
    {
        if ( isInteger( s.substring( 0, -1 ), 10 ) )
        {
            return s.charAt( s.length( ) - 1 ) == '%';
        }
        return false;
    }

    private boolean isInteger( String s, int radix )
    {
        Scanner sc = new Scanner( s.trim( ) );
        if ( !sc.hasNextInt( radix ) )
            return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt( radix );
        return !sc.hasNext( );
    }
}
