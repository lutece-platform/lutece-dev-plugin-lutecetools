package fr.paris.lutece.plugins.lutecetools.service;

import java.util.Date;

import fr.paris.lutece.portal.service.daemon.Daemon;

public class JenkinsDaemon extends Daemon
{
    @Override
    public void run( )
    {
        long t1 = new Date( ).getTime( );
        JenkinsService.instance( ).updateCache( );

        long t2 = new Date( ).getTime( );
        setLastRunLogs( "Lutece Tools - Cache for Jenkins info updated : duration = " + ( t2 - t1 ) + "ms" );
    }
}
