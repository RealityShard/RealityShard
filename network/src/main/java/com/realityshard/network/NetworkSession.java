/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network;

import java.nio.ByteBuffer;


/**
 * This resembles a single network session.
 * 
 * @author _rusty
 */
public interface NetworkSession 
{
    
    /**
     * Handle a single packet (given as bytebuffer)
     * 
     * @param       buffer
     */
    public void handlePacket(ByteBuffer buffer);
    
    
    /**
     * Forcefully disconnect this session.
     */
    public void disconnect();
}
