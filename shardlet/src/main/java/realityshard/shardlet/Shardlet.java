/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.util.Map;


/**
 * This interface resembles a single (event handler) component of a game app.
 * It can be loaded dynamically (or statically when using the dev environment)
 * and should be used to handle incoming actions of the clients.
 * 
 * @author _rusty
 */
public interface Shardlet
{
   
    /**
     * Called by the container/context when the shardlet is loaded.
     * 
     * @param       initParams              The initialize parameters for this shardlet
     * @param       context                 The shardlet context of this shardlet
     */
    public void init(Map<String, String> initParams, ShardletContext context);
    
    
    /**
     * Called by the development-environment, when not using dynamic
     * Shardlet creation. (Meaning when using hardcoded classes instead of
     * dynamically loaded java class files, within the context of the
     * development environment)
     * 
     * @return      A clone of this shardlet. The clone does not need to be
     *              initialized. You can expect the container to call init() on
     *              the clone again.
     */
    public Shardlet clone();
}
