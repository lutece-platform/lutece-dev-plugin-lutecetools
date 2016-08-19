/**
 */
package fr.paris.lutece.plugins.lutecetools.service;

import java.util.HashMap;
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
        Map<String, String> result = new HashMap<String, String>( );
        result = SonarService.instance( ).getSonarMetrics( ARTIFACTID );
        for ( Map.Entry<String, String> metric : result.entrySet( ) )
        {
            System.out.println( metric.getKey( ) + " : " + metric.getValue( ) );
            if ( metric.getKey( ) != null && metric.getKey( ).equals( KEY_NCLOC ) )
            {
                assertEquals( "Error: Is not integer", true, isInteger( metric.getValue( ), 10 ) );
            }
            else
                if ( metric.getKey( ) != null && metric.getKey( ) == KEY_SQALE_DENSITY_RATIO )
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
