/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.events;

import io.netty.channel.Channel;


/**
 * Triggered when a channel disconnects, and contains this exact channel.
 * 
 * @author _rusty
 */
public final class NetworkClientDisconnectedEvent implements Event
{
    
    private final Channel channel;
    

    public NetworkClientDisconnectedEvent(Channel channel)
    {
        this.channel = channel;
    }


    public Channel getChannel() 
    {
        return channel;
    }
}
