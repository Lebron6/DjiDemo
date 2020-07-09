package com.compass.ux.netty_lib.netty;

import android.util.Log;


import com.compass.ux.netty_lib.zhang.Communication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by 张俨 on 2018/1/10.
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<Communication> {
    private static final String TAG = NettyClientHandler.class.getName();
    private NettyListener listener;

    public NettyClientHandler(NettyListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(true);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(false);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);
        NettyClient.getInstance().reconnect();
    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String byteBuf) throws Exception {
//        Log.e(TAG, "thread == " + Thread.currentThread().getName());
//        Log.e(TAG, "来自服务器的消息 ====》" + byteBuf);
//        listener.onMessageResponse(byteBuf);
//
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Communication msg) throws Exception {
        Log.d(TAG, "thread == " + Thread.currentThread().getName());
        Log.d(TAG, "来自服务器的消息 ====》" + msg.toString());

        listener.onMessageResponse(msg.toString());
    }
}
