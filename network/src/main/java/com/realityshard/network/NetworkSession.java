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
     * Write the ByteBuffer to the underlying network stream
     * 
     * @param       buffer
     */
    public void write(ByteBuffer buffer);
    
    
    /**
     * Forcefully disconnect this session.
     */
    public void disconnect();
    
    
    /**
     * Register a handler for the NewPacket event.
     * 
     * @param       newData                 The handler.
     */
    public void registerOnNewData(LayerEventHandlers.NewData newData);
    
    
    /**
     * Register a handler for the LostClient event.
     * 
     * @param       lostClient               The handlers.
     */
    public void registerOnLostClient(LayerEventHandlers.LostClient lostClient);
}
