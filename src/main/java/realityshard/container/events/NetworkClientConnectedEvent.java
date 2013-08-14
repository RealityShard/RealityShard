/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.events;

import io.netty.channel.Channel;


/**
 * Triggered when a client connects, by the channel that was just created,
 * and contains this exact channel.
 * 
 * @author _rusty
 */
public final class NetworkClientConnectedEvent implements Event
{
    
    private final Channel channel;
    
    
    public NetworkClientConnectedEvent(Channel channel)
    {
        this.channel = channel;
    }

    
    public Channel getChannel() 
    {
        return channel;
    }    
}
