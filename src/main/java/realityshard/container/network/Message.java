/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.channel.Channel;
import realityshard.container.events.Event;


/**
 * Encapsulates a message from a client
 * (All messages are events at the same time)
 * 
 * @author _rusty
 */
public class Message implements Event
{
    
    private Channel channel;
    
    
    /**
     * Initialize the Action by setting the channel object that
     * this it will be distributed to, or that it is coming from.
     * 
     * @param       channel                 The channel that the message originates from
     */
    public void init(Channel channel)
    {
        this.channel = channel;
    }
    
    
    /**
     * Getter.
     * 
     * @return      The network session object.
     */
    public Channel getChannel()
    {
        return channel;
    }
}
