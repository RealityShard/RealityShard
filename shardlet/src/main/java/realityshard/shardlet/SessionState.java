/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.io.IOException;
import java.util.List;


/**
 * Session state is a state pattern implementation for a Session.
 * This can be used to filter the input of a Session.
 * 
 * @author _rusty
 */
public interface SessionState
{
    /**
     * Filters an incoming action / network message
     * 
     * @param       action                  The action that will be filtered
     * @return      The enumeration that contains all resulting actions.
     *              The filter may store any incomplete actions inside, so that
     *              you don't need to return broken packets / fragments.
     * @throws      IOException             The usual I/O stuff
     */
    public List<Action> doInFilter(Action action)
            throws IOException;
    
    
    /**
     * Filters an outgoing action / network message
     * 
     * @param       action                  The action that will be filtered
     * @return      In contrast to the doInFilter method, actions/packets 
     *              coming from the game-apps are not fragmented and completely
     *              deserialized, so one deserilized action also results in one
     *              serialized action/packet
     * @throws      IOException             The usual I/O stuff
     */
    public Action doOutFilter(Action action)
            throws IOException;
}
