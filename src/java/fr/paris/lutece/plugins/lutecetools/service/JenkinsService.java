package fr.paris.lutece.plugins.lutecetools.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.web.rs.Constants;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class JenkinsService
{
    // URL
    private static final String PROPERTY_JENKINS_BASE_URL = "lutecetools.jenkins.base.url";
    private static final String PROPERTY_JENKINS_CREDENTIALS_USER = "lutecetools.jenkins.user";
    private static final String PROPERTY_JENKINS_CREDENTIALS_PWD = "lutecetools.jenkins.pwd";

    private static final String URL_JENKINS_BASE = AppPropertiesService.getProperty( PROPERTY_JENKINS_BASE_URL );
    private static final String USER_JENKINS = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_USER );
    private static final String PWD_JENKINS = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_PWD );

    private static JenkinsService _singleton;
    private Map<String, BuildInfo> _mapScmInfoToBuildInfo;
    private Map<String, BuildInfo> _mapArtefactIdToBuildInfo;

    /**
     * Private constructor
     */
    private JenkinsService( )
    {
    }

    /**
     * Returns the unique instance
     *
     * @return the unique instance
     */
    public static synchronized JenkinsService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new JenkinsService( );
        }
        return _singleton;
    }

    private synchronized Map<String, BuildInfo> getMapScmInfoToBuildInfo( )
    {
        return _mapScmInfoToBuildInfo;
    }

    private synchronized void setMapScmInfoJobName( Map<String, BuildInfo> mapScmInfoToBuildInfo )
    {
        this._mapScmInfoToBuildInfo = mapScmInfoToBuildInfo;
    }

    private synchronized Map<String, BuildInfo> getMapArtefactIdToBuildInfo( )
    {
        return _mapArtefactIdToBuildInfo;
    }

    private synchronized void setMapArtefactIdToBuildInfo( Map<String, BuildInfo> mapArtefactIdToBuildInfo )
    {
        this._mapArtefactIdToBuildInfo = mapArtefactIdToBuildInfo;
    }

    /**
	 * 
	 */
    public void updateCache( )
    {
        buildMapScmInfoJobName( );
    }

    /**
	 *
	 */
    public String getJenkinsStatus( Component component )
    {
        String res = "?";
        BuildInfo buildInfo = getBuildInfo( component );
        if ( buildInfo != null )
        {
            res = buildInfo._strStatus;
        }
        return res;
    }

    /**
	 *
	 */
    public String getJenkinsJobBuildUrl( Component component )
    {
        String res = "?";
        BuildInfo buildInfo = getBuildInfo( component );
        if ( buildInfo != null )
        {
            res = buildInfo._strUrl;
        }
        return res;
    }

    /**
     * 
     * @param component
     * @return
     */
    public String getJenkinsJobBadgeIconUrl( Component component )
    {
        String res = "?";
        final String URL_BADGE_SERVICE_REST = RestConstants.BASE_PATH + Constants.PATH_PLUGIN + Constants.PATH_JENKINS + Constants.PATH_JENKINS_BADGE;
        BuildInfo buildInfo = getBuildInfo( component );
        if ( buildInfo != null )
        {
            res = buildInfo._strUrl;
            String baseUrl = AppPathService.getBaseUrl( );
            if ( baseUrl.endsWith( "/" ) )
            {
                baseUrl = baseUrl.substring( 0, baseUrl.length( ) - 1 );
            }
            String urlJenkinsBadgeIcon = URL_JENKINS_BASE + "job/" + buildInfo._jobName + "/badge/icon";
            res = baseUrl + URL_BADGE_SERVICE_REST + "?url=" + urlJenkinsBadgeIcon;
        }
        return res;
    }

    /**
     * 
     * @param strUrl
     * @return
     */
    private String transformUrlToMatchIfNeeded( String strUrl )
    {
        String res = strUrl;
        if ( res.indexOf( "/viewvc/" ) >= 0 )
        {
            res = res.replace( "/viewvc/", "/svn/" );
        }
        else
        {
            if ( res.indexOf( "/wsvn/" ) >= 0 )
            {
                res = res.replace( "/wsvn/", "/svn/" );
            }
        }
        return res;
    }

    /**
     * 
     * @param component
     * @return
     */
    private BuildInfo getBuildInfo( Component component )
    {
        BuildInfo res = null;
        if ( getMapScmInfoToBuildInfo( ) != null )
        {
            String compScmUrl = component.getSnapshotScmUrl( );
            if ( ( compScmUrl == null ) || ( compScmUrl.equals( "" ) ) )
            {
                compScmUrl = component.getScmUrl( );
            }
            if ( ( compScmUrl != null ) && ( !compScmUrl.equals( "" ) ) )
            {
                int index = compScmUrl.indexOf( ":" );
                if ( index > -1 )
                {
                    String scmUrl = compScmUrl.substring( index + 1 );
                    if ( getMapScmInfoToBuildInfo( ).get( scmUrl ) != null )
                    {
                        BuildInfo buildInfo = getMapScmInfoToBuildInfo( ).get( scmUrl );
                        res = buildInfo;
                    }
                    else
                    {
                        if ( scmUrl.endsWith( "/" ) )
                        {
                            scmUrl = scmUrl.substring( 0, scmUrl.length( ) - 1 );
                        }
                        scmUrl = transformUrlToMatchIfNeeded( scmUrl );
                        if ( getMapScmInfoToBuildInfo( ).get( scmUrl ) != null )
                        {
                            BuildInfo buildInfo = getMapScmInfoToBuildInfo( ).get( scmUrl );
                            res = buildInfo;
                        }
                    }
                }
            }

            if ( res == null )
            {
                if ( getMapArtefactIdToBuildInfo( ).get( component.getArtifactId( ) ) != null )
                {
                    BuildInfo buildInfo = getMapArtefactIdToBuildInfo( ).get( component.getArtifactId( ) );
                    res = buildInfo;
                }
                else
                {
                    AppLogService.error( "Cannot match ScmUrl for the " + component.getArtifactId( ) + " component." + " component.getSnapshotScmUrl() = ["
                            + component.getSnapshotScmUrl( ) + "]," + " component.getScmUrl() = [" + component.getScmUrl( ) + "]" );
                }
            }
        }

        return res;
    }

    /**
     * 
     * @param strToAnalyse
     * @param checkArtefactId
     * @return
     */
    private ScmAndComponentInfo getComponentInfo( String strToAnalyse, ScmAndComponentInfo scmAndComponentInfoToUpdate )
    {
        ScmAndComponentInfo res = scmAndComponentInfoToUpdate;

        if ( strToAnalyse.matches( ".*Installing.* to .*-SNAPSHOT.pom.*" ) )
        {
            int index1 = strToAnalyse.indexOf( " to " );
            int index2 = strToAnalyse.indexOf( "-SNAPSHOT.pom" );
            String tmpStr = strToAnalyse.substring( index1, index2 + "-SNAPSHOT.pom".length( ) );
            int index3 = tmpStr.indexOf( "/fr/paris/lutece" );
            String pertinentString = tmpStr.substring( index3 + 1 );
            String [ ] path = pertinentString.split( "/" );
            for ( int i = 0; i < path.length; i++ )
            {
                String z = path [path.length - i - 1];
                if ( z.endsWith( "-SNAPSHOT" ) )
                {
                    String artefactId = path [path.length - i - 2];
                    String groupId = null;
                    for ( int y = 0; y < path.length - i - 2; y++ )
                    {
                        if ( y == 0 )
                        {
                            groupId = path [y];
                        }
                        else
                        {
                            groupId = groupId + "." + path [y];
                        }
                    }
                    res._strGroupId = groupId;
                    res._strArtefactId = artefactId;
                }
            }
        }
        return res;
    }

    /**
     * 
     * @param strHtml
     * @return
     */
    private ScmAndComponentInfo parseJenkinsJobConsoleOutputToGetScmUrl( String strHtml )
    {
        ScmAndComponentInfo res = new ScmAndComponentInfo( );
        String toRead = strHtml;
        String toAnalyse = "";
        boolean emptyString = ( toRead == null ) || ( toRead.trim( ).equals( "" ) );
        boolean loop = ( !emptyString );

        while ( loop )
        {
            int index = toRead.indexOf( "\n" );
            if ( index == -1 )
            {
                toAnalyse = toRead;
                loop = false;
            }
            else
            {
                toAnalyse = toRead.substring( 0, index ).trim( );
                toRead = toRead.substring( index + 1 );
            }

            if ( toAnalyse.contains( "git" ) )
            {
                if ( toAnalyse.contains( "Fetching upstream changes from " ) )
                {
                    res._strScmTool = "git";
                    res._strScmUrl = toAnalyse.replace( "Fetching upstream changes from ", "" ).trim( );
                }
            }
            else
            {
                if ( toAnalyse.contains( "svn" ) )
                {
                    if ( toAnalyse.contains( " at revision" ) )
                    {
                        res._strScmTool = "svn";
                        int index2 = toAnalyse.indexOf( " at revision" );
                        res._strScmUrl = toAnalyse.substring( 0, index2 );
                        if ( res._strScmUrl.indexOf( "Updating " ) >= 0 )
                        {
                            res._strScmUrl = res._strScmUrl.replace( "Updating ", "" ).trim( );
                        }
                        else
                        {
                            if ( res._strScmUrl.indexOf( "Checking out " ) >= 0 )
                            {
                                res._strScmUrl = res._strScmUrl.replace( "Checking out ", "" ).trim( );
                            }
                        }
                    }
                }
                else
                {
                    if ( toAnalyse.matches( ".*Installing.* to .*-SNAPSHOT.pom.*" ) )
                    {
                        res = getComponentInfo( toAnalyse, res );
                        loop = false;
                    }
                }

            }
        }
        return res;
    }

    /**
     * 
     * @param jobName
     * @return
     */
    private ScmAndComponentInfo getScmInfo( String jobName )
    {
        ScmAndComponentInfo res = null;
        BuildInfo jobInfo = getJobInfo( jobName );
        String strHtml = performsGetJenkinsUrlAndGetsResult( URL_JENKINS_BASE + "job/" + jobName + "/" + jobInfo._strNumber
                + "/logText/progressiveText?start=0", true );
        res = parseJenkinsJobConsoleOutputToGetScmUrl( strHtml );
        return res;
    }

    /**
     * 
     * @param jobName
     * @return
     */
    private BuildInfo getJobInfo( String jobName )
    {
        BuildInfo res = new BuildInfo( );
        try
        {
            String strHtml = performsGetJenkinsUrlAndGetsResult( URL_JENKINS_BASE + "job/" + jobName + "/lastBuild/api/json", false );
            JSONObject json = new JSONObject( strHtml );
            res._jobName = jobName;
            res._strStatus = json.getString( "result" );
            res._strNumber = json.getString( "number" );
            res._strUrl = URL_JENKINS_BASE + "job/" + jobName + "/" + res._strNumber;
        }
        catch( JSONException e )
        {
            AppLogService.error( e.getMessage( ) );
        }
        return res;
    }

    /**
     * 
     * @return
     */
    private void buildMapScmInfoJobName( )
    {
        setMapScmInfoJobName( new TreeMap<String, BuildInfo>( ) );
        setMapArtefactIdToBuildInfo( new TreeMap<String, BuildInfo>( ) );
        try
        {
            String strHtml = performsGetJenkinsUrlAndGetsResult( URL_JENKINS_BASE + "view/Tous/api/json", false );
            JSONObject json = new JSONObject( strHtml );
            JSONArray listOfJobsJson = json.getJSONArray( "jobs" );

            for ( int i = 0; i < listOfJobsJson.length( ); i++ )
            {
                try
                {
                    JSONObject key = listOfJobsJson.getJSONObject( i );
                    String jobName = key.getString( "name" );

                    if ( jobName.endsWith( "-deploy" ) || ( jobName.endsWith( "- Deploy" ) ) )
                    {
                        ScmAndComponentInfo scmInfo = getScmInfo( jobName );
                        int index = scmInfo._strScmUrl.indexOf( ":" );
                        String scmUrl = scmInfo._strScmUrl.substring( index + 1 );
                        BuildInfo buildInfo = getJobInfo( jobName );
                        getMapScmInfoToBuildInfo( ).put( scmUrl, buildInfo );
                        getMapArtefactIdToBuildInfo( ).put( scmInfo._strArtefactId, buildInfo );
                    }
                }
                catch( Exception e )
                {
                    AppLogService.error( e.getMessage( ) );
                }
            }
        }
        catch( JSONException e )
        {
            AppLogService.error( e.getMessage( ) );
        }
        AppLogService.info( "MapScmInfoJobName Cache size : " + getMapScmInfoToBuildInfo( ).size( ) );
        AppLogService.info( "MapArtefactIdToBuildInfo Cache size : " + getMapArtefactIdToBuildInfo( ).size( ) );
    }

    /**
     * 
     * @param strUrl
     * @return
     */
    private String getEncodedUrlString( String strUrl )
    {
        String res = strUrl;
        try
        {
            URL url = new URL( res );
            URI uri = new URI( url.getProtocol( ), url.getAuthority( ), url.getPath( ), url.getQuery( ), null );
            res = uri.toURL( ).toString( );
        }
        catch( MalformedURLException e )
        {
            AppLogService.error( e.getMessage( ) );
        }
        catch( URISyntaxException e )
        {
            AppLogService.error( e.getMessage( ) );
        }
        return res;
    }

    /**
     * 
     * @param url
     * @param haveParameter
     * @return
     */
    public HttpResponse performsGetJenkinsUrl( String url, boolean haveParameter )
    {
        HttpResponse res = null;

        // Build token
        String buildToken = "BUILD_TOKEN";

        // Create your httpclient
        DefaultHttpClient client = new DefaultHttpClient( );

        // Then provide the right credentials
        client.getCredentialsProvider( ).setCredentials( new AuthScope( AuthScope.ANY_HOST, AuthScope.ANY_PORT ),
                new UsernamePasswordCredentials( USER_JENKINS, PWD_JENKINS ) );

        // Generate BASIC scheme object and stick it to the execution context
        BasicScheme basicAuth = new BasicScheme( );
        BasicHttpContext context = new BasicHttpContext( );
        context.setAttribute( "preemptive-auth", basicAuth );

        // Add as the first (because of the zero) request interceptor
        // It will first intercept the request and preemptively initialize the authentication scheme if there is not
        client.addRequestInterceptor( new PreemptiveAuth( ), 0 );

        // You get request that will start the build
        String getUrl = url;
        if ( haveParameter )
        {
            getUrl = getUrl + "&";
        }
        else
        {
            getUrl = getUrl + "?";
        }
        getUrl = getUrl + "token=" + buildToken;
        HttpGet get = new HttpGet( getEncodedUrlString( getUrl ) );

        try
        {
            // Execute your request with the given context
            res = client.execute( get, context );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ) );
        }

        return res;
    }

    /**
     * 
     * @param url
     * @return
     */
    private String performsGetJenkinsUrlAndGetsResult( String url, boolean haveParameter )
    {
        String res = "";

        try
        {
            HttpResponse response = performsGetJenkinsUrl( url, haveParameter );
            HttpEntity entity = response.getEntity( );
            res = EntityUtils.toString( entity );
            ;
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ) );
        }

        return res;
    }

    private class BuildInfo
    {
        public String _jobName = "";
        public String _strStatus = "";
        public String _strNumber = "";
        public String _strUrl = "";
    }

    private class ScmAndComponentInfo
    {
        public String _strScmTool = "";
        public String _strScmUrl = "";
        public String _strGroupId = "";
        public String _strArtefactId = "";
    }

    /**
     * Preemptive authentication interceptor
     *
     */
    static class PreemptiveAuth implements HttpRequestInterceptor
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
         */
        public void process( HttpRequest request, HttpContext context ) throws HttpException, IOException
        {
            // Get the AuthState
            AuthState authState = (AuthState) context.getAttribute( ClientContext.TARGET_AUTH_STATE );

            // If no auth scheme available yet, try to initialize it preemptively
            if ( authState.getAuthScheme( ) == null )
            {
                AuthScheme authScheme = (AuthScheme) context.getAttribute( "preemptive-auth" );
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute( ClientContext.CREDS_PROVIDER );
                HttpHost targetHost = (HttpHost) context.getAttribute( ExecutionContext.HTTP_TARGET_HOST );
                if ( authScheme != null )
                {
                    Credentials creds = credsProvider.getCredentials( new AuthScope( targetHost.getHostName( ), targetHost.getPort( ) ) );
                    if ( creds == null )
                    {
                        throw new HttpException( "No credentials for preemptive authentication" );
                    }
                    authState.setAuthScheme( authScheme );
                    authState.setCredentials( creds );
                }
            }
        }
    }
}
