/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network;

import java.nio.ByteBuffer;


/**
 * A class implementing this interface is able to handle raw network messages
 * and network client state updates.
 * 
 * @author _rusty
 */
public interface ApplicationLayer 
{
    
    /**
     * Handle an incoming packet.
     * 
     * @param       session                 The IoSession that this packet was
     *                                      received on.
     * @param       buffer                  The Buffer with the data.
     */
    public void handlePacket(NetworkSession session, ByteBuffer buffer);
    
    
    /**
     * Handle a new client
     * 
     * @param       session                 The new IoSession
     * @param       protocolName            The name of the protocol the client uses
     * @param       IP                      The IP of the new client
     * @param       port                    The port of the new client
     */
    public void newClient(NetworkSession session, String protocolName, String IP, int port);
    
    
    /**
     * A client disconnected
     * 
     * @param session 
     */
    public void lostClient(NetworkSession session);
}
