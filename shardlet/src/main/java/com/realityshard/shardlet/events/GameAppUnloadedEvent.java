/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.events;

import com.realityshard.shardlet.EventAggregator;
import com.realityshard.shardlet.utils.GenericTriggerableAction;


/**
 * This Event is triggered when the ShardletContext of a game-app 
 * has been unloaded successfully, meaning it is no longer used.
 * 
 * @author _rusty
 */
public final class GameAppUnloadedEvent extends GenericTriggerableAction
{
   
    /**
     * Trigger the event that this Action contains
     * 
     * @param       aggregator              The EventAggregator that you want an
     *                                      this action to be published on
     */
    @Override
    public void triggerEvent(EventAggregator aggregator)
    {
        aggregator.triggerEvent(this);
    }
}
