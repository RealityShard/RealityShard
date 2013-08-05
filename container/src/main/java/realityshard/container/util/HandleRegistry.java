/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.util;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Object that keeps track of all the handles that are in use.
 * No static shit in here. Keep your handles per gameapp!
 * (The uid can be used to link them though)
 * 
 * @param       <T>                         A reference type 
 * 
 * @author _rusty
 */
public class HandleRegistry<T extends Object>
{
    
    private final Map<UUID, Handle<T>> handles;

    
    /**
     * Constructor.
     */
    public HandleRegistry() 
    {
        handles = new ConcurrentHashMap<>();
    }

    
    /**
     * Factory method.
     * 
     * @param       object                  The object that will be wrapped in a handler
     * @return      The new handler.
     */
    public Handle<T> produce(final T object) 
    {
        final UUID uid = UUID.randomUUID();
        final HandleRegistry<T> that = this;
        
        Handle<T> h = new Handle<T>() {
            @Override
            public T get() { return object; }

            @Override
            public UUID getUid() { return uid; }

            @Override
            public void invalidate() { that.remove(this); }
        };
                
        handles.put(h.getUid(), h);
        return h;
    }


    /**
     * Get a object by its uid.
     * 
     * @param       uid
     * @return      The identified object or null.
     */
    public T getObj(UUID uid)
    {
        return handles.get(uid).get();
    }
    
    
    /**
     * Get a handle by its uid.
     * 
     * @param       uid
     * @return      The identified handle or null.
     */
    public Handle<T> getHandle(UUID uid)
    {
        return handles.get(uid);
    }
    
    
    /**
     * Get all handles that are currently managed by this registry.
     * 
     * @return      The handles.
     */
    public Collection<Handle<T>> getAllHandles()
    {
        return handles.values();
    }


    /**
     * Remove a handle from this registry.
     * This will be called by the handle itself, when its invalidated.
     * 
     * @param       handle
     */
    private void remove(Handle<T> handle)
    {
        handles.remove(handle.getUid());
    }
}
