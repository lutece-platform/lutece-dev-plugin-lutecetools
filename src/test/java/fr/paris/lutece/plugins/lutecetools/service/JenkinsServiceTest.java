/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
import fr.paris.lutece.util.signrequest.RequestAuthenticator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JenkinsServiceTest
 */
public class JenkinsServiceTest
{
    private static final String GIT_PLATFORM_SCM = "https://github.com/lutece-platform/lutece-dev-plugin-lutecetools.git";
    private static final String GIT_PLATFORM_JOB = "dev-plugin-lutecetools-deploy";
    private static final String GIT_SECTEUR_PUBLIC_SCM = "https://github.com/lutece-secteur-public/dev-plugin-lutecetools.git";
    private static final String GIT_SECTEUR_PUBLIC_JOB = "dev-plugin-lutecetools-deploy";
    private static final String SVN_SCM = "http://dev.lutece.paris.fr/viewvc/lutece/portal/trunk/plugins/seo/plugin-sitemap";
    private static final String SVN_JOB = "seo-plugin-sitemap-deploy";

    /**
     * Test of getJenkinsAuthenticator method, of class JenkinsService.
     */
    @Test
    public void testGetJenkinsAuthenticator( ) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println( "getJobName" );
        Method method = JenkinsService.class.getDeclaredMethod( "getJobName", String.class );
        method.setAccessible( true );
        JenkinsService jenkinsService = new JenkinsService( );
        String strJob = (String) method.invoke( jenkinsService, GIT_PLATFORM_SCM );
        System.out.println( strJob );
        assertEquals( strJob, GIT_PLATFORM_JOB );
        strJob = (String) method.invoke( jenkinsService, GIT_SECTEUR_PUBLIC_SCM );
        System.out.println( strJob );
        assertEquals( strJob, GIT_SECTEUR_PUBLIC_JOB );
        strJob = (String) method.invoke( jenkinsService, SVN_SCM );
        System.out.println( strJob );
        assertEquals( strJob, SVN_JOB );
    }

}
