/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet;


/**
 * A persistent user-session that is unique for every connection.
 *
 * @author	_rusty
 */
public interface Session 
{
   
    /**
     * Getter.
     *
     * @return      The IP address of the client that sent the request
     */
    public String getRemoteAddr();
    
    
    /**
     * Returns the Internet Protocol (IP) source port of the client
     * or last proxy that sent the request.
     *
     * @return      An integer specifying the port number
     */    
    public int getRemotePort();
    
    
    /**
     * Returns the name of the protocol the session uses.
     * The protocol is a string taken from the deployment descriptor of the protocol.
     *
     * @return      A <code>String</code> containing the protocol name,
     *              as used in the deployment descriptor.
     */    
    public String getProtocol();
    
    
    /**
     * Getter.
     * 
     * @return      The shardlet context that this session is connected to, or
     *              null if it has not been connected yet
     */
    public ShardletContext getShardletContext();

    
    /**
     * Send a network action (a.k.a. network packet) to this session.
     * 
     * @param action 
     */
    public void send(Action action);
    

    /**
     * Invalidates this session then unbinds any objects bound to it. 
     */
    public void invalidate();
    
        
    /**
     * Getter.
     * Returns your session-specific data that you attached to this session,
     * or null if there was nothing attached.
     * 
     * @return      The session-specific data.
     */
    public Object getAttachment();


    /**
     * Setter.
     * This adds any session specific data to this session.
     * 
     * Beware of casting exceptions! You may want to have a single class
     * that manages all session specific data and only use that one as
     * an attachment
     * 
     * @param       attachment              The session-specific data. 
     */
    public void setAttachment(Object attachment);

}

