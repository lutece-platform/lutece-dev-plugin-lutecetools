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

import fr.paris.lutece.plugins.lutecetools.business.Component;

import java.util.List;

/**
 *
 * @author pierre
 */
public class ComponentsInfos
{
    private int _nComponentCount;
    private int _nComponentAvailable;
    private List<Component> _listComponents;
    private boolean _bComplete;

    /**
     * @return the nComponentCount
     */
    public int getComponentCount( )
    {
        return _nComponentCount;
    }

    /**
     * @param nComponentCount
     *            the nComponentCount to set
     */
    public void setComponentCount( int nComponentCount )
    {
        _nComponentCount = nComponentCount;
    }

    /**
     * @return the nComponentAvailable
     */
    public int getComponentAvailable( )
    {
        return _nComponentAvailable;
    }

    /**
     * @param nComponentAvailable
     *            the nComponentAvailable to set
     */
    public void setComponentAvailable( int nComponentAvailable )
    {
        _nComponentAvailable = nComponentAvailable;
    }

    /**
     * @return the listComponents
     */
    public List<Component> getListComponents( )
    {
        return _listComponents;
    }

    /**
     * @param listComponents
     *            the listComponents to set
     */
    public void setListComponents( List<Component> listComponents )
    {
        _listComponents = listComponents;
    }

    /**
     * @return the bComplete
     */
    public boolean isComplete( )
    {
        return _nComponentAvailable == _nComponentCount;
    }

    public int getPercentAvailable( )
    {
        if ( _nComponentCount == 0 )
        {
            return 100;
        }

        return ( _nComponentAvailable * 100 ) / _nComponentCount;
    }
}
