/*
 * Copyright (c) 2002-2019, Mairie de Paris
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

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class JenkinsBuildResultHome
{
    private static IJenkinsBuildResultDao _dao = SpringContextService.getBean( "lutecetools.jenkinsBuildResultDao" );
    
    /**
     * Private constructor - this class need not be instantiated
     */
    private JenkinsBuildResultHome( )
    {
    }
    
    /**
     * Creation of an instance of jenkinsBuildResult
     *
     * @param jenkinsBuildResult
     *            The instance of the jenkinsBuildResult which contains the informations to store
     *
     */
    public static void create( JenkinsBuildResult jenkinsBuildResult )
    {
        _dao.insert( jenkinsBuildResult );
    }

    /**
     * Update of the jenkinsBuildResult which is specified in parameter
     *
     * @param jenkinsBuildResult
     *            The instance of the jenkinsBuildResult which contains the informations to update
     *
     */
    public static void update( JenkinsBuildResult jenkinsBuildResult )
    {
        _dao.store( jenkinsBuildResult );
    }

    /**
     * Remove the jenkinsBuildResult whose identifier is specified in parameter
     *
     * @param strArtifactName
     *            The jenkinsBuildResult id
     */
    public static void remove( String strArtifactName )
    {
        _dao.delete( strArtifactName );
    }


    /**
     * Returns an instance of a jenkinsBuildResult whose identifier is specified in parameter
     *
     * @param strArtifactName
     *            The jenkinsBuildResult primary key
     * @return an instance of jenkinsBuildResult
     */
    public static JenkinsBuildResult findByPrimaryKey( String strArtifactName )
    {
        return _dao.load( strArtifactName );
    }
}
