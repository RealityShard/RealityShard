/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network;

import com.realityshard.network.mina.MinaNetworkManager;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A concurrent implementation of a network layer object.
 * This handles incoming and outgoing data by usage of 
 * * ServerSocketChannels (as protocol-specific listeners)
 * * SocketChannels (as the client connections)
 * * Selectors (for managing the channels)
 * 
 * CAUTION!
 * The server sockets SelectionKey attachment is always its protocol name!
 * The socket channel SelectionKey attachment is always its UUID!
 * 
 * @author _rusty
 */
public final class NetworkFacade
    implements NetworkLayer
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(NetworkFacade.class);
    private final String ip;
   
    
    /**
     * Constructor.
     * 
     * @param       ip                      The server's own adapter
     */
    public NetworkFacade(String ip) 
    {
        this.ip = ip;
    }
    
    
    /**
     * Try to create a new client connection
     * 
     * @param       protocolName            The name of the protocol the client will use
     * @param       IP                      The IP of the new client
     * @param       port                    The port of the new client
     * @throws      IOException             If the client could not be created
     */
    @Override
    public NetworkSession tryCreateClient(String protocolName, String IP, int port)
            throws IOException
    {
        return MinaNetworkManager.addConnector(protocolName, IP, port);
    }

    
    /**
     * Add a new listener to the specified port and run it.
     * The network manager does not know what the protocol is, it
     * just uses it to sign the packages.
     * 
     * @param       protocolName            The name of the protocol
     * @param       port                    The port of the protocol
     */
    @Override
    public void addNetworkListener(String protocolName, int port)
            throws IOException
    {
        MinaNetworkManager.addAcceptor(protocolName, port);
    }

    
    /**
     * Setter.
     * Used only once, when the container is registered with this manager.
     * 
     * @param       appLayer               The connector that this network manager
     *                                      will output stuff to
     */
    @Override
    public void setApplicationLayer(ApplicationLayer appLayer) 
    {
        MinaNetworkManager.init(appLayer);
    }

    
    /**
     * Getter.
     * 
     * @return 
     */
    @Override
    public String getHostAddress() 
    {
        return ip;
    }
    
    
    /**
     * Shutdown this network server and kill all remaining connections.
     */
    public void shutdown()
    {
        MinaNetworkManager.shutdown();
    }
}
