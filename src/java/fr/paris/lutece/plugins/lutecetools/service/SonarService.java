package fr.paris.lutece.plugins.lutecetools.service;


import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * Get sonar metrics from json data
 */
public class SonarService
{
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
	private static final String KEY_MSR = "msr";
	private static final String KEY_KEY = "key";
	private static final String KEY_FRMT_VAL = "frmt_val";
	private static final String KEY_NCLOC = "ncloc";
	private static final String KEY_VIOLATIONS_DENSITY = "violations_density";
	
	private static SonarService _singleton;
	private static HttpAccess httpAccess = new HttpAccess( );
    
	
	/**
	 * Private constructor
	 */
	private SonarService(  )
	{
	}
	
	/**
	 * Returns the unique instance
	 *
	 * @return the unique instance
	 */
	public static synchronized SonarService instance(  )
	{
		if ( _singleton == null )
		{
			_singleton = new SonarService(  );
		}

		return _singleton;
	}
	
	/**
	 * get metrics from Sonar Webservice
	 *
	 * @param strArtifactId The ArtifactId
	 * @return The metrics HashMap
	 */
	public HashMap<String, String> getSonarMetrics( String strArtifactId )
	{
		HashMap<String, String> metrics = new HashMap<>( );
		StringBuilder sbJSONUrl;
		
		sbJSONUrl = new StringBuilder ( URL_SONAR_JSON );
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
			String strHtml = httpAccess.doGet( sbJSONUrl.toString( ) );
			JSONObject json = new JSONObject( strHtml.substring( 1, strHtml.lastIndexOf( "]" ) ) );
			JSONArray msr = json.getJSONArray( KEY_MSR );
			
			for ( int i = 0; i < msr.length( ); i++ )
			{
			    JSONObject key = msr.getJSONObject( i );
			    
			    if ( key.getString( KEY_KEY ).equals( KEY_NCLOC ) )
			    {
			    	metrics.put( KEY_NCLOC, key.getString( KEY_FRMT_VAL ) );
			    }
			    else if ( key.getString( KEY_KEY ).equals( KEY_VIOLATIONS_DENSITY ) )
			    {
			    	metrics.put( KEY_VIOLATIONS_DENSITY, key.getString( KEY_FRMT_VAL ) );
			    }
			}
		}
		catch ( HttpAccessException | JSONException e )
		{
			AppLogService.error( e.getMessage( ) );
		}
		
		return metrics;
	}
}
