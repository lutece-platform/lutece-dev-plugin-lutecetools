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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * Get sonar metrics from json data
 */
public class SonarService implements ComponentInfoFiller
{
    // Component attributes keys
    public static final String SONAR_NB_LINES = "sonarNbLines";
    public static final String SONAR_RCI = "sonarRci";

    private static final String SERVICE_NAME = "Sonar Info filler service registered";
    // URL
    private static final String PROPERTY_SONAR_JSON_URL = "lutecetools.sonar.json.url";
    private static final String URL_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_URL );
    private static final String PROPERTY_SONAR_JSON_METRICS = "lutecetools.sonar.json.metrics";
    private static final String METRICS_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_METRICS );
    private static final String PROPERTY_SONAR_JSON_LC_RESOURCE = "lutecetools.sonar.json.lutececore.resource";
    private static final String RESOURCE_LC_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_LC_RESOURCE );
    private static final String PROPERTY_SONAR_JSON_PLUGINS_RESOURCE = "lutecetools.sonar.json.plugins.resource";
    private static final String RESOURCE_PLUGINS_SONAR_JSON = AppPropertiesService.getProperty( PROPERTY_SONAR_JSON_PLUGINS_RESOURCE );

    // Tags
    private static final String TAG_LUTECE_CORE = "lutece-core";

    // Keys
    private static final String KEY_COMPONENT = "component";
    private static final String KEY_MEASURES = "measures";
    private static final String KEY_METRIC = "metric";
    private static final String KEY_VALUE = "value";
    private static final String KEY_NCLOC = "ncloc";
    private static final String KEY_SQALE_DEBT_RATIO = "sqale_debt_ratio";

    private static HttpAccess _httpAccess = new HttpAccess( );

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void fill( Component component, StringBuilder sbLogs )
    {
        Map<String, String> metrics = getSonarMetrics( component.getArtifactId( ) );
        for ( Map.Entry<String, String> entry : metrics.entrySet( ) )
        {
            if ( entry.getKey( ).equals( KEY_NCLOC ) )
            {
                component.set( SONAR_NB_LINES, entry.getValue( ) );
            }
            else
                if ( entry.getKey( ).equals( KEY_SQALE_DEBT_RATIO ) )
                {
                    component.set( SONAR_RCI, entry.getValue( ) );
                }
        }

    }

    /**
     * get metrics from Sonar Webservice
     *
     * @param strArtifactId
     *            The ArtifactId
     * @return The metrics HashMap
     */
    public Map<String, String> getSonarMetrics( String strArtifactId )
    {
        Map<String, String> metrics = new HashMap<>( );
        StringBuilder sbJSONUrl;
        sbJSONUrl = new StringBuilder( URL_SONAR_JSON );
        if ( strArtifactId.equals( TAG_LUTECE_CORE ) )
        {
            sbJSONUrl.append( RESOURCE_LC_SONAR_JSON ).append( strArtifactId ).append( METRICS_SONAR_JSON );
        }
        else
        {
            sbJSONUrl.append( RESOURCE_PLUGINS_SONAR_JSON ).append( strArtifactId ).append( METRICS_SONAR_JSON );
        }

        try
        {
            String strHtml = _httpAccess.doGet( sbJSONUrl.toString( ) );
            JSONObject json = new JSONObject( strHtml );
            JSONObject component = json.getJSONObject( KEY_COMPONENT );

            JSONArray measures = component.getJSONArray( KEY_MEASURES );

            for ( int i = 0; i < measures.length( ); i++ )
            {
                JSONObject key = measures.getJSONObject( i );

                if ( key.getString( KEY_METRIC ).equals( KEY_NCLOC ) )
                {
                    metrics.put( KEY_NCLOC, key.getString( KEY_VALUE ) );
                }
                else
                    if ( key.getString( KEY_METRIC ).equals( KEY_SQALE_DEBT_RATIO ) )
                    {
                        metrics.put( KEY_SQALE_DEBT_RATIO, String.valueOf( 100 - key.getInt( KEY_VALUE ) ) + "%" );
                    }
            }
        }
        catch( HttpAccessException | JSONException e )
        {
            AppLogService.error( e.getMessage( ) );
        }

        return metrics;
    }

}
