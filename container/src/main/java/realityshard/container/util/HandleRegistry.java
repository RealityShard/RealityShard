/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Object that keeps track of all the handles that are in use.
 * No static shit in here. Keep your handles per gameapp!
 * (The uid can be used to link them though)
 * 
 * @author _rusty
 */
public class HandleRegistry<T extends Object>
{
    
    private final Map<InternalUid, Handle<T>> handles;

    
    public HandleRegistry() 
    {
        handles = new ConcurrentHashMap<>();
    }

    
    public Handle<T> produce(final T object) 
    {
        final InternalUid uid = InternalUid.getRandom();
        final HandleRegistry<T> that = this;
        
        Handle<T> h = new Handle<T>() {
            @Override
            public T get() { return object; }

            @Override
            public InternalUid getUid() { return uid; }

            @Override
            public void invalidate() { uid.setInvalid(); that.remove(this); }
        };
                
        handles.put(h.getUid(), h);
        return h;
    }


    public T getObj(InternalUid uid)
    {
        return handles.get(uid).get();
    }
    
    
    public Handle<T> getHandle(InternalUid uid)
    {
        return handles.get(uid);
    }
    
    
    public Collection<Handle<T>> getAllHandles()
    {
        return handles.values();
    }


    private void remove(Handle<T> h)
    {
        handles.remove(h.getUid());
    }
}
