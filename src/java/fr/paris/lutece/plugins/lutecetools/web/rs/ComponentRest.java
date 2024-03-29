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
package fr.paris.lutece.plugins.lutecetools.web.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.service.JiraService;
import fr.paris.lutece.plugins.lutecetools.service.MavenRepoService;
import fr.paris.lutece.plugins.lutecetools.service.SonarService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Page resource
 */
@Path( RestConstants.BASE_PATH + Constants.PATH_PLUGIN )
public class ComponentRest
{
    private static final String KEY_COMPONENTS = "components";
    private static final String KEY_COMPONENT = "component";
    private static final String KEY_ID = "artifact_id";
    private static final String KEY_VERSION = "version";
    private static final String KEY_CORE_VERSION = "core_version";
    private static final String KEY_PARENT_POM_VERSION = "parent_pom_version";
    private static final String KEY_SNAPSHOT_VERSION = "snapshot_version";
    private static final String KEY_SNAPSHOT_CORE_VERSION = "snapshot_core_version";
    private static final String KEY_SNAPSHOT_PARENT_POM_VERSION = "snapshot_parent_pom_version";
    private static final String KEY_SONAR_NB_LINES = "sonar_nb_lines";
    private static final String KEY_SONAR_RCI = "sonar_rci";
    private static final String KEY_JIRA_CODE = "jira_code";
    private static final String KEY_JIRA_ROADMAP_URL = "jira_roadmap_url";
    private static final String KEY_JIRA_CURRENT_VERSION_CLOSED_ISSUES = "jira_current_version_closed_issues";
    private static final String KEY_JIRA_CURRENT_VERSION_OPENED_ISSUES = "jira_current_version_opened_issues";
    private static final String KEY_SCM_URL = "scm_url";
    private static final String KEY_SCM_SNAPSHOT_URL = "scm_snapshot_url";
    private static final String KEY_SCM_CONNECTION = "scm_connection";
    private static final String KEY_SCM_DEVELOPER_CONNECTION = "scm_developer_connection";
    private static final String KEY_KEYWORDS = "keywords";
    private static final String KEY_KEYWORD = "keyword";
    private static final String KEY_INTRODUCTION = "description";
    private static final String KEY_METADATA = "metadata";
    private static final String KEY_LANG = "lang";

    private static final ObjectMapper _mapper = new ObjectMapper( );

