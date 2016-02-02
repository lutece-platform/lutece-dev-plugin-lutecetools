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
package fr.paris.lutece.plugins.lutecetools.web;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.service.ComponentService;
import fr.paris.lutece.plugins.lutecetools.service.ComponentsInfos;
import fr.paris.lutece.plugins.lutecetools.service.MavenRepoService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "components", pageTitleI18nKey = "lutecetools.xpage.components.pageTitle", pagePathI18nKey = "lutecetools.xpage.components.pagePath" )
public class ComponentListApp extends MVCApplication
{
    private static final String TEMPLATE_XPAGE = "/skin/plugins/lutecetools/components.html";
    private static final String MARK_COMPONENTS_LIST = "components_list";
    private static final String MARK_GITHUB_FILTER = "github_filter";
    private static final String MARK_DISPLAY_CORE_VERSIONS = "core_versions";
    private static final String MARK_LOGS = "logs";
    private static final String MARK_INTEGER_SUCCESS = "rci_color_success";
    private static final String MARK_INTEGER_WARNING = "rci_color_warning";
    private static final String MARK_TOTAL_LINES = "total_lines";
    private static final String MARK_COMPONENT_SEARCHED = "component_searched";
    private static final String VIEW_HOME = "home";
    private static final String ACTION_REFRESH = "refresh";
    private static final String ACTION_CLEAR_CACHE = "clearCache";
    private static final String ACTION_SEARCH_COMPONENT = "searchComponent";
    private static final String PARAMETER_GITHUB = "github";
    private static final String PARAMETER_CORE_VERSIONS = "core";
    private static final String PARAMETER_COMPONENT = "component";
    private static final String VALUE_ON = "on";
    private static final long serialVersionUID = 1L;
    
    // RCI color mark
    
    private static final String PROPERTY_SONAR_RCI_SUCCESS = "lutecetools.sonar.mark.rci.success";
    private static final String SONAR_RCI_SUCCESS = AppPropertiesService.getProperty( PROPERTY_SONAR_RCI_SUCCESS );
    private static final String PROPERTY_SONAR_RCI_WARNING = "lutecetools.sonar.mark.rci.warning";
    private static final String SONAR_RCI_WARNING = AppPropertiesService.getProperty( PROPERTY_SONAR_RCI_WARNING );
    
    // Errors
    
    private static final String ERROR_NOT_FOUND = "lutecetools.error.research.notFound";
    
    // Session variable to store working values
    private Component component = null;
    
    /**
     * Returns the content of the page lutecetools.
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
        String strGitHubFilter = request.getParameter( PARAMETER_GITHUB );
        String strDisplayCoreVersions = request.getParameter( PARAMETER_CORE_VERSIONS );
        boolean bDisplayCoreVersions = ( strDisplayCoreVersions != null ) && strDisplayCoreVersions.equals( VALUE_ON );
        boolean bGitHubFilter = ( strGitHubFilter != null ) && ( strGitHubFilter.equals( VALUE_ON ) );
        Integer nTotal = 0;

        Map<String, Object> model = getModel(  );

        ComponentsInfos ci = MavenRepoService.instance(  ).getComponents( );
        
        if ( bGitHubFilter )
        {
            ci.setListComponents( filterGitHub( ci.getListComponents(  ) ) );
        }
        
        for ( Component c : ci.getListComponents( ) )
        {
        	if ( c.getSonarNbLines( ) != null )
        	{
        		nTotal += Integer.parseInt( c.getSonarNbLines( ).replace( ",", "" ) );
        	}
        }
        
        if ( component != null )
        {
        	model.put( MARK_COMPONENT_SEARCHED, component );
        	component = null;
        }
        model.put( MARK_INTEGER_SUCCESS, SONAR_RCI_SUCCESS);
        model.put( MARK_INTEGER_WARNING, SONAR_RCI_WARNING);
        model.put( MARK_COMPONENTS_LIST, ci );
        model.put( MARK_TOTAL_LINES, nTotal );
        model.put( MARK_GITHUB_FILTER, bGitHubFilter );
        model.put( MARK_DISPLAY_CORE_VERSIONS, bDisplayCoreVersions );
        model.put( MARK_LOGS, MavenRepoService.getLogs() );
        
        return getXPage( TEMPLATE_XPAGE, request.getLocale(  ), model );
    }

    /**
     * Refresh action processing
     * @param request The HTTP request
     * @return The page
     */
    @Action( ACTION_REFRESH )
    public XPage refresh( HttpServletRequest request )
    {
        return redirect( request, VIEW_HOME, getViewParameters( request ) );
    }

    /**
     * Clear Cache action processing
     * @param request The HTTP request
     * @return The page
     */
    @Action( ACTION_CLEAR_CACHE )
    public XPage clearCache( HttpServletRequest request )
    {
        ComponentService.clearCache(  );
        MavenRepoService.clearLogs();

        return redirect( request, VIEW_HOME, getViewParameters( request ) );
    }

    /**
     * Filter a list of component to keep only github hosted ones
     * @param listComponents The list to filter
     * @return The filtered list
     */
    private List<Component> filterGitHub( List<Component> listComponents )
    {
        List<Component> list = new ArrayList<Component>(  );

        for ( Component c : listComponents )
        {
            if ( c.getGitHubStatus(  ) > 0 )
            {
                list.add( c );
            }
        }

        return list;
    }

    /**
     * Get the parameters send with the action to resend to the view
     * @param request The HTTP Request
     * @return The parameters
     */
    private Map<String, String> getViewParameters( HttpServletRequest request )
    {
        Map<String, String> mapParameters = new HashMap<String, String>(  );
        String strGitHubFilter = request.getParameter( PARAMETER_GITHUB );
        String strDisplayCoreVersions = request.getParameter( PARAMETER_CORE_VERSIONS );

        if ( ( strGitHubFilter != null ) && ( strGitHubFilter.equals( VALUE_ON ) ) )
        {
            mapParameters.put( PARAMETER_GITHUB, VALUE_ON );
        }

        if ( ( strDisplayCoreVersions != null ) && strDisplayCoreVersions.equals( VALUE_ON ) )
        {
            mapParameters.put( PARAMETER_CORE_VERSIONS, VALUE_ON );
        }

        return mapParameters;
    }
    
}
