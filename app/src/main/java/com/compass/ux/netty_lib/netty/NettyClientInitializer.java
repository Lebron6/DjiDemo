package com.compass.ux.netty_lib.netty;


import com.compass.ux.app.MApplication;
import com.compass.ux.netty_lib.zhang.ConnectorIdleStateTrigger;
import com.compass.ux.netty_lib.zhang.EventHandle;
import com.compass.ux.netty_lib.zhang.HeartBeatClientHandler;
import com.compass.ux.netty_lib.zhang.ProtoMessage;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by 张俨 on 2017/10/9.
 */

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyListener listener;

    public NettyClientInitializer(NettyListener listener) {
        if(listener == null){
            throw new IllegalArgumentException("listener == null ");
        }
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
//        SslContext sslCtx = SSLContext.getDefault()
//                .createSSLEngine(InsecureTrustManagerFactory.INSTANCE).build();

        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(sslCtx.newHandler(ch.alloc()));    // 开启SSL

        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));    // 开启日志，可以设置日志等级

        //解码用
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        //构造函数传递要解码成的类型
        pipeline.addLast(new ProtobufDecoder(ProtoMessage.Message.getDefaultInstance()));
        //编码用
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());


        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ConnectorIdleStateTrigger());
        pipeline.addLast(new HeartBeatClientHandler(MApplication.EQUIPMENT_ID,listener));
//        pipeline.addLast( new Decode<>(Communication.class));//解码器 这里要与服务器保持一致
//        pipeline.addLast(new Encode());//编码器 这里要与服务器保持一致

//        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));

//        pipeline.addLast(new LengthFieldBasedFrameDecoder(102400, 0, 4, 0, 4));
//        // LengthFieldPrepender是一个编码器，主要是在响应字节数据前面添加字节长度字段
//        pipeline.addLast(new LengthFieldPrepender(4));
//
//        pipeline.addLast(new StringDecoder());
//        pipeline.addLast(new StringEncoder());





//      pipeline.addLast(new NettyClientHandler(listener));

        pipeline.addLast(new EventHandle(listener));




    }
}