/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * A filter is a passive, stateless object that has some kind of filter functionality
 * it uses to transform a given action. E.g. used for encryption, data compression,
 * protocol specific parsing etc.
 * A filter is invoked directly by the container, based on the shardlet it precedes
 * filters are also used to implement different low-level protocol functionality.
 * These filters have to be added to the protocol definition within the deployment-descriptor.
 * 
 * @author _rusty
 */
public interface ProtocolFilter
{
    
    /**
     * Initialzes the filter passing its init parameters to it.
     * 
     * @param       initParams              The initialization paramaters as
     *                                      specified by the deployment descriptor,
     *                                      or the dev environment.
     */
    public void init(Map<String, String> initParams);
    
    
    /**
     * Filters an incoming action / network message
     * 
     * @param       action                  The action that will be filtered
     * @return      The enumeration that contains all resulting actions.
     *              The filter may store any incomplete actions inside, so that
     *              you don't need to return broken packets / fragments.
     * @throws      IOException             The usual I/O stuff
     */
    public List<TriggerableAction> doInFilter(TriggerableAction action)
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
