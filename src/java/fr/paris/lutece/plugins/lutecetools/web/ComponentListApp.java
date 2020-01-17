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
package fr.paris.lutece.plugins.lutecetools.web;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.service.AbstractGitPlatformService;
import fr.paris.lutece.plugins.lutecetools.service.ComponentService;
import fr.paris.lutece.plugins.lutecetools.service.ComponentsInfos;
import fr.paris.lutece.plugins.lutecetools.service.MavenRepoService;
import fr.paris.lutece.plugins.lutecetools.service.SonarService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "components", pageTitleI18nKey = "lutecetools.xpage.components.pageTitle", pagePathI18nKey = "lutecetools.xpage.components.pagePath" )
public class ComponentListApp extends MVCApplication
{

    private static final String TEMPLATE_XPAGE = "/skin/plugins/lutecetools/components.html";
    private static final String TEMPLATE_XPAGE_DETAIL = "/skin/plugins/lutecetools/component.html";

    private static final String MARK_COMPONENTS_LIST = "components_list";
    private static final String MARK_COMPONENT = "component";
    private static final String MARK_GITHUB_FILTER = "github_filter";
    private static final String MARK_GITLAB_FILTER = "gitlab_filter";
    private static final String MARK_DISPLAY_CORE_VERSIONS = "core_versions";
    private static final String MARK_LOGS = "logs";
    private static final String MARK_INTEGER_SUCCESS = "rci_color_success";
    private static final String MARK_INTEGER_WARNING = "rci_color_warning";
    private static final String MARK_TOTAL_LINES = "total_lines";
    private static final String MARK_TOTAL_PRS = "total_prs";
    private static final String MARK_OLDEST_PR = "oldest_pr";

    private static final String VIEW_HOME = "home";
    private static final String VIEW_DETAIL = "detail";

    private static final String ACTION_REFRESH = "refresh";
    private static final String ACTION_CLEAR_CACHE = "clearCache";

    private static final String PARAMETER_GITHUB = "github";
    private static final String PARAMETER_GITLAB = "gitlab";
    private static final String PARAMETER_CORE_VERSIONS = "core";
    private static final String PARAMETER_ARTIFACT_ID = "artifact_id";
    private static final String PARAMETER_REFRESH = "refresh";

    private static final String VALUE_ON = "on";
    private static final String PLATFORM_GITHUB = "github";
    private static final String PLATFORM_GITLAB = "gitlab";
    private static final long serialVersionUID = 1L;

    // RCI color mark
    private static final String PROPERTY_SONAR_RCI_SUCCESS = "lutecetools.sonar.mark.rci.success";
    private static final String SONAR_RCI_SUCCESS = AppPropertiesService.getProperty( PROPERTY_SONAR_RCI_SUCCESS );
    private static final String PROPERTY_SONAR_RCI_WARNING = "lutecetools.sonar.mark.rci.warning";
    private static final String SONAR_RCI_WARNING = AppPropertiesService.getProperty( PROPERTY_SONAR_RCI_WARNING );

