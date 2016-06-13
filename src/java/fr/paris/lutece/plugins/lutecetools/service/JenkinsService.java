package fr.paris.lutece.plugins.lutecetools.service;

import java.io.IOException;
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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class JenkinsService {
	// URL
	private static final String PROPERTY_JENKINS_BASE_URL = "lutecetools.jenkins.base.url";
	private static final String PROPERTY_JENKINS_CREDENTIALS_USER = "lutecetools.jenkins.user";
	private static final String PROPERTY_JENKINS_CREDENTIALS_PWD = "lutecetools.jenkins.pwd";
	
	private static final String URL_JENKINS_BASE = AppPropertiesService.getProperty( PROPERTY_JENKINS_BASE_URL );
	private static final String USER_JENKINS = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_USER );
	private static final String PWD_JENKINS = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_PWD );
	
	private static JenkinsService _singleton;
	private Map<String, BuildInfo> _mapScmInfoToBuildInfo;
	
	/**
	 * Private constructor
	 */
	private JenkinsService(  )
	{
	}
	
	/**
	 * Returns the unique instance
	 *
	 * @return the unique instance
	 */
	public static synchronized JenkinsService instance(  )
	{
		if ( _singleton == null )
		{
			_singleton = new JenkinsService(  );
		}

		return _singleton;
	}

	private synchronized Map<String, BuildInfo> getMapScmInfoToBuildInfo() {
		return _mapScmInfoToBuildInfo;
	}

	private synchronized void setMapScmInfoJobName(Map<String, BuildInfo> mapScmInfoToBuildInfo) {
		this._mapScmInfoToBuildInfo = mapScmInfoToBuildInfo;
	}


	/**
	 * 
	 */
    public void updateCache(  )
    {
    	buildMapScmInfoJobName();
    }
   
	/**
	 *
	 */
	public String getJenkinsStatus(Component component) {
        String res = "?";
		BuildInfo buildInfo = getBuildInfo(component);
		if (buildInfo != null) {
			res = buildInfo._strStatus;
		}
        return res;
    }
	
	/**
	 *
	 */
	public String getJenkinsJobUrl(Component component) {
        String res = "?";
		BuildInfo buildInfo = getBuildInfo(component);
		if (buildInfo != null) {
			res = buildInfo._strUrl;
		}
        return res;
	}

	/**
	 * 
	 * @param component
	 * @return
	 */
	private BuildInfo getBuildInfo(Component component) {
		BuildInfo res = null;
		if (getMapScmInfoToBuildInfo() != null) {
			String compScmUrl = component.getSnapshotScmUrl();
			if ((compScmUrl == null) || (compScmUrl.equals(""))) {
				compScmUrl = component.getScmUrl();
			}
			if ((compScmUrl != null) && (!compScmUrl.equals(""))) {
				int index = compScmUrl.indexOf(":");
				if (index > -1) {
					String baseUrl = compScmUrl.substring(index + 1);
					if (getMapScmInfoToBuildInfo().get(baseUrl) != null) {
						BuildInfo buildInfo = getMapScmInfoToBuildInfo().get(baseUrl);
						res = buildInfo;
					}
					else {
						baseUrl = baseUrl.replace("viewvc", "svn");
						if (getMapScmInfoToBuildInfo().get(baseUrl) != null) {
							BuildInfo buildInfo = getMapScmInfoToBuildInfo().get(baseUrl);
							res = buildInfo;
						}
						else {
							AppLogService.error( "Cannot match ScmUrl for the " + component.getArtifactId() +
									" component.getSnapshotScmUrl() = [" + component.getSnapshotScmUrl() + "]," +
									" component.getScmUrl() = [" + component.getScmUrl() + "]");
						}
					}
				}
				else {
					AppLogService.error( "Cannot match ScmUrl for the " + component.getArtifactId() +
							" component.getSnapshotScmUrl() = [" + component.getSnapshotScmUrl() + "]," +
							" component.getScmUrl() = [" + component.getScmUrl() + "]");
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
	private ScmInfo parseJenkinsJobConsoleOutputToGetScmUrl(String strHtml) {
		ScmInfo res = new ScmInfo();
		String toRead = strHtml;
		String toAnalyse = "";
		boolean emptyString = (toRead == null) || (toRead.trim().equals(""));
		boolean loop = (!emptyString);
		
		while (loop) {
			int index = toRead.indexOf("\n");
			if (index == -1) {
				toAnalyse = toRead;
				loop = false;
			}
			else {
				toAnalyse = toRead.substring(0, index).trim();
				toRead = toRead.substring(index + 1);
			}
			
			if (toAnalyse.contains("git")) {
				if (toAnalyse.contains("Fetching upstream changes from ")) {
					res._strScmTool = "git";
					res._strScmUrl = toAnalyse.replace("Fetching upstream changes from ", "").trim();
					loop = false;
				}
			}
			else {
				if (toAnalyse.contains("svn")) {
					if (toAnalyse.contains(" at revision")) {
						res._strScmTool = "svn";
						int index2 = toAnalyse.indexOf(" at revision");
						res._strScmUrl = toAnalyse.substring(0, index2);
						if (res._strScmUrl.indexOf("Updating ") >= 0) {
							res._strScmUrl = res._strScmUrl.replace("Updating ", "").trim();
						}
						else  {
							if (res._strScmUrl.indexOf("Checking out ") >= 0) {
								res._strScmUrl = res._strScmUrl.replace("Checking out ", "").trim();
							}
						}
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
	private ScmInfo getScmInfo(String jobName) {
		ScmInfo res = null;
		BuildInfo jobInfo = getJobInfo(jobName);
		String strHtml = performsGetJenkinsUrlAndGetsResult(URL_JENKINS_BASE + "job/" + jobName + "/" + jobInfo._strNumber + "/logText/progressiveText?start=0", true);
		res = parseJenkinsJobConsoleOutputToGetScmUrl(strHtml);
		return res;
	}

	/**
	 * 
	 * @param jobName
	 * @return
	 */
	private BuildInfo getJobInfo(String jobName) {
		BuildInfo res = new BuildInfo();
		try 
		{
			String strHtml = performsGetJenkinsUrlAndGetsResult(URL_JENKINS_BASE + "job/" + jobName + "/lastBuild/api/json", false);
			JSONObject json = new JSONObject( strHtml );
			res._jobName = jobName;
			res._strStatus = json.getString( "result" );
			res._strNumber = json.getString( "number" );
			res._strUrl = URL_JENKINS_BASE + "job/" + jobName + "/lastBuild";
		}
		catch ( JSONException e )
		{
			AppLogService.error( e.getMessage( ) );
		}
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	private void buildMapScmInfoJobName() {
		setMapScmInfoJobName(new TreeMap<String, BuildInfo>());
		try 
		{
			String strHtml = performsGetJenkinsUrlAndGetsResult(URL_JENKINS_BASE + "view/Tous/api/json", false);
			JSONObject json = new JSONObject( strHtml );
			JSONArray listOfJobsJson = json.getJSONArray( "jobs" );
			
			for ( int i = 0; i < listOfJobsJson.length( ); i++ )
			{
			    JSONObject key = listOfJobsJson.getJSONObject( i );
			    String jobName = key.getString( "name" );
			    
			    if (jobName.endsWith("-deploy")) {
			    	if (!jobName.contains("$")) {
					    ScmInfo scmInfo = getScmInfo(jobName);
					    int index = scmInfo._strScmUrl.indexOf(":");
					    String baseUrl = scmInfo._strScmUrl.substring(index + 1);
					    BuildInfo buildInfo = getJobInfo(jobName);
					    getMapScmInfoToBuildInfo().put(baseUrl, buildInfo);
			    	}
			    }
			}
		}
		catch ( JSONException e )
		{
			AppLogService.error( e.getMessage( ) );
		}
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private String performsGetJenkinsUrlAndGetsResult(String url, boolean haveParameeter) {
		String res = "";
		    
		// Build token
		String buildToken = "BUILD_TOKEN";

		// Create your httpclient
		DefaultHttpClient client = new DefaultHttpClient();

		// Then provide the right credentials
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(USER_JENKINS, PWD_JENKINS));

		// Generate BASIC scheme object and stick it to the execution context
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);

		// Add as the first (because of the zero) request interceptor
		// It will first intercept the request and preemptively initialize the authentication scheme if there is not
		client.addRequestInterceptor(new PreemptiveAuth(), 0);

		// You get request that will start the build
		String getUrl = url;
		if (haveParameeter) {
			getUrl = getUrl + "&";
		}
		else {
			getUrl = getUrl + "?";
		}
		getUrl = getUrl + "token=" + buildToken;
		HttpGet get = new HttpGet(getUrl);

		try {
			// Execute your request with the given context
			HttpResponse response = client.execute(get, context);
			HttpEntity entity = response.getEntity();
			res = EntityUtils.toString(entity);;
		}
		catch (IOException e) {
			AppLogService.error( e.getMessage( ) );
		}
		
		return res;
	}

	private class BuildInfo {
		public String _jobName = "";
		public String _strStatus = "";
		public String _strNumber = "";
		public String _strUrl = "";
	}

	private class ScmInfo {
		public String _strScmTool = "";
		public String _strScmUrl = "";
	}
	
	/**
	 * Preemptive authentication interceptor
	 *
	 */
	static class PreemptiveAuth implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
		 * org.apache.http.protocol.HttpContext)
		 */
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			// Get the AuthState
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

		}

	}
}
