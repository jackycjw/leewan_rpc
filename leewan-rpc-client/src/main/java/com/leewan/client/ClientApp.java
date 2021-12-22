package com.leewan.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientApp {

    public static void main(String[] args) throws Exception {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {

                    }
                })
                .connect("122.112.217.54", 5540).sync();
        Channel channel = channelFuture.channel();
        System.out.println("连接建立成功");

        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 1024*1024*50; i++) {
            buffer1.writeByte(i);
        }
        System.out.println("缓冲池长度：" + buffer1.readableBytes());
        ChannelFuture future = channel.writeAndFlush(buffer1);

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 1024*1024*50; i++) {
            buffer2.writeByte(i);
        }
        System.out.println("缓冲池2长度：" + buffer2.readableBytes());
        future = channel.writeAndFlush(buffer2);

    }
}
