/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container;

import realityshard.shardlet.SessionState;
import realityshard.shardlet.Action;
import realityshard.shardlet.TriggerableAction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * This class holds a list of ProtocolFilters and executes the
 * chain if it recieves a doIn/OutFilter request
 * 
 * @author _rusty
 */
public class ProtocolChain implements SessionState
{
    
    private final List<SessionState> incomingFilters;
    private final List<SessionState> outgoingFilters;
    
    
    /**
     * Constructor.
     * 
     * Note that filters given to this Constructor should have been
     * initialized by calling init() on them before using this.
     * 
     * @param       incomingFilters         A list of ProtocolFilters for Incoming Actions (Packets)
     * @param       outgoingFilters         A list of ProtocolFilters for Outgoing Actions (Packets)
     */
    public ProtocolChain(List<SessionState> incomingFilters, List<SessionState> outgoingFilters)
    {
        this.incomingFilters = incomingFilters;
        this.outgoingFilters = outgoingFilters;
    }

    
    /**
     * Do nothing!
     * This WONT initialize the protocol filters!
     * We don't have sufficient information to do this,
     * so it should have been done before creating a ProtocolChain object.
     * 
     */
    @Override
    public void init(Map<String, String> initParams)
    {
        // do nothing here, all filters should have been initialized before
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
    public List<TriggerableAction> doInFilter(TriggerableAction action) 
            throws IOException
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
        List<TriggerableAction> result = new ArrayList<>(Arrays.asList(action));
        
        // we need to save the results temporarily, so lets use a new list:
        List<TriggerableAction> tmpResult = new ArrayList<>();
        
        for (SessionState protocolFilter : incomingFilters) 
        {           
            // pass the action through the filters
            // note that "result" may be filled with other actions that completed suddenly
            // so we need to process each of them separately
            for (TriggerableAction tmpAction : result) 
            {
                // also, we cannot modify result while it is processed, so
                // we'll temporarily save the results:
                tmpResult.addAll(protocolFilter.doInFilter(tmpAction));
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
            throws IOException
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
