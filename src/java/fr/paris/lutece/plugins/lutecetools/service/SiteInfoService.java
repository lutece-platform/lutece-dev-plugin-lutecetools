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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.lutecetools.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.paris.lutece.plugins.lutecetools.business.Component;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 *
 * @author leridons
 */
public final class SiteInfoService
{

    private static final String TAG_TITLE = "title";
    private static final String TAG_META = "meta";
    private static final String TAG_IMG = "img";
    private static final String TAG_SUBSECTION = "subsection";

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_CONTENT = "content";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_KEYWORDS_VALUE = "keywords";
    private static final String ATTRIBUTE_INTRODUCTION_VALUE = "Introduction";
    private static final String ATTRIBUTE_CONFIGURATION_VALUE = "Configuration";
    private static final String ATTRIBUTE_USAGE_VALUE = "Usage";

    private static final String EXCEPTION_MESSAGE = "LuteceTools - SiteInfoService : Error retrieving site index infos : ";

    private static SiteInfoService _singleton;

    /**
     * Private constructor
     */
    private SiteInfoService( )
    {
    }

    /**
     * Returns the unique instance
     * 
     * @return the unique instance
     */
    public static synchronized SiteInfoService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new SiteInfoService( );
        }

        return _singleton;
    }

    /**
     * Retreive SITE infos for a given component
     * 
     * @param component           The component
     * @param strXdocSiteIndexUrl The Xdoc Site Index URL
     * @param strLang
     * @param sbLogs              Logs
     */
    public void getSiteInfos( Component component, String strXdocSiteIndexUrl, String strLang, StringBuilder sbLogs )
    {
        try
        {
            HttpAccess httpAccess = new HttpAccess( );
            String strSiteIndex = httpAccess.doGet( strXdocSiteIndexUrl );

            String strLANG = "_" + strLang;

            DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance( );
            InputStream is = new ByteArrayInputStream( strSiteIndex.getBytes( ) ); // use inputstream to ignore BOM when
                                                                                   // parsing

            DocumentBuilder builder = documentBuilder.newDocumentBuilder( );
            Document document = builder.parse( new InputSource( is ) );
            Element root = document.getDocumentElement( );

            NodeList titleList = root.getElementsByTagName( TAG_TITLE );
            for ( int i = 0; i < titleList.getLength( ); i++ )
            {
                Element elmt = (Element) titleList.item( i );
                String title = elmt.getTextContent( );
                if ( title != null )
                {
                    component.set( Component.SITE_TITLE + strLANG, title );
                }
            }

            NodeList metaList = root.getElementsByTagName( TAG_META );
            String strKeywords = null;
            for ( int i = 0; i < metaList.getLength( ); i++ )
            {
                Element elmt = (Element) metaList.item( i );
                if ( elmt.hasAttribute( ATTRIBUTE_NAME )
                        && ATTRIBUTE_KEYWORDS_VALUE.equals( elmt.getAttribute( ATTRIBUTE_NAME ) ) )
                {
                    strKeywords = elmt.getAttribute( ATTRIBUTE_CONTENT );
                }
            }

            component.set( Component.HAS_KEYWORDS + strLANG,
                    ( StringUtils.isBlank( strKeywords ) ? "false" : "true" ) );

            // split keywords
            if ( strKeywords != null )
            {
                String[] strTempKeywords = strKeywords.split( "," );
                Set<String> keywords = new HashSet<>( );
                for ( String keyword : strTempKeywords )
                {
                    keywords.add( keyword.trim( ) );
                }

                component.set( Component.SITE_KEYWORDS + strLANG, keywords );
            }

            NodeList subSections = root.getElementsByTagName( TAG_SUBSECTION );
            StringBuilder strIntroduction = new StringBuilder( );
            StringBuilder strConfiguration = new StringBuilder( );
            StringBuilder strUsage = new StringBuilder( );

            for ( int i = 0; i < subSections.getLength( ); i++ )
            {
                Element elmt = (Element) subSections.item( i );

                if ( elmt.hasAttribute( ATTRIBUTE_NAME )
                        && ATTRIBUTE_INTRODUCTION_VALUE.equals( elmt.getAttribute( ATTRIBUTE_NAME ) ) )
                {
                    strIntroduction.append( elmt.getTextContent( ) );
                }

                if ( elmt.hasAttribute( ATTRIBUTE_NAME )
                        && ATTRIBUTE_CONFIGURATION_VALUE.equals( elmt.getAttribute( ATTRIBUTE_NAME ) ) )
                {
                    strConfiguration.append( elmt.getTextContent( ) );
                }

                if ( elmt.hasAttribute( ATTRIBUTE_NAME )
                        && ATTRIBUTE_USAGE_VALUE.equals( elmt.getAttribute( ATTRIBUTE_NAME ) ) )
                {
                    strUsage.append( elmt.getTextContent( ) );
                }
            }

            component.set( Component.SITE_INTRODUCTION + strLANG, strIntroduction.toString( ) );
            component.set( Component.SITE_CONFIGURATION + strLANG, strConfiguration.toString( ) );
            component.set( Component.SITE_USAGE + strLANG, strUsage.toString( ) );

            NodeList imgList = root.getElementsByTagName( TAG_IMG );
            List<String> imagesSrcList = new ArrayList<>( );
            for ( int i = 0; i < imgList.getLength( ); i++ )
            {
                Element elmt = (Element) imgList.item( i );
                imagesSrcList.add( elmt.getAttribute( ATTRIBUTE_SRC ) );
            }
            component.set( Component.SITE_IMGS + strLANG, imagesSrcList );

        }
        catch ( HttpAccessException e )
        {
            sbLogs.append( "\n*** ERROR *** Error reading site index for component :" )
                    .append( component.getArtifactId( ) ).append( " [url : " + strXdocSiteIndexUrl + "]" )
                    .append( EXCEPTION_MESSAGE );
        }
        catch ( Exception e )
        {
            AppLogService.error( EXCEPTION_MESSAGE + component.getArtifactId( ) + " : " + e.getMessage( ), e );
        }
    }

}
