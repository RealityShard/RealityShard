/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A general event that can be handled by an event-aggregator.
 * 
 * @author _rusty
 */
public interface Event
{     
    
    /**
     * This annotation will mark the handler methods that the event-aggregator will look for
     * 
     * @author _rusty
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    public @interface Handler 
    {
    }
}
