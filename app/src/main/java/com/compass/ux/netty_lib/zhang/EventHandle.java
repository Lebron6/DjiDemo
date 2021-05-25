package com.compass.ux.netty_lib.zhang;

import android.util.Log;

import com.compass.ux.netty_lib.netty.NettyListener;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zhangchuang
 * @Description
 * @date 2020/6/15 12:46 下午
 **/
public class EventHandle extends SimpleChannelInboundHandler<ProtoMessage.Message> {
    private String TAG = "EventHandle";

    private NettyListener listener;

    public EventHandle(NettyListener listener) {
        this.listener = listener;
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        Log.d(TAG, "thread == " + Thread.currentThread().getName());
//        Log.d(TAG, "来自服务器的消息 ====》" + msg);
//        if (msg instanceof String){
//            listener.onMessageResponse((ProtoMessage.Message) msg);
//        }else{
//            Log.d(TAG,"不是String类型");
//        }
//
//
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage.Message msg) throws Exception {
        Communication communication= msg.CoverCommunication();
        Log.e("Netty接收：",new Gson().toJson(communication));
        XcFileLog.getInstace().i("Netty接收:",new Gson().toJson(communication));
        listener.onMessageResponse(communication);
    }
}
