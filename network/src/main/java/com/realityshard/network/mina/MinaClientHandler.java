/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network.mina;

import com.realityshard.network.NetworkSession;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A client handler implementation for usage with the apache mina framework
 * 
 * @author _rusty
 */
public class MinaClientHandler extends IoHandlerAdapter
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MinaClientHandler.class);
    
    private final MinaNetworkManager netManager;
    private final String protocol;
    
    
    /**
     * Constructor.
     * 
     * @param       netManager 
     * @param       protocol 
     */
    public MinaClientHandler(MinaNetworkManager netManager, String protocol)
    {
        this.netManager = netManager;
        this.protocol = protocol;
    }
    
    
    /**
     * In case of exception with the acceptor...
     * 
     * @param       session
     * @param       exc 
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable exc)
    {
        //LOGGER.error("Session exception!", exc);
    }
    
    
    /**
     * Message handler.
     * 
     * @param       session
     * @param       message 
     */
    @Override
    public void messageReceived(IoSession session, Object message)
    {
        ByteBuffer buff = ((IoBuffer) message).buf();
        
        buff.order(ByteOrder.LITTLE_ENDIAN);
        
        // inform the application layer
        netManager.onNewPacket((NetworkSession) session.getAttribute("adapter"), buff);
    }
    
    
    /**
     * New Session handler
     * 
     * @param       session 
     */
    @Override
    public void sessionCreated(IoSession session)
    {
        LOGGER.debug("Session created on {}.", protocol);
        
        // create a new adapter for this new session
        MinaSessionAdapter netSession = new MinaSessionAdapter(session);
        
        // and attach it to the session
        session.setAttribute("adapter", netSession);
        
        InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress();
        // inform the application layer
        netManager.onNewClient(
                    netSession, 
                    protocol, 
                    addr.getAddress().getHostAddress(), 
                    addr.getPort());
    }
    
    
    /**
     * Session closed handler
     * 
     * @param       session 
     */
    @Override
    public void sessionClosed(IoSession session)
    {
        LOGGER.debug("Session closed on {}.", protocol);
        
        netManager.onLostClient((NetworkSession) session.getAttribute("adapter"));
    }
}
