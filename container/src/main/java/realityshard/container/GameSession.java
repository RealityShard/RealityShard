/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import realityshard.container.gameapp.GameAppContext;
import realityshard.network.NetEventHandlers;
import realityshard.shardlet.utils.GenericSession;
import realityshard.network.NetworkSession;
import realityshard.shardlet.Action;
import realityshard.shardlet.ShardletContext;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.shardlet.utils.GenericAction;


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
            String protocolName)
    {
        super(IP, port, protocolName);
        this.netSession = netSession;
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
        // filter it, and delegate it
        for (Action act : getState().doInFilter(rawData))
        {
            context.handleIncoming(act);
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
        // let the state handle the action first
        ByteBuffer buf = getState().doOutFilter(action);
        buf.flip();

        // then send it.
        netSession.write(buf);
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
