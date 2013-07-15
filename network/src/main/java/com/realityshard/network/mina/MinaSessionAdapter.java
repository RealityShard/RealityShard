/**
 * For copyright information see the LICENSE document.
 */

package com.realityshard.network.mina;

import com.realityshard.network.NetworkSession;
import java.nio.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;


/**
 * This is an implementation of NetworkSession, that makes use of
 * the apache mina framework's features.
 * 
 * @author _rusty
 */
public class MinaSessionAdapter implements NetworkSession
{
    private final IoSession session;
    
    
    /**
     * Constructor.
     * 
     * @param       session 
     */
    public MinaSessionAdapter(IoSession session)
    {
        this.session = session;
    }

    
    /**
     * Write a packet.
     * 
     * @param       buffer
     */
    @Override
    public void handlePacket(ByteBuffer buffer) 
    {
        session.write(IoBuffer.wrap(buffer));
    }

    
    /**
     * Disconnect this session.
     */
    @Override
    public void disconnect() 
    {
        // close this session immediately
        session.close(true);
    }

}
