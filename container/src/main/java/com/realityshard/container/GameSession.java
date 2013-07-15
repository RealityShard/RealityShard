/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.container;

import com.realityshard.container.gameapp.GameAppContext;
import com.realityshard.shardlet.utils.GenericSession;
import com.realityshard.network.NetworkSession;
import com.realityshard.shardlet.Action;
import com.realityshard.shardlet.ProtocolFilter;
import com.realityshard.shardlet.ShardletContext;
import com.realityshard.shardlet.TriggerableAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a shardlet.Session implementation.
 * This class is used to identify a client.
 * 
 * @author _rusty
 */
public final class GameSession extends GenericSession
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    
    private final NetworkSession netSession;
    private final ProtocolFilter protocol;
    private GameAppContext context;
    
    /**
     * Constructor.
     * 
     * @param       netSession              The network session of this game session
     * @param       context                 The context that this session belongs to
     * @param       IP                      The IP of the client connection
     * @param       port                    The port of the client connection
     * @param       protocolName            The protocol name of protocol this session
     *                                      uses internally.
     * @param       protocol                The protocol this session uses internally.
     *                                      Note that this is usually a <code>ProtocolChain</code>
     */
    public GameSession(
            NetworkSession netSession,
            GameAppContext context,
            String IP, 
            int port, 
            String protocolName, 
            ProtocolFilter protocol)
    {
        super(IP, port, protocolName);
        this.netSession = netSession;
        this.protocol = protocol;
        this.context = context;
    }
    
    
    /**
     * Send an action to the client associated with this session
     * 
     * @param action 
     */ 
    @Override
    public void send(Action action)
    {
        try 
        {
            // let the protocol handle the action first
            Action act = protocol.doOutFilter(action);
            
            ByteBuffer buf = act.getBuffer();
            buf.flip();
            
            // then send it.
            netSession.handlePacket(buf);
        } 
        catch (IOException ex) 
        {
            LOGGER.error("Protocol failed to handle an action.", ex);
        }
    }
    
    
    /**
     * Handles an new action
     * 
     * @param action 
     */
    public void receive(TriggerableAction action)
    {
        try 
        {
            // let the protocol handle the action first
            List<TriggerableAction> actions = protocol.doInFilter(action);
            
            for (TriggerableAction act : actions) 
            {
                context.handleIncomingAction(act);
            }
        } 
        catch (IOException ex) 
        {
            LOGGER.error("Protocol failed to handle an action.", ex);
        }
    }
    

    /**
     * Invalidates this session, leading to the client socket channel being
     * kicked from the network interface.
     */
    @Override
    public void invalidate() 
    {
        netSession.disconnect();
    }   

    
    /**
     * Getter.
     * 
     * @return 
     */
    public NetworkSession getNetSession() 
    {
        return netSession;
    }

    
    /**
     * Getter.
     * 
     * @return 
     */
    @Override
    public ShardletContext getShardletContext() 
    {
        return context;
    }
    
    
    /**
     * Setter.
     */
    public void setShardletContext(GameAppContext context)
    {
        this.context = context;
    }
}
