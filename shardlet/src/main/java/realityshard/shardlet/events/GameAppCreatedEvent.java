/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.events;

import realityshard.shardlet.Event;


/**
 * This Event is triggered when the ShardletContext of a game-app 
 * has been created successfully, meaning all Shardlets have been initialized.
 * 
 * This may be the right time to distribute any context-global references.
 * 
 * @author _rusty
 */
public final class GameAppCreatedEvent implements Event
{
}
