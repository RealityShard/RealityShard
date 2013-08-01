/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.utils;

import realityshard.shardlet.Session;
import realityshard.shardlet.Action;
import java.nio.ByteBuffer;


/**
 * A ShardletAction implementation.
 * This class can be inherited if needed, but it can also be used as
 * a fully functional action.
 * 
 * @author _rusty
 */
public class GenericAction implements Action
{

    private String protocol;
    private Session session;
    

    /**
     * Constructor.
     */
    public GenericAction()
    {
        protocol = null;
        session = null;
    }
    
    
    /**
     * Constructor.
     * 
     * @param       session 
     */
    public GenericAction(Session session)
    {
        this.protocol = session.getProtocol();
        this.session = session;
    }
    
    
    /**
     * Initialize the ShardletAction by serving the Session object that
     * this it will be distributed to, or that it is coming from.
     * 
     * @param       session                 The session object as a reference.
     */
    @Override
    public void init(Session session)
    {
        if (session != null)
        {
            this.protocol = session.getProtocol();
            this.session = session;
        }
    }

    
    /**
     * Getter.
     * 
     * @return      The protocol of this action as string (the name)
     */
    @Override
    public String getProtocol() 
    {
        return protocol;
    }
    
    
    /**
     * Getter.
     * 
     * @return      The session bound to this action.
     */
    @Override
    public Session getSession() 
    {
        return session;
    }
}
