/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network.mina;

import com.realityshard.network.GenericNetworkManager;
import com.realityshard.network.NetworkSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements a TCP based server, using the apache mina framework.
 * 
 * @author _rusty
 */
public class MinaNetworkManager extends GenericNetworkManager 
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MinaNetworkManager.class);
    
    private final static Map<String, MinaClientHandler> handlers = new ConcurrentHashMap<>();
    private final static Collection<NioSocketAcceptor> acceptors = new ArrayList<>();
    
    
    /**
     * Add a new client acceptor on a given port.
     * This basically follows the TCP server example of the
     * apache mina framework.
     * 
     * @param       protocol 
     * @param       port
     */
    @Override
    public void addAcceptor(String protocol, int port)
    {
        if (handlers.containsKey(protocol))
        {
            LOGGER.error("Protocol already registered: [name {} | port {}]", protocol, port);
            return;
        }
        
        // create a new acceptor, that will listen for clients on the 
        // specified port
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        
        // create the handler
        MinaClientHandler handler = new MinaClientHandler(this, protocol);
        acceptor.setHandler(new MinaClientHandler(this, protocol));
        handlers.put(protocol, handler);
        
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        
        try 
        {
            // start the acceptor:
            acceptor.bind(new InetSocketAddress(port));
        } 
        catch (IOException ex) 
        {
            LOGGER.error("Got a problem starting the Mina network acceptor.", ex);
        }
        
        // add the acceptor to our list:
        acceptors.add(acceptor);
        
        LOGGER.debug("Registered new protocol: [name {} | port {}]", protocol, port);
    }
    
    
    /**
     * Try to establish a new connection by using a connector.
     * 
     * @param       protocol                The protocol that the connection will be using
     * @param       remoteAddr              The remote IP address or hostname
     * @param       port                    The remote port
     * @return      Null if no session could be created
     */
    @Override
    public NetworkSession addConnector(String protocol, String remoteAddr, int port)
    {
        if (!handlers.containsKey(protocol))
        {
            LOGGER.error("Unknown protocol: [name {}]. Could not create session.", protocol);
            return null;
        }
        
        // create a new connector, that will be used to create the session
        // to a specific client
        NioSocketConnector connector = new NioSocketConnector();
        
        MinaClientHandler handler = handlers.get(protocol);
        connector.setHandler(handler);
        
        IoSession session = null;
        try
        {
            ConnectFuture con = connector.connect(new InetSocketAddress(InetAddress.getByName(remoteAddr), port));
            con.awaitUninterruptibly(500);
            
            session = con.getSession();
        }
        catch (UnknownHostException | RuntimeException e)
        {
            LOGGER.error(String.format("Could not connect to: [ip {}]", remoteAddr), e);
        }
        
        if (session != null)
        {
            LOGGER.debug(String.format("Registered new session: [name {} | ip {} | port {}]", protocol, remoteAddr, port));
            return new MinaSessionAdapter(session);
        }
        
        return null;
    }
    
    
    /**
     * Shutdown the acceptors.
     */
    @Override
    public void shutdown()
    {
        for (NioSocketAcceptor acceptor : acceptors) 
        {
            acceptor.unbind();
        }
    }
}
