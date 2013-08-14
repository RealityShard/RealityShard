/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.util.AttributeKey;
import realityshard.container.gameapp.GameAppContext;


/**
 * The key to get a channels context.
 * 
 * @author _rusty
 */
public abstract class GameAppContextKey 
{
	public static AttributeKey<GameAppContext> KEY = new AttributeKey<>(GameAppContext.class.getName());
        
        /**
         * Determines if the channel has been assigned to a game app instance, i.e. if the 
         * attribute of this KEY has been changed.
         */
        public static AttributeKey<Boolean> IS_SET = new AttributeKey<>(GameAppContext.class.getName() + "_is_set");
}
