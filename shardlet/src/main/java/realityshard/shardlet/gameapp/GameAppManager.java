/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.gameapp;

import realityshard.shardlet.ShardletContext;
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
     * Creates a new game app of type 'name'
     * 
     * @param       name                    The name of this game app as defined by
     *                                      the deployment descriptor or development env
     * @param       parent                  The parent game app that created this game app
     *                                      (null if there was none)
     * @param       additionalParams        Any additionaly init params.
     * @return      The shardlet context that was created, or null if the creation failed.
     */
    public ShardletContext createGameApp(String name, ShardletContext.Remote parent, Map<String, String> additionalParams);
    
    
    /**
     * Should be called by a game app to unload/close/shutdown itself.
     * The game-app manager will then remove any reference to that game-app,
     * so it gets garbage collected.
     * 
     * Any sessions connected with that game app will be invalidated.
     * 
     * @param       me                      The game app that will be unloaded
     *                                      (This should not be called by other
     *                                      game apps...)
     */
    public void notifyUnload(ShardletContext me);
}
