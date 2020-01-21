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
import fr.paris.lutece.test.LuteceTestCase;
import java.util.Map;
import java.util.Map.Entry;
import org.gitlab.api.models.GitlabProject;
import org.junit.Test;

/**
 * GitlabService Test
 */
public class GitLabServiceTest extends LuteceTestCase
{

    /**
     * Test of getRepositories method, of class GitlabService.
     */
    public void testGetRepositories( ) throws Exception
    {
        System.out.println( "getRepositories" );
        Map<String, GitlabProject> mapRepositories = GitLabService.getRepositories( );
        for ( GitlabProject project : mapRepositories.values( ) )
        {
            System.out.println( "Repository : " + project.getName( ) + " group : " + GitLabService.getGroup( project ) );
        }
    }

    /**
     * Test of fill method, of class GitlabService.
     */
    public void testFill( )
    {
        System.out.println( "fill" );
        Component component = new Component( );
        component.setArtifactId( "plugin-docupaie" );
        StringBuilder sb = new StringBuilder( );
        GitLabService service = new GitLabService( );
        service.fill( component, sb );

        System.out.println( "component artifact id : " + component.getArtifactId( ) );
        System.out.println( "component organization : " + component.get( AbstractGitPlatformService.GIT_GROUP ) );
    }

}
