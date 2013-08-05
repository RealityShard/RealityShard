/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.gameapp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;


/**
 * This interface defines methods to create game apps etc.
 * This will be implemented in the container internally, but it resides in the
 * api, because the game app factories use it.
 * 
 * @author _rusty
 */
public interface GameAppManager 
{
    
    /**
     * Check if we actually can create a game app with that name.
     * 
     * @param       name                    The name of the game app (All game
     *                                      game apps produced by one factory
     *                                      have the same name)
     * @return      True if it can be created, false if not.
     */
    public boolean canCreateGameApp(String name);
    
    
    /**
     * Creates a new game app of type 'name'
     * 
     * @param       name                    The name of this game app as defined by
     *                                      the factory.
     * @param       parent                  The parent game app that created this game app
     *                                      (null if there was none).
     * @param       additionalParams        Any additionaly init params.
     * @return      The shardlet context that was created, or null if the creation failed.
     */
    public GameAppContext.Remote createGameApp(String name, GameAppContext.Remote parent, Map<String, String> additionalParams);
    
    
    /**
     * This is the only method to delete a game app from the container.
     * Game Apps should never delete themselves on their own.
     * 
     * Any channels connected with this game app will be invalidated.
     * 
     * @param       that                    The game app that will be unloaded
     */
    public void removeGameApp(GameAppContext that);
    
    
    /**
     * Use this method to gain info about the local network interface that listens
     * for new channels.
     * 
     * @param       that                    The specific context that the
     *                                      network interface belongs to.
     * @return      The local address of the gameapp context.
     */
    public InetSocketAddress localAddressFor(GameAppContext that);
}
