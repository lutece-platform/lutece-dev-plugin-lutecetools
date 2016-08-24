/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.lutecetools.business.Dependency;
import fr.paris.lutece.plugins.lutecetools.business.Site;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dependencies Service
 */
public final class DependenciesService
{
    private static final String TEMPLATE_POM = "skin/plugins/lutecetools/pom_template.html";
    private static final String MARK_SITE = "site";
    private static final String MARK_DEPENDENCIES = "dependencies";
    private static final String MARK_CORE_VERSION = "core_version";
    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_GROUP_ID = "groupId";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_VERSION = "version";
    private static final String TAG_TYPE = "type";
    private static final String FORMAT_TEXT = "text";
    private static final String FORMAT_XML = "xml";
    private static final String FORMAT_POM = "pom";
    private static final String INDENT = "    ";

    /**
     * private constructor
     */
    private DependenciesService( )
    {
    }

    /**
     * Process dependencies generation
     * 
     * @param strSource
     *            The source
     * @param strFormat
     *            The output
     * @return The dependencies
     */
    public static String process( String strSource, String strFormat )
    {
        String [ ] components = strSource.split( "\\s+" );

        List<Dependency> list = getDependenciesList( components );

        if ( ( strFormat != null ) && strFormat.equals( FORMAT_TEXT ) )
        {
            return getDependenciesText( list );
        }
        else
            if ( ( strFormat != null ) && strFormat.equals( FORMAT_XML ) )
            {
                return getDependenciesXML( list );
            }
            else
                if ( ( strFormat != null ) && strFormat.equals( FORMAT_POM ) )
                {
                    return getDependenciesPOM( list );
                }

        return "Invalid format";
    }

    /**
     * Gets the dependencies list
     * 
     * @param components
     *            The array of components
     * @return The list
     */
    private static List<Dependency> getDependenciesList( String [ ] components )
    {
        List<Dependency> list = new ArrayList<Dependency>( );

        for ( String name : components )
        {
            Dependency dependency = new Dependency( );
            dependency.setArtifactId( name );
            dependency.setGroupId( "fr.paris.lutece.plugins" );
            dependency.setComponentType( "lutece-plugin" );
            MavenRepoService.setReleaseVersion( dependency );
            list.add( dependency );
        }

        Collections.sort( list );

        return list;
    }

    /**
     * Returns the dependencies formatted with XML format
     * 
     * @param list
     *            The dependencies list
     * @return The output
     */
    private static String getDependenciesXML( List<Dependency> list )
    {
        StringBuffer sb = new StringBuffer( );

        for ( Dependency dependency : list )
        {
            XmlUtil.beginElement( sb, TAG_DEPENDENCY );
            XmlUtil.addElement( sb.append( INDENT ), TAG_GROUP_ID, dependency.getGroupId( ) );
            XmlUtil.addElement( sb.append( INDENT ), TAG_ARTIFACT_ID, dependency.getArtifactId( ) );
            XmlUtil.addElement( sb.append( INDENT ), TAG_VERSION, dependency.getVersion( ) );
            XmlUtil.addElement( sb.append( INDENT ), TAG_TYPE, dependency.getComponentType( ) );
            XmlUtil.endElement( sb, TAG_DEPENDENCY );
        }

        return sb.toString( );
    }

    /**
     * Returns the dependencies formatted with Text format
     * 
     * @param list
     *            The dependencies list
     * @return The output
     */
    private static String getDependenciesText( List<Dependency> list )
    {
        StringBuilder sb = new StringBuilder( );

        for ( Dependency dependency : list )
        {
            sb.append( dependency.getArtifactId( ) ).append( "\t" ).append( dependency.getVersion( ) ).append( "\n" );
        }

        return sb.toString( );
    }

    private static String getDependenciesPOM( List<Dependency> list )
    {
        Site site = new Site( );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_SITE, site );
        model.put( MARK_DEPENDENCIES, list );
        model.put( MARK_CORE_VERSION, MavenRepoService.getLatestCoreVersion( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_POM, LocaleService.getDefault( ), model );

        return template.getHtml( );
    }

}
