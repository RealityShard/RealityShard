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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
        private final Logger LOGGER = LoggerFactory.getLogger(Encoder.class);
        private RC4Codec codec;
        
        public Encoder(byte[] rc4Key) { codec = new RC4Codec(rc4Key, Cipher.ENCRYPT_MODE); }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception 
        {
            if (!msg.isReadable()) { return; }
            
            LOGGER.debug("Decoded message.");
            
            codec.encode(ctx, msg, out);
        }
    }
    
    
    /**
     * Use this class to decode outbound streams
     */
    public static final class Decoder extends ByteToMessageDecoder
    {
        private final Logger LOGGER = LoggerFactory.getLogger(Decoder.class);
        private RC4Codec codec;
        
        public Decoder(byte[] rc4Key) { codec = new RC4Codec(rc4Key, Cipher.DECRYPT_MODE); }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception 
        {
            if (!msg.isReadable()) { return; }
            
            ByteBuf outBuf = Unpooled.buffer(msg.readableBytes());
            codec.encode(ctx, msg, outBuf);
            
            LOGGER.debug("Decoded message.");
            
            out.add(outBuf);
        }
    }
    
    
    private final Cipher rc4Encrypt;
    
    
    private RC4Codec(byte[] rc4Key, int mode)
    {
        try 
        {
            SecretKeySpec rc4KeySpec = new SecretKeySpec(rc4Key, "RC4");

            this.rc4Encrypt = Cipher.getInstance("RC4");
            this.rc4Encrypt.init(mode, rc4KeySpec);

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