    /**
     * Returns the content of the page lutecetools.
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
        List<String> listFilterPlatform = new ArrayList<>( );
        String strGitHubFilter = request.getParameter( PARAMETER_GITHUB );
        String strGitLabFilter = request.getParameter( PARAMETER_GITLAB );
        String strDisplayCoreVersions = request.getParameter( PARAMETER_CORE_VERSIONS );
        boolean bDisplayCoreVersions = ( strDisplayCoreVersions != null ) && strDisplayCoreVersions.equals( VALUE_ON );
        if ( VALUE_ON.equals( strGitHubFilter ) )
        {
            listFilterPlatform.add( PLATFORM_GITHUB );
        }
        if ( VALUE_ON.equals( strGitLabFilter ) )
        {
            listFilterPlatform.add( PLATFORM_GITLAB );
        }
        Integer nTotal = 0;
        int nTotalPRs = 0;
        long oldestPR = Long.MAX_VALUE;

        Map<String, Object> model = getModel( );

        ComponentsInfos ciInfos = MavenRepoService.instance( ).getComponents( );

        if ( !listFilterPlatform.isEmpty( ) )
        {
            ciInfos.setListComponents( filterPlatform( ciInfos.getListComponents( ), listFilterPlatform ) );
        }

        // FIXME
        for ( Component c : ciInfos.getListComponents( ) )
        {
            if ( c.get( SonarService.SONAR_NB_LINES ) != null )
            {
                nTotal += Integer.parseInt( c.get( SonarService.SONAR_NB_LINES ).replace( ",", "" ) );
            }
            Integer iPullRequestCount = c.getInt( AbstractGitPlatformService.PULL_REQUEST_COUNT );
            if ( iPullRequestCount != null && iPullRequestCount > 0 )
            {
                nTotalPRs = nTotalPRs + c.getInt( AbstractGitPlatformService.PULL_REQUEST_COUNT );
                if ( c.getLong( AbstractGitPlatformService.OLDEST_PULL_REQUEST ) < oldestPR )
                {
                    oldestPR = c.getLong( AbstractGitPlatformService.OLDEST_PULL_REQUEST );
                }
            }
        }

        model.put( MARK_INTEGER_SUCCESS, SONAR_RCI_SUCCESS );
        model.put( MARK_INTEGER_WARNING, SONAR_RCI_WARNING );
        model.put( MARK_COMPONENTS_LIST, ciInfos );
        model.put( MARK_TOTAL_LINES, nTotal );
        if ( nTotalPRs > 0 )
        {
            model.put( MARK_TOTAL_PRS, nTotalPRs );
            model.put( MARK_OLDEST_PR, new Date( oldestPR ) );
        }
        model.put( MARK_GITHUB_FILTER, listFilterPlatform.contains( PLATFORM_GITHUB ) );
        model.put( MARK_GITLAB_FILTER, listFilterPlatform.contains( PLATFORM_GITLAB ) );
        model.put( MARK_DISPLAY_CORE_VERSIONS, bDisplayCoreVersions );
        model.put( MARK_LOGS, MavenRepoService.instance( ).getLogs( ) );

        return getXPage( TEMPLATE_XPAGE, request.getLocale( ), model );
    }

    /**
     * Returns the content of the page lutecetools.
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_DETAIL )
    public XPage viewDetail( HttpServletRequest request )
    {

        String strArtifactId = request.getParameter( PARAMETER_ARTIFACT_ID );
        boolean refresh = ( request.getParameter( PARAMETER_REFRESH ) != null );
        Map<String, Object> model = getModel( );

        long nTotal = 0;
        long nTotalPRs = 0;
        long oldestPR = 0;
        Component c = MavenRepoService.instance( ).getComponent( strArtifactId, true, refresh );

        if ( c.get( SonarService.SONAR_NB_LINES ) != null )
        {
            nTotal += Integer.parseInt( c.get( SonarService.SONAR_NB_LINES ).replace( ",", "" ) );
        }
        Integer iPullRequestCount = c.getInt( AbstractGitPlatformService.PULL_REQUEST_COUNT );
        if ( iPullRequestCount != null && iPullRequestCount > 0 )
        {
            nTotalPRs = nTotalPRs + c.getInt( AbstractGitPlatformService.PULL_REQUEST_COUNT );
            if ( c.getLong( AbstractGitPlatformService.OLDEST_PULL_REQUEST ) < oldestPR )
            {
                oldestPR = c.getLong( AbstractGitPlatformService.OLDEST_PULL_REQUEST );
            }
        }

        model.put( MARK_INTEGER_SUCCESS, SONAR_RCI_SUCCESS );
        model.put( MARK_INTEGER_WARNING, SONAR_RCI_WARNING );
        model.put( MARK_COMPONENT, c );
        model.put( MARK_TOTAL_LINES, nTotal );
        if ( nTotalPRs > 0 )
        {
            model.put( MARK_TOTAL_PRS, nTotalPRs );
            model.put( MARK_OLDEST_PR, new Date( oldestPR ) );
        }

        model.put( MARK_LOGS, MavenRepoService.instance( ).getLogs( ) );

        return getXPage( TEMPLATE_XPAGE_DETAIL, request.getLocale( ), model );
    }

    /**
     * Refresh action processing
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @Action( ACTION_REFRESH )
    public XPage refresh( HttpServletRequest request )
    {
        return redirect( request, VIEW_HOME, getViewParameters( request ) );
    }

    /**
     * Clear Cache action processing
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @Action( ACTION_CLEAR_CACHE )
    public XPage clearCache( HttpServletRequest request )
    {
        ComponentService.clearCache( );
        MavenRepoService.instance( ).clearLogs( );

        return redirect( request, VIEW_HOME, getViewParameters( request ) );
    }

    /**
     * Filter a list of component to keep only github hosted ones
     *
     * @param listComponents
     *            The list to filter
     * @return The filtered list
     */
    private List<Component> filterPlatform( List<Component> listComponents, List<String> listPlatform )
    {
        List<Component> list = new ArrayList<>( );

        for ( Component c : listComponents )
        {
            String strPlatform = c.get( AbstractGitPlatformService.GIT_PLATFORM );
            if ( listPlatform.contains( strPlatform ) )
            {
                list.add( c );
            }
        }

        return list;
    }

    /**
     * Get the parameters send with the action to resend to the view
     *
     * @param request
     *            The HTTP Request
     * @return The parameters
     */
    private Map<String, String> getViewParameters( HttpServletRequest request )
    {
        Map<String, String> mapParameters = new ConcurrentHashMap<>( );
        String strGitHubFilter = request.getParameter( PARAMETER_GITHUB );
        String strGitLabFilter = request.getParameter( PARAMETER_GITLAB );
        String strDisplayCoreVersions = request.getParameter( PARAMETER_CORE_VERSIONS );

        if ( ( strGitHubFilter != null ) && ( strGitHubFilter.equals( VALUE_ON ) ) )
        {
            mapParameters.put( PARAMETER_GITHUB, VALUE_ON );
        }
        if ( ( strGitLabFilter != null ) && ( strGitLabFilter.equals( VALUE_ON ) ) )
        {
            mapParameters.put( PARAMETER_GITLAB, VALUE_ON );
        }

        if ( ( strDisplayCoreVersions != null ) && strDisplayCoreVersions.equals( VALUE_ON ) )
        {
            mapParameters.put( PARAMETER_CORE_VERSIONS, VALUE_ON );
        }

        return mapParameters;
    }

}
