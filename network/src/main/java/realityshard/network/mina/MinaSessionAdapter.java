/**
 * For copyright information see the LICENSE document.
 */

package realityshard.network.mina;

import realityshard.network.NetEventHandlers;
import realityshard.network.NetworkSession;
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

    private NetEventHandlers.NewData newData;
    private NetEventHandlers.LostClient lostClient;


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
     * Write data.
     *
     * @param       buffer
     */
    @Override
    public void write(ByteBuffer buffer)
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


    /**
     * Register an event handler.
     *
     * @param       newData
     */
    @Override
    public void registerOnNewData(NetEventHandlers.NewData newData)
    {
        this.newData = newData;
    }


    /**
     * Register an event handler.
     *
     * @param       lostClient
     */
    @Override
    public void registerOnLostClient(NetEventHandlers.LostClient lostClient)
    {
        this.lostClient = lostClient;
    }


    /**
     * Event trigger.
     */
    public void onNewData(ByteBuffer buffer)
    {
        newData.onNewData(buffer);
    }


    /**
     * Event trigger.
     */
    public void onLostClient()
    {
        lostClient.onLostClient();
    }
}
