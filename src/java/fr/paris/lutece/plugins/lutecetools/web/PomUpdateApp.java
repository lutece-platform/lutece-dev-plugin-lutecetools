package fr.paris.lutece.plugins.lutecetools.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.lutecetools.service.Globals;
import fr.paris.lutece.plugins.lutecetools.service.LutecetoolsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.lutecetools.service.XMLParser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
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
	
	private static final String MARK_OUTPUT = "output";
	private static final String MARK_HANDLER = "handler";
	private static final String MARK_WARNING = "warning";
	
	private static final String INPUT_FIELD_NAME = "source";
	private static final String ACTION_PROCESS = "process";
	
	private static final String ERROR_XML_PARSING = "Parsing error";
	private static final String CANCELLED = "Cancelled";
	private static final String ERROR_FILE_EXTENSION = "Wrong file extension";
	private static final String ERROR_EMPTY_FIELD = "Empty field";
	
	private LutecetoolsAsynchronousUploadHandler _lutecetoolsAsynchronousUploadHandler = SpringContextService.getBean( LutecetoolsAsynchronousUploadHandler.BEAN_NAME );
	private String _strOutput = "";
	
	/**
	 * Returns the content of the page pomupdate.
	 * @param request The HTTP request
	 * @return The view
	 */
	@View( value = VIEW_HOME, defaultView = true )
	public XPage viewHome( HttpServletRequest request )
	{
		Map<String, Object> model = getModel(  );
		
		if ( !Globals._strWarningPomMessage.isEmpty( ) )
		{
			model.put( MARK_WARNING, Globals._strWarningPomMessage );
			Globals._strWarningPomMessage = "";
		}
		model.put( MARK_HANDLER, _lutecetoolsAsynchronousUploadHandler );
		model.put( MARK_OUTPUT, _strOutput );

		return getXPage( TEMPLATE_XPAGE, request.getLocale(  ), model );
	}
    
	@Action( ACTION_PROCESS )
	public XPage process( HttpServletRequest request )
	{
		FileItem _fileInput = _lutecetoolsAsynchronousUploadHandler.getFile( request, INPUT_FIELD_NAME );

		if ( _fileInput != null )
		{
			if ( _fileInput.getContentType( ).endsWith( "xml" ) )
			{
				_strOutput = XMLParser.updatePOM( _fileInput );
				if ( _strOutput.isEmpty( ) )
				{
					_strOutput = ERROR_XML_PARSING;
				}
				else if ( _strOutput.equals( CANCELLED ) )
				{
					_strOutput = CANCELLED;
				}
			}
			else
			{
	    		_strOutput = ERROR_FILE_EXTENSION;
	    	}
		}
		else
		{
			_strOutput = ERROR_EMPTY_FIELD;
		}

		return redirectView( request, VIEW_HOME );
	}
}
