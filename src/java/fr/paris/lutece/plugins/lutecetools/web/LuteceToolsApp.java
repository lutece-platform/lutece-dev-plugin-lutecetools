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

import fr.paris.lutece.plugins.lutecetools.service.FileChooser;
import fr.paris.lutece.plugins.lutecetools.service.FileDownloader;
import fr.paris.lutece.plugins.lutecetools.service.Global;
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
@Controller( xpageName = "lutecetools", pageTitleI18nKey = "lutecetools.xpage.lutecetools.pageTitle", pagePathI18nKey = "lutecetools.xpage.lutecetools.pagePath" )
public class LuteceToolsApp extends MVCApplication
{
    private static final String TEMPLATE_XPAGE = "/skin/plugins/lutecetools/lutecetools.html";
    private static final String VIEW_HOME = "home";
    
    // Actions
    private static final String ACTION_CHOOSE = "choose";
    private static final String ACTION_DOWNLOAD_POM = "downloadPom";
    
    // Markers
    private static final String MARK_PATH = "path";
    private static final String MARK_VALID_PATH = "valid_path";
    
    // Parameters
    private static final String PARAMETER_PATH="path";
    
    // Session variable to store working values
    private String path = "";
    
    // Infos
    private static final String INFO_FILE_DOWNLOADED = "lutecetools.info.site.fileDownloaded";
    
    // Errors
    private static final String ERROR_DIR_NOT_FOUND = "lutecetools.error.site.DirNotFound";
    private static final String ERROR_FILE_NOT_FOUND = "lutecetools.error.site.fileNotFound";
    private static final String ERROR_FILE_EXISTS = "lutecetools.error.site.fileExists";
    private static final Integer VALUE_NO_SUCH_DIRECTORY = -2;
    private static final Integer VALUE_INPUT_FILE_NOT_FOUND = -1;
    private static final Integer VALUE_OUTPUT_FILE_EXISTS = 0;
    private static final Integer VALUE_SUCCESS = 1;

    /**
     * Returns the content of the page lutecetools.
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
    	Map<String, Object> model = getModel(  );
    	model.put( MARK_PATH, path );
    	model.put( MARK_VALID_PATH, Global._strFileChooserPath );
         
        return getXPage( TEMPLATE_XPAGE, request.getLocale(  ), model );
    }
    
    /**
     * 
     * @param request
     * @return XPage
     */
    @Action( ACTION_CHOOSE )
    public XPage doChoose( HttpServletRequest request )
    {		
    	path = FileChooser.selectFile( );
    	
    	return redirectView( request, VIEW_HOME );
    }
    
    /**
     * Download a pom
     *
     * @param request The Http request
     * @return The HTML page to display infos
     */
    @Action( ACTION_DOWNLOAD_POM )
    public XPage doDownloadPom( HttpServletRequest request )
    {
    	String strFilePath = request.getParameter( PARAMETER_PATH );
    	Integer nReturn = FileDownloader.updateAndDownload( strFilePath );
    	
    	if ( nReturn == VALUE_INPUT_FILE_NOT_FOUND )
    	{
    		addError( ERROR_FILE_NOT_FOUND, getLocale( request ) );
    	}
    	else if ( nReturn == VALUE_OUTPUT_FILE_EXISTS )
    	{
    		addError( ERROR_FILE_EXISTS, getLocale( request ) );
    	}
    	else if ( nReturn == VALUE_NO_SUCH_DIRECTORY )
    	{
    		addError( ERROR_DIR_NOT_FOUND, getLocale( request ) );
    	}
    	else if ( nReturn == VALUE_SUCCESS )
    	{
    		addInfo( INFO_FILE_DOWNLOADED, getLocale( request ) );
    	}
    	
    	return redirectView( request, VIEW_HOME );
    }
}
