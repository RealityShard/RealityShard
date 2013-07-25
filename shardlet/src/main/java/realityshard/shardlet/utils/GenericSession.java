/**
 * For copyright information see the LICENSE document.
 */

package realityshard.shardlet.utils;

import realityshard.shardlet.Session;
import realityshard.shardlet.SessionState;


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

    private volatile SessionState state;
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

        state = new SessionState.NoFilter();
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
    public abstract void kick();


    /**
     * Getter.
     *
     * @return      This sessions SessionState state object, that does filtering.
     */
    protected SessionState getState()
    {
        return state;
    }


    /**
     * Setter.
     *
     * @param       state
     */
    @Override
    public void setState(SessionState state)
    {
        this.state = state;
    }


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
