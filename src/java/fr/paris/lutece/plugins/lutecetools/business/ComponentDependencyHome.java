/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.lutecetools.business;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ComponentDependencyHome 
{
    private static IComponentDependencyDAO _dao = SpringContextService.getBean( "lutecetools.componentDependencyDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "lutecetools" );
	
    /**
     * Private constructor
     */
    private ComponentDependencyHome(  )
    {
    }

    /**
     * create a link between a component and its dependency
     * 
     * @param component
     *            The instance of the Component which contains the informations to store
     * @param dependency
     *            The instance of the Dependency which contains the informations to store
     */
    public static void create( Component component, Dependency dependency )
    {
        _dao.insert( component, dependency, _plugin );
    }

    /**
     * Remove component dependencies whose identifier is passed in parameter
     * 
     * @param nComponentArtifactId
     *            The component artifact identifier
     */
    public static void remove( String nComponentArtifactId )
    {
        _dao.delete( nComponentArtifactId, _plugin );
    }

    /**
     * Remove all data
     */
    public static void removeAll( )
    {
        _dao.deleteAll( _plugin );
    }

    /**
     * Load the artifact identifier of all the components that depend on a component as a dependency
     *
     * @param nDependencyArtifactId
     *            the artifact identifier of the dependency on which other components depend
     * @return the list which contains the artifact identifiers of all the components that depend on a component
     */
    public static List<String> getDependentComponentArtifactIdList( String nDependencyArtifactId )
    {
        return _dao.selectDependentComponentArtifactIds( nDependencyArtifactId, _plugin );
    }
}