    @GET
    @Path( Constants.PATH_COMPONENT + Constants.PATH_ALL )
    public Response getComponents( @HeaderParam( HttpHeaders.ACCEPT ) String accept, @QueryParam( Constants.PARAMETER_FORMAT ) String format )
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) || ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getComponentsJson( );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getComponentsXml( );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build( );
    }

    /**
     * Gets all components list in XML format
     * 
     * @return The list
     */
    public String getComponentsXml( )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader( ) );
        XmlUtil.beginElement( sbXML, KEY_COMPONENTS );

        for ( String strArtifactId : MavenRepoService.instance( ).getComponentsListFromRepository( ) )
        {
            XmlUtil.addElement( sbXML, KEY_ID, strArtifactId );
        }

        XmlUtil.endElement( sbXML, KEY_COMPONENTS );

        return sbXML.toString( );
    }

    /**
     * Gets all components list in JSON format
     * 
     * @return The list
     */
    public String getComponentsJson( )
    {
        JsonNode json = _mapper.createObjectNode( );
        ArrayNode jsonComponents = _mapper.createArrayNode( );

        for ( String strArtifactId : MavenRepoService.instance( ).getComponentsListFromRepository( ) )
        {
            JsonNode jsonComponent = _mapper.createObjectNode( );
            ( (ObjectNode) jsonComponent ).put( KEY_ID, strArtifactId );
            jsonComponents.add( jsonComponent );
        }

        ( (ObjectNode) json ).set( KEY_COMPONENTS, jsonComponents );

        return json.toString( );
    }

    /**
     * Gets a component by its artifact ID
     * 
     * @param strArtifactId
     * @param accept
     *            The Accept header parameter
     * @param format
     *            The format
     * @param bCache
     *            false if the component informations must be recalculated
     * @param strType
     *            the compoent type(lutece-plugin,lutece-site,...)
     * @return The response
     * @throws IOException
     *             if an error occurs
     */
    @GET
    @Path( Constants.PATH_COMPONENT + "/{" + Constants.PATH_ID + "}" )
    public Response getComponent( @PathParam( Constants.PATH_ID ) String strArtifactId, @HeaderParam( HttpHeaders.ACCEPT ) String accept,
            @QueryParam( Constants.PARAMETER_FORMAT ) String format, @QueryParam( Constants.PARAMETER_CACHE ) Boolean bCache,
            @QueryParam( Constants.PARAMETER_TYPE ) String strType ) throws IOException
    {
        String entity;
        String mediaType;
        
        boolean cache = true;
        if ( bCache != null )
        {
            cache = bCache;
        }

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) || ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getComponentJson( strArtifactId, cache, strType );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getComponentXml( strArtifactId, cache, strType );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build( );
    }

    /**
     * Gets a component in XML format
     * 
     * @param strArtifactId
     *            The artifact ID
     * @param bCache
     *            false if the component informations must be recalculated
     * @param strType
     *            the compoent type(lutece-plugin,lutece-site,...)
     * @return The XML output
     */
    public String getComponentXml( String strArtifactId, boolean bCache, String strType )
    {
        StringBuffer sbXML = new StringBuffer( );

        try
        {
            Component component = MavenRepoService.instance( ).getComponent( strArtifactId, true, !bCache, strType );

            if ( component != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addComponentXml( sbXML, component );
            }
        }
        catch( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid component number", 3 ) );
        }
        catch( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Component not found", 1 ) );
        }

        return sbXML.toString( );
    }

    /**
     * Gets a component in JSON format
     * 
     * @param strArtifactId
     *            The artifact ID
     * @param bCache
     *            false if the component informations must be recalculated
     * @param strType
     *            the compoent type(lutece-plugin,lutece-site,...)
     * @return The JSON output
     */
    public String getComponentJson( String strArtifactId, boolean bCache, String strType )
    {
        JsonNode json = _mapper.createObjectNode( );
        String strJson = "";

        try
        {

            Component component = MavenRepoService.instance( ).getComponent( strArtifactId, true, !bCache, strType );

            if ( component != null )
            {
                // Jackson automatic serialization to allow automatic deserialization
                addComponentJsonAuto( json, component );
                strJson = json.toString( );
            }
        }
        catch( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid component number", 3 );
        }
        catch( Exception e )
        {
            strJson = JSONUtil.formatError( "Component not found", 1 );
        }

        return strJson;
    }

    /**
     * Write a component into a buffer
     * 
     * @param sbXML
     *            The buffer
     * @param component
     *            The component
     */
    private void addComponentXml( StringBuffer sbXML, Component component )
    {
        XmlUtil.beginElement( sbXML, KEY_COMPONENT );
        XmlUtil.addElement( sbXML, KEY_ID, component.getArtifactId( ) );
        XmlUtil.addElement( sbXML, KEY_VERSION, component.getVersion( ) );
        XmlUtil.addElement( sbXML, KEY_CORE_VERSION, component.get( Component.CORE_VERSION ) );
        XmlUtil.addElement( sbXML, KEY_PARENT_POM_VERSION, component.get( Component.PARENT_POM_VERSION ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_VERSION, component.get( Component.SNAPSHOT_VERSION ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_CORE_VERSION, component.get( Component.SNAPSHOT_CORE_VERSION ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_PARENT_POM_VERSION, component.get( Component.SNAPSHOT_PARENT_POM_VERSION ) );
        XmlUtil.addElement( sbXML, KEY_SONAR_NB_LINES, component.get( SonarService.SONAR_NB_LINES ) );
        XmlUtil.addElement( sbXML, KEY_SONAR_RCI, component.get( SonarService.SONAR_RCI ) );
        XmlUtil.addElement( sbXML, KEY_JIRA_CODE, component.get( Component.JIRA_KEY ) );
        XmlUtil.addElement( sbXML, KEY_JIRA_ROADMAP_URL, "https://dev.lutece.paris.fr/jira/projects/" + component.get( Component.JIRA_KEY )
                + "/?selectedTab=com.atlassian.jira.jira-projects-plugin:roadmap-panel" );
        XmlUtil.addElement( sbXML, KEY_JIRA_CURRENT_VERSION_CLOSED_ISSUES,
                component.getInt( JiraService.JIRA_ISSUES_COUNT ) - component.getInt( JiraService.JIRA_UNRESOLVED_ISSUES_COUNT ) );
        XmlUtil.addElement( sbXML, KEY_JIRA_CURRENT_VERSION_OPENED_ISSUES, component.getInt( JiraService.JIRA_UNRESOLVED_ISSUES_COUNT ) );
        XmlUtil.addElement( sbXML, KEY_SCM_URL, component.get( Component.SCM_URL ) );
        XmlUtil.addElement( sbXML, KEY_SCM_SNAPSHOT_URL, component.get( Component.SNAPSHOT_SCM_URL ) );
        XmlUtil.addElement( sbXML, KEY_SCM_CONNECTION, component.get( Component.SCM_CONNECTION ) );
        XmlUtil.addElement( sbXML, KEY_SCM_DEVELOPER_CONNECTION, component.get( Component.SCM_DEVELOPER_CONNECTION ) );

        List<Locale> locales = I18nService.getAdminAvailableLocales( );
        for ( Locale locale : locales )
        {
            Map<String, String> attributeList = new HashMap<>( );
            attributeList.put( KEY_LANG, locale.getLanguage( ) );

            XmlUtil.beginElement( sbXML, KEY_METADATA, attributeList );
            XmlUtil.addElementHtml( sbXML, KEY_INTRODUCTION, ( component.get( Component.SITE_INTRODUCTION + "_" + locale.getLanguage( ) ) == null ? ""
                    : component.get( Component.SITE_INTRODUCTION + "_" + locale.getLanguage( ) ) ) );

            Set<String> keywords = (HashSet<String>) component.getObject( Component.SITE_KEYWORDS + "_" + locale.getLanguage( ) );
            if ( keywords != null )
            {
                XmlUtil.beginElement( sbXML, KEY_KEYWORDS );
                for ( String keyword : keywords )
                {
                    XmlUtil.addElement( sbXML, KEY_KEYWORD, keyword );
                }
                XmlUtil.endElement( sbXML, KEY_KEYWORDS );
            }

            XmlUtil.endElement( sbXML, KEY_METADATA );
        }

        XmlUtil.endElement( sbXML, KEY_COMPONENT );
    }

    /**
     * Write a component into a JSON Object
     * 
     * @param json
     *            The JSON Object
     * @param component
     *            The component
     */
    private void addComponentJsonAuto( JsonNode json, Component component )
    {
        try
        {

            String strJson = _mapper.writeValueAsString( component );

            JsonNode jsonComponent = null;

            try
            {
                jsonComponent = _mapper.readTree( strJson );
            }
            catch( IOException e )
            {
                AppLogService.error( "ERROR serializing component", e );
            }

            ( (ObjectNode) json ).set( KEY_COMPONENT, jsonComponent );

        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "ERROR serializing component", e );
        }

    }

}
