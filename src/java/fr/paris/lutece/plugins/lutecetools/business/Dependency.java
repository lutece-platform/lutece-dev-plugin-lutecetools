/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.lutecetools.business;


/**
 *
 * @author pierre
 */
public class Dependency
{
    private String _strGroupId;
    private String _strArtifactId;
    private String _strVersion;
    private String _strComponentType;

    /**
     * Returns the GroupId
     * @return The GroupId
     */
    public String getGroupId(  )
    {
        return _strGroupId;
    }

    /**
     * Sets the GroupId
     * @param strGroupId The GroupId
     */
    public void setGroupId( String strGroupId )
    {
        _strGroupId = strGroupId;
    }

    /**
     * Returns the ArtifactId
     * @return The ArtifactId
     */
    public String getArtifactId(  )
    {
        return _strArtifactId;
    }

    /**
     * Sets the ArtifactId
     * @param strArtifactId The ArtifactId
     */
    public void setArtifactId( String strArtifactId )
    {
        _strArtifactId = strArtifactId;
    }

    /**
     * Returns the Version
     * @return The Version
     */
    public String getVersion(  )
    {
        return _strVersion;
    }

    /**
     * Sets the Version
     * @param strVersion The Version
     */
    public void setVersion( String strVersion )
    {
        _strVersion = strVersion;
    }

    /**
     * Returns the ComponentType
     * @return The ComponentType
     */
    public String getComponentType(  )
    {
        return _strComponentType;
    }

    /**
     * Sets the ComponentType
     * @param strComponentType The ComponentType
     */
    public void setComponentType( String strComponentType )
    {
        _strComponentType = strComponentType;
    }
}
