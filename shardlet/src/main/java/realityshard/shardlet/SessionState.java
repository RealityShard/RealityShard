/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
     * @param       buffer                  The buffer that will be filtered
     * @return      The enumeration that contains all resulting actions.
     *              The filter may store any incomplete actions inside, so that
     *              you don't need to return broken packets / fragments.
     */
    List<Action> doInFilter(ByteBuffer buffer);


    /**
     * Filters an outgoing action / network message
     *
     * @param       action                  The action that will be filtered
     * @return      In contrast to the doInFilter method, actions/packets
     *              coming from the game-apps are not fragmented and completely
     *              serialized in one go.
     *              Don't forget to flip the buffer ;)
     */
    ByteBuffer doOutFilter(Action action);
    
    
    public class Invalid implements SessionState
    {
        @Override
        public List<Action> doInFilter(ByteBuffer buffer)
        {
            throw new IllegalStateException("Cannot filter incoming data. This session is new or was kicked.");
        }

        @Override
        public ByteBuffer doOutFilter(Action action)
        {
            throw new IllegalStateException("Cannot filter outgoing data. This session is new or was kicked.");
        }
    }


    public class NoFilter implements SessionState
    {
        @Override
        public List<Action> doInFilter(ByteBuffer buffer)
        {
            return new ArrayList<>();
        }

        @Override
        public ByteBuffer doOutFilter(Action action)
        {
            return ByteBuffer.allocate(0);
        }
    }
}
