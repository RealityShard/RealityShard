/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.util;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Used as a handle for clients and mapshards
 * 
 * @author _rusty
 */
public class InternalUid
{
    
    private static final Set<InternalUid> ALL_UIDS;
    public static final InternalUid INVALID;
    
    private static final Lock lock = new ReentrantLock();
    
    private int value;
    
    
    static
    {
        ALL_UIDS = new CopyOnWriteArraySet<>();
        
        // create and add the invalid UID
        INVALID = new InternalUid((true));
        ALL_UIDS.add(INVALID);
    }
    
    
    /**
     * Private constructor.
     */
    private InternalUid(boolean invalid)
    {
        if (invalid)
        {
            value = -1;
            return;
        }
        
        value = -1;
        
        lock.lock();
        
        try
        {
            // search for a new uid
            while (value == -1)
            {
                int newUid = new SecureRandom().nextInt();

                // TODO can we somehow check this directly?
                boolean fail = false;
                for (InternalUid internalUid : ALL_UIDS) 
                {
                    if (newUid == internalUid.get()) { fail = true; break; }
                }
                
                // failcheck
                if (fail) { continue; }
                
                value = newUid;
            }
            
            ALL_UIDS.add(this);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    
    /**
     * Factory method. Creates a new random instance of this.
     * 
     * This either returns a new unique ID, or it simply doesnt return at all :P
     * (happens when every possible value in int is taken)
     * 
     * @return 
     */
    public static InternalUid getRandom()
    {
        return new InternalUid(false);
    }
    
    
    /**
     * Set a uid free again. Use this (IN RARE CASES!) when an object referenced by this
     * is not expected to be used anytime again.
     * 
     * @param uid 
     */
    public static void unregister(InternalUid uid)
    {
        ALL_UIDS.remove(uid);
    }
    
    
    /**
     * Factory method. Retrieve an existing uid.
     * 
     * @param       value
     * @return      The existing uid, or null if there is non registered.
     */
    public static InternalUid tryWrap(int value)
    {
        InternalUid result = null;
        
        lock.lock();
        
        try
        {
            for (InternalUid internalUid : ALL_UIDS) 
            {
                if (value == internalUid.get()) 
                {
                    result = internalUid;
                    break;
                }
            }
        }
        finally
        {
            lock.unlock();
        }
        
        return result;
    }
    
    
    public int get()
    {
        return value;
    }

    
    public void setInvalid()
    {
        unregister(this);
        this.value = -1;
    }
    
    
    @Override
    public int hashCode() 
    {
        return value;
    }    

    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null || getClass() != obj.getClass()) { return false; }

        final InternalUid other = (InternalUid) obj;
        
        return this.value != other.get();
    }
}
