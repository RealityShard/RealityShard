/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.utils;

import realityshard.shardlet.SessionState;
import realityshard.shardlet.Action;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class holds a list of ProtocolFilters and executes the
 * chain if it recieves a doIn/OutFilter request
 *
 * @author _rusty
 */
public class FilterChainState implements SessionState
{

    private final List<SessionState> incomingFilters = new ArrayList<>();
    private final List<SessionState> outgoingFilters = new ArrayList<>();


    /**
     * Convenience method. Add an incoming filter.
     *
     * @param       incoming
     * @return      This object, so to be able to chain these methods
     */
    public FilterChainState addInFilter(SessionState incoming)
    {
        incomingFilters.add(incoming);
        return this;
    }


    /**
     * Convenience method. Add an outgoing filter.
     *
     * @param       outgoing
     * @return      This object, so to be able to chain these methods
     */
    public FilterChainState addOutFilter(SessionState outgoing)
    {
        incomingFilters.add(outgoing);
        return this;
    }


    /**
     * Executes the filter chain by calling the doInFilter() method
     * on each Filter.
     *
     * @param       action                  The action (aka Packet) that needs
     *                                      to be processed.
     * @throws      IOException             If any filter threw it.
     */
    @Override
    public List<Action> doInFilter(Action action)
    {
        // this system should work the following way:
        // we have a new action for the start, and create a temporary list out of that action
        // to save code later on.

        // for each filter in our list, we take the actions in the result list
        // and feed it to the filter one after another
        // each action may produce a new list of actions, so we need to temporarily store that list
        // then when the filter has processed all action of our list,
        // we'll save the content of our temporarily created list inside the result list
        // that will be used as input for the next filter.

        // init the result list (that will transfer the actions between the filters)
        List<Action> result = new ArrayList<>(Arrays.asList(action));

        // we need to save the results temporarily, so lets use a new list:
        List<Action> tmpResult = new ArrayList<>();

        for (SessionState filter : incomingFilters)
        {
            // pass the action through the filters
            // note that "result" may be filled with other actions that completed suddenly
            // so we need to process each of them separately
            for (Action tmpAction : result)
            {
                // also, we cannot modify result while it is processed, so
                // we'll temporarily save the results:
                tmpResult.addAll(filter.doInFilter(tmpAction));
            }

            // now, after processing the filter, we can copy the temporary stuff into our
            // "result" array
            result = tmpResult;

            // and now we can do the whole stuff for the next filter
        }

        // finally we can return the result
        return result;
    }


    /**
     * Executes the filter chain by calling the doOutFilter() method
     * on each Filter.
     *
     * @param       action                  The action (aka Packet) that needs
     *                                      to be processed.
     * @throws      IOException             If any filter threw it.
     */
    @Override
    public Action doOutFilter(Action action)
    {
        Action result = null;

        // do the usual filtering (less complex because theres only ONE action)
        for (SessionState protocolFilter : outgoingFilters)
        {
            // the action will be transformed by the filter
            result = protocolFilter.doOutFilter(action);
        }

        return result;
    }

}
