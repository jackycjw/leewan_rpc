package com.leewan.rpc.share.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * 客户端RSA公钥加密/解密
 */
public class ClientCipherHandler extends ByteToMessageCodec<ByteBuf> {

    private Cipher keyEncryptCipher;

    /**
     * DES加密
     */
    private Cipher desEncryptCipher;
    /**
     * DES解密
     */
    private Cipher desDecryptCipher;

    public ClientCipherHandler(String publicKey) throws GeneralSecurityException {
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        keyEncryptCipher = Cipher.getInstance("RSA");
        keyEncryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        byte[] content = new byte[msg.readableBytes()];
        msg.readBytes(content);
        content = desEncryptCipher.doFinal(content);
        out.writeBytes(content);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        byte[] content = new byte[in.readableBytes()];
        in.readBytes(content);
        content = desDecryptCipher.doFinal(content);
        out.add(ctx.alloc().buffer().writeBytes(content));
    }

    /**
     * 连接创建的时候生成对称秘钥
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("DESede");
        SecretKey secretKey = generator.generateKey();
        //一次性对称秘钥
        byte[] encoded = secretKey.getEncoded();
        //使用非对称秘钥加密一次性秘钥
        encoded = keyEncryptCipher.doFinal(encoded);
        ctx.writeAndFlush(ctx.alloc().buffer(encoded.length).writeBytes(encoded));

        //对称加密器
        desEncryptCipher = Cipher.getInstance("DESede");
        desEncryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //对称解密器
        desDecryptCipher = Cipher.getInstance("DESede");
        desDecryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        super.channelActive(ctx);
    }
}
