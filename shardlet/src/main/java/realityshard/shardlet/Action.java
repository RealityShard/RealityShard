/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet;


/**
 * Defines an object to provide a message to or from a client, by:
 * 
 * A) The Shardlet container creates an <code>Action</code> object and passes
 * it as an argument to the Shardlet's handler method.
 *
 * B) The Shardlet creates an <code>Action</code> object and passes
 * it as an argument to the Shardlet container where it is distributed.
 * 
 * @author _rusty
 */
public interface Action extends Event
{
    
    /**
     * Initialize the Action by serving the Session object that
     * this it will be distributed to, or that it is coming from.
     * 
     * @param       session                 The session object as a reference.
     */
    public void init(Session session);
    
    
    /**
     * Returns the name of the protocol the action uses.
     * The protocol refers to the name of the game app using it.
     *
     * @return      A <code>String</code> containing the protocol name,
     *              as used in the deployment descriptor.
     */    
    public String getProtocol();
    
    
    /**
     * Getter.
     * 
     * @return      The network session object.
     */
    public Session getSession();
}
