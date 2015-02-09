/*
* Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.plugins.lutecetools.service.MavenRepoService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Page resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.COMPONENT_PATH )
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

    @GET
    @Path( Constants.ALL_PATH )
    public Response getComponents( @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getComponentsJson(  );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getComponentsXml(  );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets all components list in XML format
     * @return The list
     */
    public String getComponentsXml(  )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader(  ) );
        XmlUtil.beginElement( sbXML, KEY_COMPONENTS );

        for ( String strArtifactId : MavenRepoService.getComponentsListFromRepository(  ) )
        {
            XmlUtil.addElement( sbXML, KEY_ID, strArtifactId );
        }

        XmlUtil.beginElement( sbXML, KEY_COMPONENTS );

        return sbXML.toString(  );
    }

    /**
     * Gets all components list in JSON format
     * @return The list
     */
    public String getComponentsJson(  )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonComponents = new JSONArray(  );

        for ( String strArtifactId : MavenRepoService.getComponentsListFromRepository(  ) )
        {
            JSONObject jsonComponent = new JSONObject(  );
            jsonComponent.accumulate( KEY_ID, strArtifactId );
            jsonComponents.add( jsonComponent );
        }

        json.accumulate( KEY_COMPONENTS, jsonComponents );

        return json.toString(  );
    }

    /**
     * Gets a component by its artifact ID
     * @param strArtifactId
     * @param accept The Accept header parameter
     * @param format The format
     * @return The response
     * @throws IOException if an error occurs
     */
    @GET
    @Path( "/{" + Constants.ID_PATH + "}" )
    public Response getComponent( @PathParam( Constants.ID_PATH )
    String strArtifactId, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getComponentJson( strArtifactId );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getComponentXml( strArtifactId );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets a component in XML format
     * @param strArtifactId The artifact ID
     * @return The XML output
     */
    public String getComponentXml( String strArtifactId )
    {
        StringBuffer sbXML = new StringBuffer(  );

        try
        {
            Component component = MavenRepoService.instance(  ).getComponent( strArtifactId, true );

            if ( component != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addComponentXml( sbXML, component );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid component number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Component not found", 1 ) );
        }

        return sbXML.toString(  );
    }

    /**
     * Gets a component in JSON format
     * @param strArtifactId The artifact ID
     * @return The JSON output
     */
    public String getComponentJson( String strArtifactId )
    {
        JSONObject json = new JSONObject(  );
        String strJson = "";

        try
        {
            Component component = MavenRepoService.instance(  ).getComponent( strArtifactId, true );

            if ( component != null )
            {
                addComponentJson( json, component );
                strJson = json.toString(  );
            }
        }
        catch ( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid component number", 3 );
        }
        catch ( Exception e )
        {
            strJson = JSONUtil.formatError( "Component not found", 1 );
        }

        return strJson;
    }

    /**
     * Write a component into a buffer
     * @param sbXML The buffer
     * @param component The component
     */
    private void addComponentXml( StringBuffer sbXML, Component component )
    {
        XmlUtil.beginElement( sbXML, KEY_COMPONENT );
        XmlUtil.addElement( sbXML, KEY_ID, component.getArtifactId(  ) );
        XmlUtil.addElement( sbXML, KEY_VERSION, component.getVersion(  ) );
        XmlUtil.addElement( sbXML, KEY_CORE_VERSION, component.getCoreVersion(  ) );
        XmlUtil.addElement( sbXML, KEY_PARENT_POM_VERSION, component.getParentPomVersion(  ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_VERSION, component.getSnapshotVersion(  ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_CORE_VERSION, component.getSnapshotCoreVersion(  ) );
        XmlUtil.addElement( sbXML, KEY_SNAPSHOT_PARENT_POM_VERSION, component.getSnapshotParentPomVersion(  ) );
        XmlUtil.endElement( sbXML, KEY_COMPONENT );
    }

    /**
     * Write a component into a JSON Object
     * @param json The JSON Object
     * @param component The component
     */
    private void addComponentJson( JSONObject json, Component component )
    {
        JSONObject jsonComponent = new JSONObject(  );
        jsonComponent.accumulate( KEY_ID, component.getArtifactId(  ) );
        jsonComponent.accumulate( KEY_VERSION, component.getVersion(  ) );
        jsonComponent.accumulate( KEY_CORE_VERSION, component.getCoreVersion(  ) );
        jsonComponent.accumulate( KEY_PARENT_POM_VERSION, component.getParentPomVersion(  ) );
        jsonComponent.accumulate( KEY_SNAPSHOT_VERSION, component.getSnapshotVersion(  ) );
        jsonComponent.accumulate( KEY_SNAPSHOT_CORE_VERSION, component.getSnapshotCoreVersion(  ) );
        jsonComponent.accumulate( KEY_SNAPSHOT_PARENT_POM_VERSION, component.getSnapshotParentPomVersion(  ) );
        json.accumulate( KEY_COMPONENT, jsonComponent );
    }
}
