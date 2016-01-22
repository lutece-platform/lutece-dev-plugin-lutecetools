package fr.paris.lutece.plugins.lutecetools.service;

import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser 
{
	public static String selectFile( )
	{
		String path = ""; 
		JFileChooser f;

		if ( Global._strFileChooserPath.isEmpty( ) )
		{
			f = new JFileChooser( );
		}
		else
		{
			f = new JFileChooser( Global._strFileChooserPath );
		}
		
		f.setFileSelectionMode( JFileChooser.FILES_ONLY );
		f.setFileFilter(new FileNameExtensionFilter("Xml files","xml"));
		
		int result = f.showSaveDialog( null );
		
		if ( f.getSelectedFile() != null && result == JFileChooser.APPROVE_OPTION )
		{
			path = f.getSelectedFile( ).toString( );
			File file = new File( path );
			if ( file.exists( ) )
			{
				Global._strFileChooserPath = path;
			}
			else
			{
				Global._strFileChooserPath = "";
			}
		}
		else if ( result == JFileChooser.CANCEL_OPTION )
		{
			Global._strFileChooserPath = "";
		}
			
		return path ;
	}
	
	public static String selectDir( )
	{
		String path = ""; 
		JFileChooser f;

		if ( Global._strDirChooserPath.isEmpty( ) )
		{
			f = new JFileChooser( );
		}
		else
		{
			f = new JFileChooser( Global._strDirChooserPath );
		}
		
		f.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		
		int result = f.showSaveDialog( null );
		
		if ( f.getSelectedFile() != null && result == JFileChooser.APPROVE_OPTION )
		{
			path = f.getSelectedFile( ).toString( );
			File dir = new File( path );
			if ( dir.exists( ) )
			{
				Global._strDirChooserPath = path;
			}
		}
		else if ( result == JFileChooser.CANCEL_OPTION )
		{
			Global._strDirChooserPath = "";
		}
			
		return path ;
	}
}
