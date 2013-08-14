/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realityshard.container.events.NetworkClientConnectedEvent;
import realityshard.container.events.NetworkClientDisconnectedEvent;
import realityshard.container.gameapp.GameAppContext;


/**
 * Notifies the context of a channel when it connected or disconnected
 * 
 * @author _rusty
 */
public class ConnectionStateHandler extends ChannelInboundHandlerAdapter
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionStateHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception 
    {
        LOGGER.debug("Got a new client!");
        
        // trigger the event
        GameAppContext context = ctx.channel().attr(GameAppContextKey.KEY).get();
        context.trigger(new NetworkClientConnectedEvent(ctx.channel()));
        
        // and make sure the pipeline is not interrupted
        super.channelActive(ctx);
    }

    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception 
    {
        LOGGER.debug("Lost a client!");
        
        // trigger the event
        GameAppContext context = ctx.channel().attr(GameAppContextKey.KEY).get();
        context.trigger(new NetworkClientDisconnectedEvent(ctx.channel()));
        
        // and make sure the pipeline is not interrupted
        super.channelInactive(ctx);
    }
}
