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

import fr.paris.lutece.plugins.lutecetools.service.DependenciesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "dependenciesupgrade", pageTitleI18nKey = "lutecetools.xpage.dependencies.pageTitle", pagePathI18nKey = "lutecetools.xpage.dependencies.pagePath" )
public class DependenciesUpgradeApp extends MVCApplication
{
    private static final long serialVersionUID = 3891074334152956337L;
    private static final String TEMPLATE_XPAGE = "/skin/plugins/lutecetools/dependenciesupgrade.html";
    private static final String MARK_INPUT = "inputsource";
    private static final String MARK_OUTPUT = "output";
    private static final String PARAMETER_SOURCE = "source";
    private static final String PARAMETER_FORMAT = "format";
    private static final String VIEW_HOME = "home";
    private static final String ACTION_PROCESS = "process";
    private String _strInput = "";
    private String _strOutput = "";

    /**
     * Returns the content of the page dependenciesupgrade.
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        model.put( MARK_INPUT, _strInput );
        model.put( MARK_OUTPUT, _strOutput );

        return getXPage( TEMPLATE_XPAGE, request.getLocale( ), model );
    }

    @Action( ACTION_PROCESS )
    public XPage process( HttpServletRequest request )
    {
        String strSource = request.getParameter( PARAMETER_SOURCE );
        String strFormat = request.getParameter( PARAMETER_FORMAT );

        _strInput = strSource;
        _strOutput = DependenciesService.process( strSource, strFormat );

        return redirectView( request, VIEW_HOME );
    }
}
