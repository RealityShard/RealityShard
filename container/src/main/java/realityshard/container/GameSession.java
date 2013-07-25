/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import realityshard.shardlet.gameapp.GameAppContext;
import realityshard.network.NetEventHandlers;
import realityshard.shardlet.utils.GenericSession;
import realityshard.network.NetworkSession;
import realityshard.shardlet.Action;
import realityshard.shardlet.SessionState;
import realityshard.shardlet.ShardletContext;
import realityshard.shardlet.TriggerableAction;
import realityshard.shardlet.utils.GenericTriggerableAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a shardlet.Session implementation.
 * This class is used to identify a client.
 * 
 * @author _rusty
 */
public final class GameSession extends GenericSession implements
        NetEventHandlers.NewData,
        NetEventHandlers.LostClient
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    
    private final NetworkSession netSession;
    private final SessionState protocol;
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
            SessionState protocol)
    {
        super(IP, port, protocolName);
        this.netSession = netSession;
        this.protocol = protocol;
        this.context = context;
    }
    
    
    /**
     * Called by the network layer when we got new data.
     * 
     * @param       rawData
     */
    @Override
    public void onNewData(ByteBuffer rawData)
    {
        GenericTriggerableAction action = new GenericTriggerableAction();
        action.init(this);
        action.setBuffer(rawData);

        try 
        {
            // filter it, and delegate it
            for (TriggerableAction act : protocol.doInFilter(action)) 
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
     * Called by the network layer when a client disconnects
     */
    @Override
    public void onLostClient() 
    {
        // unregister the session with its context
        context.handleLostClient(this);
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
            netSession.write(buf);
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
    public void kick() 
    {
        netSession.disconnect();
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
