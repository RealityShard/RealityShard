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
     * Use this to initially get a handle for some kind of data storage object.
     * 
     * @param       object                  The object that will be wrapped in a handler
     * @return      The new handler.
     */
    public Handle<T> register(final T object) 
    {
        final UUID uid = UUID.randomUUID();
        
        return registerExisting(object, uid);
    }
    
    
    /**
     * Factory method.
     * 
     * Use this method to register a handle for an object that you already got a
     * uid for.
     * Example: Your game app has received a uid and data for some kind of globally
     * shared object state. Use this to associate that uid with the data locally.
     *  
     * @param       object                  The object that will be wrapped in a handler.
     * @param       uid                     An existing UUID.
     * @return      The new handler.
     */
    public Handle<T> registerExisting(final T object, final UUID uid)
    {
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
     * Check if there are no handles registered in this registry.
     * 
     * @return      True or false depending on the handlecount.
     */
    public boolean isEmpty()
    {
        return handles.isEmpty();
    }


    /**
     * Remove a handle from this registry.
     * This will be called by the handle itself, when its invalidated.
     * 
     * @param       handle
     */
    protected void remove(Handle<T> handle)
    {
        handles.remove(handle.getUid());
    }
}
