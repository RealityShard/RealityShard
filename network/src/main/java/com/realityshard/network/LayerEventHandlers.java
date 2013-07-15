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
public interface LayerEventHandlers 
{
    
    public interface NewPacket
    {
        
        /**
         * Handle an incoming packet.
         * 
         * @param       session                 The IoSession that this packet was
         *                                      received on.
         * @param       buffer                  The Buffer with the data.
         */
        public void onNewPacket(NetworkSession session, ByteBuffer buffer);
    }
    
    
    public interface NewClient
    {
        
        /**
         * Handle a new client
         * 
         * @param       session                 The new IoSession
         * @param       protocolName            The name of the protocol the client uses
         * @param       IP                      The IP of the new client
         * @param       port                    The port of the new client
         */
        public void onNewClient(NetworkSession session, String protocolName, String IP, int port);
    }
    
    
    public interface LostClient
    {
        /**
         * A client disconnected
         * 
         * @param session 
         */
        public void onLostClient(NetworkSession session);
    }
}
