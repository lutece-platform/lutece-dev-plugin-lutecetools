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

public interface IComponentDependencyDAO 
{
	/**
     * Insert a new record in the table.
     * 
     * @param component
     *            instance of the Component object to insert
     * @param dependency
     *            instance of the Dependency object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Component component, Dependency dependency, Plugin plugin );

    /**
     * Delete records from the table
     * 
     * @param nComponentArtifactId
     *            The artifact identifier of the component to delete
     * @param plugin
     *            the Plugin
     */
    void delete( String nComponentArtifactId, Plugin plugin );
    
    /**
     * Delete all records from the table
     * 
     * @param plugin
     *            the Plugin
     */
    void deleteAll( Plugin plugin );

	/**
     * Returns the artifact identifiers of all components that depend on a particular component
     * 
     * @param nDependencyArtifactId
     *            The artifact identifier of the component on which other components depend
     * @param plugin
     *            the Plugin
     * @return the list of component artifact identifiers 
     */
    List<String> selectDependentComponentArtifactIds( String nDependencyArtifactId , Plugin plugin );
}
