/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.signrequest.BasicAuthorizationAuthenticator;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

/**
 * JenkinsService
 */
public class JenkinsService implements ComponentInfoFiller
{
    public static final String DEFAULT_BADGE_URL = "images/skin/plugins/lutecetools/job-not-found.svg";

    private static final String JENKINS_JOB_BUILD_URL = "jenkinsJobBuildUrl";
    private static final String JENKINS_JOB_BADGE_ICON_URL = "jenkinsJobBadgeIconUrl";
    private static final String JENKINS_JOB_STATUS = "jenkinsStatus";

    private static final String SERVICE_NAME = "Jenkins Info filler service registered";
    private static final String PROPERTY_JENKINS_BADGE_URL = "lutecetools.jenkins.badge.url";
    private static final String PROPERTY_JENKINS_CREDENTIALS_USER = "lutecetools.jenkins.user";
    private static final String PROPERTY_JENKINS_CREDENTIALS_PWD = "lutecetools.jenkins.pwd";
    private static final String PREFIX_LUTECE_PLATFORM = "https://github.com/lutece-platform/lutece-";
    private static final String PREFIX_LUTECE_SECTEUR_PUBLIC = "https://github.com/lutece-secteur-public/";
    private static final String SUFFIX_GIT = ".git";
    private static final String SUFFIX_DEPLOY_JOB = "-deploy";

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
        String strScmInfos = component.get( Component.SNAPSHOT_SCM_URL );
        if ( strScmInfos != null )
        {
            String strJobName = getJobName( strScmInfos );
            String strJenkinsBadgeUrl = AppPropertiesService.getProperty( PROPERTY_JENKINS_BADGE_URL );
            String strBadgeUrl = DEFAULT_BADGE_URL;
            if ( strJobName != null )
            {
                strBadgeUrl = strJenkinsBadgeUrl + strJobName;
            }
            component.set( JENKINS_JOB_STATUS, String.valueOf( strJobName != null ) );
            component.set( JENKINS_JOB_BADGE_ICON_URL, strBadgeUrl );
        }
    }

    /**
     * Gets a Jenkins Authenticator
     * 
     * @return An Authenticator
     */
    public RequestAuthenticator getJenkinsAuthenticator( )
    {
        String strUser = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_USER );
        String strPassword = AppPropertiesService.getProperty( PROPERTY_JENKINS_CREDENTIALS_PWD );
        return new BasicAuthorizationAuthenticator( strUser, strPassword );
    }

    /**
     * Get JobName from SCM infos
     * 
     * @param strScmInfos
     *            SCM infos
     * @return the Job name
     */
    private String getJobName( String strScmInfos )
    {
        if ( strScmInfos == null )
        {
            return null;
        }
        if ( strScmInfos.contains( "lutece-core" ) )
        {
            return "lutece-core-deploy";
        }

        String strJobName;
        if ( strScmInfos.startsWith( PREFIX_LUTECE_PLATFORM ) )
        {
            strJobName = strScmInfos.substring( PREFIX_LUTECE_PLATFORM.length( ) );
        }
        else
            if ( strScmInfos.startsWith( PREFIX_LUTECE_SECTEUR_PUBLIC ) )
            {
                strJobName = strScmInfos.substring( PREFIX_LUTECE_SECTEUR_PUBLIC.length( ) );
            }
            else
            {
                strJobName = getSvnJobName( strScmInfos );
            }

        if ( strJobName != null )
        {
            if ( strJobName.endsWith( SUFFIX_GIT ) )
            {
                strJobName = strJobName.substring( 0, strJobName.length( ) - SUFFIX_GIT.length( ) );
            }
            strJobName += SUFFIX_DEPLOY_JOB;
        }
        return strJobName;
    }

    /**
     * Gets Job name from a SVN SCM URL
     * 
     * @param strScmInfos
     *            The SCM infos
     * @return The JobName
     */
    private String getSvnJobName( String strScmInfos )
    {
        String strJobName = null;

        if ( strScmInfos.endsWith( "/" ) )
        {
            strScmInfos = strScmInfos.substring( 0, strScmInfos.length( ) - 1 );
        }
        int nPos = strScmInfos.lastIndexOf( '/' );
        if ( nPos > 0 )
        {
            strJobName = strScmInfos.substring( nPos + 1 );
            String strPath = strScmInfos.substring( 0, nPos );
            if ( nPos > 0 )
            {
                nPos = strPath.lastIndexOf( '/' );
                String strCategory = strPath.substring( nPos + 1 );
                if ( strCategory.equals( "trunk" ) )
                {
                    strCategory = "application";
                }
                strJobName = strCategory + "-" + strJobName;
            }
        }
        return strJobName;
    }
}
