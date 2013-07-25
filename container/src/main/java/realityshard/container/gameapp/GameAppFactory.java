/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import realityshard.shardlet.Shardlet;
import realityshard.shardlet.ShardletContext;
import java.util.Map;
import realityshard.shardlet.Session;


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
     * The protocol port is the port that this gameapp will accept clients on.
     * When the container starts up, it will instruct the network layer to produce
     * a new listening socket on that port.
     *
     * @return      The portnumber.
     */
    public int getProtocolPort();


    /**
     * This method will be called by the container when a new session has been
     * established.
     * Use this to set this Sessions state and attachment!
     *
     * @return
     */
    public void initializeSession(Session session);


    /**
     * Create a new Game-App and return its context.
     *
     * @param       manager                 The new game app needs to know the game app manager of
     *                                      this container, so it can create new game apps etc.
     * @param       parent                  The parent context of the new context (if any)
     * @param       additionalParams        The additional parameters used for game app creation.
     * @return      The new GameApp as a shardlet.ShardletContext
     */
    public ShardletContext produceGameApp(GameAppManager manager, ShardletContext.Remote parent, Map<String, String> additionalParams);
}
