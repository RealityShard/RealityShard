/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.apache.mina.core.session.IoSession;


/**
 * Interface for any network layer objects
 * 
 * @author _rusty
 */
public interface NetworkLayer
{
   
    
    /**
     * Create a new client
     * 
     * @param       protocolName            The name of the protocol the client will use
     * @param       IP                      The IP of the new client
     * @param       port                    The port of the new client
     * @return      The IoSession of the client if successful, or null if the client
     *              connection could not be created.
     * @throws      IOException             If the client could not be created
     */
    public NetworkSession tryCreateClient(String protocolName, String IP, int port)
            throws IOException;
    
    
    /**
     * Add a new listener to the specified port and run it.
     * The network manager does not know what the protocol is, it
     * just uses it to sign the packages.
     * 
     * @param       protocolName            The name of the protocol to be able to parse packets
     *                                      from clients connected to this server
     * @param       port                    The port of the protocol
     * @throws      IOException             If we could not create the listener
     */
    public void addNetworkListener(String protocolName, int port)
        throws IOException;
    
    
    /**
     * Setter.
     * 
     * @param       appLayer                The application layer that the netman
     *                                      will invoke when receiving new packets
     *                                      or accepting new clients.
     */
    public void setApplicationLayer(ApplicationLayer appLayer);
    
    
    /**
     * Getter.
     * 
     * @return      This server's own ip address.
     */
    public String getHostAddress();
}
