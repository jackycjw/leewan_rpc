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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

/**
 * 客户端RSA公钥加密/解密
 */
public class ServerCipherHandler extends ByteToMessageCodec<ByteBuf> {

    private Cipher keyDecryptCipher;

    /**
     * DES加密
     */
    private Cipher desEncryptCipher;
    /**
     * DES解密
     */
    private Cipher desDecryptCipher;

    private boolean inited = false;

    public ServerCipherHandler(String privateKey) throws GeneralSecurityException {
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        keyDecryptCipher = Cipher.getInstance("RSA");
        keyDecryptCipher.init(Cipher.DECRYPT_MODE, priKey);
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

        if (!inited) {
            //第一次是传输对称秘钥
            byte[] secretKey = keyDecryptCipher.doFinal(content);
            //对称加密器
            desEncryptCipher = Cipher.getInstance("DESede");
            desEncryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "DESede"));
            //对称解密器
            desDecryptCipher = Cipher.getInstance("DESede");
            desDecryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "DESede"));
            inited = true;
            return;
        }

        content = desDecryptCipher.doFinal(content);
        out.add(ctx.alloc().buffer().writeBytes(content));
    }

}
