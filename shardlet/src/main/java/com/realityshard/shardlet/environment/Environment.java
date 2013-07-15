/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.environment;

import java.util.List;


/**
 * This stores all the game-apps and protocols of a single R:S server instance.
 * If using a development environment, you should implement this interface in 
 * your projects, returning concrete factories, if you don't want to use R:S's 
 * dynamic class loading features.
 * 
 * @author _rusty
 */
public interface Environment 
{
    
    
    /**
     * This method should return ALL game-app-factories that you want to use
     * within reality-shard.
     * 
     * @return      The game-app-factories that this instance of a container should be able to use.
     */
    public List<GameAppFactory> getGameAppFactories();
    
    
    /**
     * This method should return ALL protocols that you want to use
     * within reality-shard.
     * 
     * @return      The factories for protocols (internally, these are used to 
     *              create protocol chains)
     */
    public List<ProtocolFactory> getProtocolFactories();
}
