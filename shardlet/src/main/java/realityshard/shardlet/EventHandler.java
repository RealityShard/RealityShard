/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;

import java.lang.annotation.*;


/**
 * This annotation will mark the handler methods that the event-aggregator will look for
 * 
 * @author _rusty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface EventHandler 
{
}
