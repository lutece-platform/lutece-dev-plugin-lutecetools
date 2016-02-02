package fr.paris.lutece.plugins.lutecetools.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.lutecetools.service.XMLParser;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "pomupdate", pageTitleI18nKey = "lutecetools.xpage.pomupdate.pageTitle", pagePathI18nKey = "lutecetools.xpage.pomupdate.pagePath" )
public class PomUpdateApp extends MVCApplication
{
	private static final String TEMPLATE_XPAGE = "/skin/plugins/lutecetools/pomupdate.html";
	private static final String VIEW_HOME = "home";
	private static final String MARK_INPUT = "input";
	private static final String MARK_OUTPUT = "output";
	private static final String MARK_VALID_PATH = "valid_path";
	private static final String PARAMETER_SOURCE = "source";
	private static final String ACTION_PROCESS = "process";
	private static final String ERROR_XML_PARSING = "Parsing error";
	private static final String CANCELLED = "Cancelled";
	private static final String NOT_FOUND = "File not found";
	
	private String _strInput = "";
	private String _strOutput = "";
	private Boolean _strValidPath = false;
	
	/**
     * Returns the content of the page pomupdate.
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
    	Map<String, Object> model = getModel(  );
        model.put( MARK_INPUT, _strInput );
        model.put( MARK_OUTPUT, _strOutput );
        model.put( MARK_VALID_PATH, _strValidPath );
        
        return getXPage( TEMPLATE_XPAGE, request.getLocale(  ), model );
    }
    
    @Action( ACTION_PROCESS )
    public XPage process( HttpServletRequest request )
    {
        String strSourcePath = request.getParameter( PARAMETER_SOURCE );

        _strInput = strSourcePath;
        _strOutput = XMLParser.updatePOM( _strInput );
        _strValidPath = false;
        if ( _strOutput.equals( NOT_FOUND ) )
        {
        	_strOutput = NOT_FOUND + "\n" + "input path = " + _strInput;
        }
        else if ( _strOutput.isEmpty( ) )
        {
        	_strOutput = ERROR_XML_PARSING;
        	_strValidPath = true;
        }
        else if ( _strOutput.equals( CANCELLED ) )
        {
        	_strOutput = CANCELLED;
        	_strValidPath = true;
        }
        else
        {
        	_strValidPath = true;
        }
				
        return redirectView( request, VIEW_HOME );
    }
}
