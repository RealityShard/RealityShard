/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.environment;

import com.realityshard.shardlet.RemoteShardletContext;
import com.realityshard.shardlet.Shardlet;
import com.realityshard.shardlet.ShardletContext;
import java.util.Map;


/**
 * This interface defines how a factory for game apps should look like.
 * This is used within the context manager to produce new game apps.
 * 
 * @author _rusty
 */
public interface GameAppFactory 
{

    /**
     * The name of the game app should be unique, so it is identifiable by the
     * ContextManager.
     * 
     * @return      The name of the GameApp this factory produces.
     */
    public String getName();
    
    
    /**
     * Indicates whether a game app should be loaded after the container started.
     * Note that only one game app per factory that has this startup indicator, will
     * be created.
     * 
     * @return      The boolean value that determines whether one game app of this
     *              factory should be produced when the container starts
     */
    public boolean isStartup();
    
    
    /**
     * Create a new Game-App and return its context.
     * 
     * @param       manager                 The new game app needs to know the game app manager of
     *                                      this container, so it can create new game apps etc.
     * @param       parent                  The parent context of the new context (if any)
     * @param       additionalParams        The additional parameters used for game app creation.
     * @return      The new GameApp as a shardlet.ShardletContext
     */
    public ShardletContext produceGameApp(GameAppManager manager, RemoteShardletContext parent, Map<String, String> additionalParams);
    
    
    /**
     * Add a new shardlet prototype and the data used for initialization
     * 
     * @param       shardlet                The prototype shardlet
     * @param       initParams              The initialization parameters used when
     *                                      the shardlet is initialized.
     * @return      This factory, so you can chain these kind of methods.
     */
    public GameAppFactory addShardlet(Shardlet shardlet, Map<String, String> initParams);

}
