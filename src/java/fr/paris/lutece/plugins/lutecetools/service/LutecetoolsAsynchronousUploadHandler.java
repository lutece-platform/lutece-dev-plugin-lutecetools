/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.asynchronousupload.service.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.filesystem.UploadUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

public class LutecetoolsAsynchronousUploadHandler extends AbstractAsynchronousUploadHandler
{

    private static final String HANDLER_NAME = "lutecetoolsAsynchronousUploadHandler";
    public static final String BEAN_NAME = "lutecetools.lutecetoolsAsynchronousUploadHandler";

    private static final String ERROR_MESSAGE_UNKNOWN_ERROR = "lutecetools.message.unknownError";

    private static Map<String, Map<String, List<FileItem>>> _mapWebtiersUpload = new ConcurrentHashMap<String, Map<String, List<FileItem>>>( );

    /**
     * Vérifie si une liste de fichiers uploadés pour un champ donné sont valides
     * 
     * @param request
     *            La requête effectuée
     * @param strFieldName
     *            Le nom du champ ayant servi à uploadé un fichier.
     * @param listFileItemsToUpload
     *            Liste des fichiers uploadés à verifier.
     * @param locale
     *            La locale à utiliser pour afficher les messages d'erreurs éventuels
     * @return Le message d'erreur, ou null si aucune erreur n'a été détéctée et si les fichiers sont valides.
     */
    @Override
    public String canUploadFiles( HttpServletRequest request, String strFieldName, List<FileItem> listFileItemsToUpload, Locale locale )
    {
        if ( StringUtils.isNotBlank( strFieldName ) )
        {
            initMap( request.getSession( ).getId( ), strFieldName );
            List<FileItem> listUploadedFileItems = getListUploadedFiles( strFieldName, request.getSession( ) );

            if ( !listFileItemsToUpload.isEmpty( ) && listFileItemsToUpload.size( ) == 1 && listUploadedFileItems.isEmpty( )
                    && compareTo( "text/", listFileItemsToUpload.get( 0 ).getContentType( ) ) )
            {
                return null;
            }
        }
        return I18nService.getLocalizedString( ERROR_MESSAGE_UNKNOWN_ERROR, locale );
    }

    /**
     * Permet de récupérer la liste des fichiers uploadés pour un champ donné. La liste doit être ordonnée chronologiquement par date d'upload. A chaque fichier
     * un index sera associé correspondant à l'index du fichier dans la liste (le fichier le plus vieux aura l'index 0). Deux appels successifs de cette méthode
     * doivent donc renvoyés une liste ordonnées de la même manière.
     * 
     * @param strFieldName
     *            Le nom du champ dont on souhaite récupérer les fichiers
     * @param session
     *            la session de l'utilisateur utilisant le fichier. A n'utiliser que si les fichiers sont enregistrés en session.
     * @return La liste des fichiers uploadés pour le champ donné
     */
    @Override
    public List<FileItem> getListUploadedFiles( String strFieldName, HttpSession session )
    {
        if ( StringUtils.isBlank( strFieldName ) )
        {
            throw new AppException( "id field name is not provided for the current file upload" );
        }

        initMap( session.getId( ), strFieldName );

        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapWebtiersUpload.get( session.getId( ) );

        return mapFileItemsSession.get( strFieldName );
    }

    /**
     * Permet de supprimer un fichier précédament uploadé
     * 
     * @param strFieldName
     *            Le nom du champ
     * @param session
     *            la session de l'utilisateur utilisant le fichier. A n'utiliser que si les fichiers sont enregistrés en session.
     * @param nIndex
     *            L'index du fichier dans la liste des fichiers uploadés.
     */
    @Override
    public void removeFileItem( String strFieldName, HttpSession session, int nIndex )
    {
        List<FileItem> uploadedFiles = getListUploadedFiles( strFieldName, session );

        if ( ( uploadedFiles != null ) && !uploadedFiles.isEmpty( ) && ( uploadedFiles.size( ) > nIndex ) )
        {
            // Remove the object from the Hashmap
            FileItem fileItem = uploadedFiles.remove( nIndex );
            fileItem.delete( );
        }
    }

