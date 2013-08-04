/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.events;


/**
 * This Event is triggered by the MetaGameAppContext, when the container
 * successfully created a gameapp and registered it with the meta context.
 * 
 * This may be the right time to distribute any context-global references.
 * 
 * @author _rusty
 */
public final class GameAppCreatedEvent implements Event
{
}
