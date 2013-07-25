/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.io.IOException;
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

    public class Invalid implements SessionState
    {
        @Override
        public List<Action> doInFilter(Action action)
        {
            throw new IllegalStateException("Cannot filter incoming data. This session is new or was kicked.");
        }

        @Override
        public Action doOutFilter(Action action)
        {
            throw new IllegalStateException("Cannot filter outgoing data. This session is new or was kicked.");
        }
    }


    public class NoFilter implements SessionState
    {
        @Override
        public List<Action> doInFilter(Action action)
        {
            List<Action> result = new ArrayList<>();
            result.add(action);

            return result;
        }

        @Override
        public Action doOutFilter(Action action)
        {
            return action;
        }
    }


    /**
     * Filters an incoming action / network message
     *
     * @param       action                  The action that will be filtered
     * @return      The enumeration that contains all resulting actions.
     *              The filter may store any incomplete actions inside, so that
     *              you don't need to return broken packets / fragments.
     * @throws      IOException             The usual I/O stuff
     */
    public List<Action> doInFilter(Action action);


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
    public Action doOutFilter(Action action);
}
