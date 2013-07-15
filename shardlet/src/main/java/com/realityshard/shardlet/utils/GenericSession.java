/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.shardlet.utils;

import com.realityshard.shardlet.Session;


/**
 * Encapsulates the most necessary methods of the shardlet.Session interface.
 * 
 * @author _rusty
 */
public abstract class GenericSession implements Session
{

    private final String IP;
    private final int port;
    private final String protocol;
    
    private volatile Object attachment;
    
    
    /**
     * Constructor.
     * 
     * @param       IP                      The IP of the client connection
     * @param       port                    The port of the client connection
     * @param       protocol                The protocol name of protocol this session
     *                                      uses internally.
     */ 
    public GenericSession(String IP, int port, String protocol)
    {
        this.IP = IP;
        this.port = port;
        this.protocol = protocol;
    }

    
    /**
     * Returns the Internet Protocol (IP) address of the client 
     * or last proxy that sent the request.
     *
     * @return      A <code>String</code> containing the 
     *              IP address of the client that sent the request
     */
    @Override
    public String getRemoteAddr() 
    {
        return IP;
    }

    
    /**
     * Returns the Internet Protocol (IP) source port of the client
     * or last proxy that sent the request.
     *
     * @return      An integer specifying the port number
     */ 
    @Override
    public int getRemotePort() 
    {
        return port;
    }
    
    
    /**
     * Getter.
     * 
     * @return      The protocol-name as string
     */
    @Override
    public String getProtocol()
    {
        return protocol;
    }

    
    /**
     * Invalidates this session then unbinds any objects bound
     * to it. 
     */
    @Override
    public abstract void invalidate();
    
    
    /**
     * Getter.
     * 
     * @return
     */
    @Override
    public Object getAttachment() 
    {
        return attachment;
    }


    /**
     * Setter.
     * 
     * @param       attachment
     */
    @Override
    public void setAttachment(Object attachment) 
    {
        this.attachment = attachment;
    }
}
