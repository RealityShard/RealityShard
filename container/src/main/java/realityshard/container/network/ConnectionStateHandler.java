/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception 
    {
        // trigger the event
        GameAppContext context = ctx.channel().attr(GameAppContextKey.KEY).get();
        context.trigger(new NetworkClientConnectedEvent(ctx.channel()));
        
        // and make sure the pipeline is not interrupted
        super.channelRegistered(ctx);
    }

    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception 
    {
        // trigger the event
        GameAppContext context = ctx.channel().attr(GameAppContextKey.KEY).get();
        context.trigger(new NetworkClientDisconnectedEvent(ctx.channel()));
        
        // and make sure the pipeline is not interrupted
        super.channelUnregistered(ctx);
    }
}
