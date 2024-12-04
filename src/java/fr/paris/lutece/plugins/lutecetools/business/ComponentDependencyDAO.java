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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class ComponentDependencyDAO implements IComponentDependencyDAO
{
	private static final String SQL_QUERY_INSERT = "INSERT INTO lutecetools_component_dependency ( component_artifactId, dependency_artifactId, dependency_version ) VALUES ( ?, ?, ? ) ";
	private static final String SQL_QUERY_DELETE = "DELETE FROM lutecetools_component_dependency WHERE component_artifactId = ? ";
	private static final String SQL_QUERY_DELETE_ALL = "DELETE FROM lutecetools_component_dependency ";
	private static final String SQL_QUERY_SELECT_BY_DEPENDENCY_ID = "SELECT component_artifactId FROM lutecetools_component_dependency WHERE dependency_artifactId = ? ";

	@Override
	public void insert( Component component, Dependency dependency, Plugin plugin ) 
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setString( 1, component.getArtifactId( ) );
            daoUtil.setString( 2, dependency.getArtifactId( ) );
            daoUtil.setString( 3, dependency.getVersion( ) );

            daoUtil.executeUpdate( );
        }
	}

	@Override
	public void delete( String nComponentArtifactId, Plugin plugin ) 
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setString( 1, nComponentArtifactId );
            
            daoUtil.executeUpdate( );
        }
	}

	@Override
	public void deleteAll( Plugin plugin) 
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
	}

	@Override
	public List<String> selectDependentComponentArtifactIds(String nDependencyArtifactId, Plugin plugin) 
	{
		List<String> artifactIdList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DEPENDENCY_ID, plugin ) )
        {
            daoUtil.setString( 1, nDependencyArtifactId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
            	artifactIdList.add( daoUtil.getString( 1 ) );
            }
        }

        return artifactIdList;
	}
}
