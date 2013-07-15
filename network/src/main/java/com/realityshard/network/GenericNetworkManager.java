/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A manager for the entire network layer.
 * Every specific manager should use this as a basis for its communications.
 * 
 * @author _rusty
 */
public abstract class GenericNetworkManager
    implements NetworkLayer
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GenericNetworkManager.class);
    
    private LayerEventHandlers.NewPacket newPacket;
    private LayerEventHandlers.NewClient newClient;
    private LayerEventHandlers.LostClient lostClient;
    
    
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
        // template method
        return this.addConnector(protocolName, IP, port);
    }

    protected abstract NetworkSession addConnector(String protocolName, String IP, int port);
    
    
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
        // template method
        this.addAcceptor(protocolName, port);
    }
    
    protected abstract void addAcceptor(String protocolName, int port);
    
    
    /**
     * Shutdown this network server and kill all remaining connections.
     */
    @Override
    public abstract void shutdown();
    

    /**
     * Register an event handler.
     * 
     * @param newPacket 
     */
    @Override
    public void RegisterOnNewPacket(LayerEventHandlers.NewPacket newPacket) 
    {
        this.newPacket = newPacket;
    }

    
    /**
     * Register an event handler.
     * 
     * @param newClient 
     */
    @Override
    public void RegisterOnNewClient(LayerEventHandlers.NewClient newClient) 
    {
        this.newClient = newClient;
    }

    
    /**
     * Register an event handler.
     * 
     * @param lostClient 
     */
    @Override
    public void RegisterOnLostClient(LayerEventHandlers.LostClient lostClient) 
    {
        this.lostClient = lostClient;
    }
    
    
    /**
     * Convenience for the subclasses.
     */
    public void onNewPacket(NetworkSession session, ByteBuffer buffer)
    {
        newPacket.onNewPacket(session, buffer);
    }
    
    
    /**
     * Convenience for the subclasses.
     */
    public void onNewClient(NetworkSession session, String protocolName, String ip, int port)
    {
        newClient.onNewClient(session, protocolName, ip, port);
    }
    
    
    /**
     * Convenience for the subclasses.
     */
    public void onLostClient(NetworkSession session)
    {
        lostClient.onLostClient(session);
    }
}