    /**
     * Permet de déclarer un fichier comme uploadé. L'implémentation de cette méthode est désormais en charge de la gestion du fichier.
     * 
     * @param fileItem
     *            Le fichier uploadé
     * @param strFieldName
     *            Le nom du champ auquel le fichier est associé
     * @param request
     *            La requête
     */
    @Override
    public void addFileItemToUploadedFilesList( FileItem fileItem, String strFieldName, HttpServletRequest request )
    {
        // This is the name that will be displayed in the form. We keep
        // the original name, but clean it to make it cross-platform.
        String strFileName = UploadUtil.cleanFileName( fileItem.getName( ).trim( ) );

        initMap( request.getSession( ).getId( ), strFieldName );

        // Check if this file has not already been uploaded
        List<FileItem> uploadedFiles = getListUploadedFiles( strFieldName, request.getSession( ) );

        if ( uploadedFiles != null )
        {
            boolean bNew = true;

            if ( !uploadedFiles.isEmpty( ) )
            {
                Iterator<FileItem> iterUploadedFiles = uploadedFiles.iterator( );

                while ( bNew && iterUploadedFiles.hasNext( ) )
                {
                    FileItem uploadedFile = iterUploadedFiles.next( );
                    String strUploadedFileName = UploadUtil.cleanFileName( uploadedFile.getName( ).trim( ) );
                    // If we find a file with the same name and the same
                    // length, we consider that the current file has
                    // already been uploaded
                    bNew = !( StringUtils.equals( strUploadedFileName, strFileName ) && ( uploadedFile.getSize( ) == fileItem.getSize( ) ) );
                }
            }

            if ( bNew )
            {
                uploadedFiles.add( fileItem );
            }
        }
    }

    /**
     * Permet de définir le nom du handler. Ce nom doit être unique, et ne contenir que des caractères numériques (pas de points, de virgule, ...). Il est
     * recommandé de préfixer le nom du plugin, puis de suffixer un nom fonctionnel. Attention, le nom du handler est différent du nom du bean Spring associé !
     */
    @Override
    public String getHandlerName( )
    {
        return HANDLER_NAME;
    }

    /**
     * Init the map
     * 
     * @param strSessionId
     *            the session id
     * @param strFieldName
     *            the field name
     */
    private void initMap( String strSessionId, String strFieldName )
    {
        // find session-related files in the map
        Map<String, List<FileItem>> mapFileItemsSession = _mapWebtiersUpload.get( strSessionId );

        // create map if not exists
        if ( mapFileItemsSession == null )
        {
            synchronized( this )
            {
                // Ignore double check locking error : assignation and instanciation of objects are separated.
                mapFileItemsSession = _mapWebtiersUpload.get( strSessionId );

                if ( mapFileItemsSession == null )
                {
                    mapFileItemsSession = new ConcurrentHashMap<String, List<FileItem>>( );
                    _mapWebtiersUpload.put( strSessionId, mapFileItemsSession );
                }
            }
        }

        List<FileItem> listFileItems = mapFileItemsSession.get( strFieldName );

        if ( listFileItems == null )
        {
            listFileItems = new ArrayList<FileItem>( );
            mapFileItemsSession.put( strFieldName, listFileItems );
        }
    }

    private Boolean compareTo( String mime, String fileContent )
    {
        // create for check mime type upload image in createImage
        String s = fileContent.substring( 0, mime.length( ) );
        if ( s.equals( mime ) )
            return ( true );
        return ( false );
    }

    public boolean hasFile( HttpServletRequest request, String strFieldName )
    {
        if ( StringUtils.isNotBlank( strFieldName ) )
        {
            List<FileItem> listUploadedFileItems = getListUploadedFiles( strFieldName, request.getSession( ) );

            if ( !listUploadedFileItems.isEmpty( ) )
            {
                return true;
            }
        }
        return false;
    }

    public FileItem getFile( HttpServletRequest request, String strFieldName )
    {
        if ( StringUtils.isNotBlank( strFieldName ) )
        {
            List<FileItem> listUploadedFileItems = getListUploadedFiles( strFieldName, request.getSession( ) );

            if ( !listUploadedFileItems.isEmpty( ) )
            {
                return listUploadedFileItems.get( 0 );
            }
        }
        return null;
    }

}
