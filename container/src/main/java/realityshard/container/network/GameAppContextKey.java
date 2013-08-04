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
	public static AttributeKey<GameAppContext> GAME_APP_CONTEXT_KEY = new AttributeKey<>(GameAppContext.class.getName());
}
