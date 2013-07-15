/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet;


/**
 * This interface represents a remote shardlet context.
 * It is used to work with other shards, by passing them events through 
 * TriggerableActions.
 * 
 * This functionality was implemented in ShardletContext itself before, but
 * it is quite confusing to implement it there, as it concerns only _OTHER_
 * contexts, not the current context.
 * Additionally, the other contexts had access to context internal functionality,
 * like sendAction() and so on, which is not intended.
 * 
 * This is part of the Inter-Shard-Communication functionality.
 * 
 * @author _rusty
 */
public interface RemoteShardletContext 
{
    /**
     * Use this method to transmit an event to another ShardletContext and
     * have it triggered there.
     * TriggerableAction is an action that encapsulates and event that
     * can be triggered by the remote context manually.
     * 
     * @see         TriggerableAction
     * 
     * @param       action                  This action should encapsulate the
     *                                      Event that you want to have triggered
     *                                      in the remote context.
     */
    public void sendTriggerableAction(TriggerableAction action);
}
