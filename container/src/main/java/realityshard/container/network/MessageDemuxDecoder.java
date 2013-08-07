/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import realityshard.container.gameapp.GameAppContext;


/**
 * This handler demultiplexes the incoming messages to the event-aggregators
 * of the game-apps of the channel.
 * 
 * @author _rusty
 */
public class MessageDemuxDecoder extends MessageToMessageDecoder<Message>
{

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception 
    {
        // get the specific game app context this event belongs to
        GameAppContext context = ctx.channel().attr(GameAppContextKey.KEY).get();
        
        // and let the context decide what to do with the message
        context.handleMessage(msg);
        
        // we dont break the cycle here - maybe other handlers sit behind this one
        out.add(msg);
    }

}
