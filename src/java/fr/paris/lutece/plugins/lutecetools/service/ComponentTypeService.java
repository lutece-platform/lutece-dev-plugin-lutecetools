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
package fr.paris.lutece.plugins.lutecetools.service;

import fr.paris.lutece.plugins.lutecetools.business.Component;

/**
 * This component info filler fills the component type : plugin, module or lutece-core. This is used for example when building a pom of a site.
 */
public class ComponentTypeService implements ComponentInfoFiller
{
    private static final String LUTECE_CORE = "lutece-core";
    private static final String LIBRARY = "library";
    private static final String PLUGIN = "plugin";
    private static final String MODULE = "module";
    private static final String DASH = "-";
    private static final String SERVICE_NAME = "Compenent type filler (plugin, module, lutece-core and library)";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fill( Component component, StringBuilder sbLogs )
    {
        String strArtifactId = component.getArtifactId( );

        if ( strArtifactId.equals( LUTECE_CORE ) )
        {
            component.setComponentType( LUTECE_CORE );
            return;
        }

        switch( strArtifactId.split( DASH ) [0] )
        {
            case LIBRARY:
                component.setComponentType( LIBRARY );
                return;
            case PLUGIN:
                component.setComponentType( PLUGIN );
                return;
            case MODULE:
                component.setComponentType( MODULE );
                return;
        }

        sbLogs.append( "Component " ).append( component.getArtifactId( ) ).append( " has to be renamed to plugin-XXX, module-XXX ..." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

}
