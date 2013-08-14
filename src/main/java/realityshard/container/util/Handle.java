/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.util;

import java.util.UUID;


/**
 * Acts as a link between an object of type T and its unique identifier 
 * 
 * @param       <T>                         A reference type
 * 
 * @author _rusty
 */
public interface Handle<T extends Object>
{
    
    /**
     * Getter.
     * 
     * @return      The object that actually belongs to this handle
     */
    T get();
    
    
    /**
     * Getter.
     * 
     * @return      The uid that actually belongs to this handle
     */
    UUID getUid();
    
    
    /**
     * Invalidate this handle, essentially deregistering it from the HandleRegstry
     * and freeing its UID.
     */
    void invalidate();
}
