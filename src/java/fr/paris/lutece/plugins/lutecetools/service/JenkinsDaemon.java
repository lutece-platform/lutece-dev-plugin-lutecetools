package fr.paris.lutece.plugins.lutecetools.service;

import java.util.Date;

import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class JenkinsDaemon extends Daemon
{
    private static final String BEAN_JENKINS_SERVICE = "lutecetools.filler.jenkins";
    private static JenkinsService _jenkinsService = SpringContextService.getBean( BEAN_JENKINS_SERVICE );

    /**
     * {@inheritDoc }
     */
    @Override
    public void run( )
    {
        long t1 = new Date( ).getTime( );
        _jenkinsService.updateCache( );

        long t2 = new Date( ).getTime( );
        setLastRunLogs( "Lutece Tools - Cache for Jenkins info updated : duration = " + ( t2 - t1 ) + "ms" );
    }
}
