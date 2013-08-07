/**
 * For copyright information see the LICENSE document.
 */

package realityshard.container.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * RC4 encryption handler, making use of the built-in cipher
 * Taken from iDemmel, with permission
 * 
 * @author _rusty
 */
public final class RC4Codec 
{
    
    /**
     * Use this class to encode outbound streams
     */
    public static final class Encoder extends MessageToByteEncoder<ByteBuf>
    {
        private RC4Codec codec;
        
        public Encoder(byte[] rc4Key) { codec = new RC4Codec(rc4Key, Mode.ENCRYPT_MODE); }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception 
        {
            codec.encode(ctx, msg, out);
        }
    }
    
    
    /**
     * Use this class to decode outbound streams
     */
    public static final class Decoder extends ByteToMessageDecoder
    {
        private RC4Codec codec;
        
        public Decoder(byte[] rc4Key) { codec = new RC4Codec(rc4Key, Mode.DECRYPT_MODE); }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception 
        {
            ByteBuf outBuf = Unpooled.buffer(msg.readableBytes());
            codec.encode(ctx, msg, outBuf);
            
            out.add(outBuf);
        }
    }
    
    
    public enum Mode
    {
        ENCRYPT_MODE(1),
        DECRYPT_MODE(2);
        
        private final int id;
        private Mode(int id) { this.id = id; }
        public int val() { return id; }
    }
    
    private final Cipher rc4Encrypt;
    
    
    private RC4Codec(byte[] rc4Key, Mode mode)
    {
        try 
        {
            SecretKeySpec rc4KeySpec = new SecretKeySpec(rc4Key, "RC4");

            this.rc4Encrypt = Cipher.getInstance("RC4");
            this.rc4Encrypt.init(mode.val(), rc4KeySpec);

        } 
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) 
        {
                throw new RuntimeException(e);
        }
    }
    

    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception 
    {
        byte[] dataToBeEncrypted = new byte[in.readableBytes()];
        in.readBytes(dataToBeEncrypted);

        byte[] encryptedBytes = this.rc4Encrypt.update(dataToBeEncrypted);

        out.writeBytes(encryptedBytes);
    }
}
