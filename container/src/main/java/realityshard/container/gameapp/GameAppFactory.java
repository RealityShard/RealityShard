/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import java.util.Map;
import realityshard.container.util.Handle;


/**
 * This interface defines how a factory for game apps should look like.
 * This is used within the container to produce new game apps.
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
     * Produce a new server channel.
     * This should be done here:
     * - Create a new ServerBootstrap
     * - Configure the child-channel preferences
     * - Bind to a port (and return the ChannelFuture)
     * 
     * (Only executed once at startup)
     * 
     * @param       bootstrap               Use this bootstrap to implement the method.
     * @return      The server channel for gameapps produced by this factory.
     */
    public Channel getServerChannel(ServerBootstrap bootstrap)
            throws Exception;

    
    /**
     * Create a new Game-App.
     *
     * @param       manager                 The manager of this new game app.
     * @param       parent                  The parent context of the new context (if any)
     * @param       additionalParams        The additional parameters used for game app creation.
     * @return      The new GameApp if successfull, else null.
     */
    public Handle<GameAppContext> produceGameApp(GameAppManager manager, Handle<GameAppContext> parent, Map<String, String> additionalParams);
}
