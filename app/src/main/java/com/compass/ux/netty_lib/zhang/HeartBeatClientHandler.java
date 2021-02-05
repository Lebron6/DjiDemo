package com.compass.ux.netty_lib.zhang;

import android.util.Log;


import com.compass.ux.app.Constant;
import com.compass.ux.netty_lib.netty.NettyClient;
import com.compass.ux.netty_lib.netty.NettyListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author chuangzhang
 */
@Sharable
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
  private String TAG="HeartBeatClientHandler";
  String apronId;
  private NettyListener listener;
  public HeartBeatClientHandler(String apronId, NettyListener listener) {
    this.apronId = apronId;
    this.listener = listener;
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) {

    Log.d(TAG,"已激活");

    NettyClient.getInstance().setConnectStatus(true);
    listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);

//    Communication communication = Communication.builder()
//        .apronId(apronId)
//        .method("register")
//        .requestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))
//        .build();
    Communication communication=new Communication();
    communication.setEquipmentId(apronId);
//    communication.setMethod("register");
    String register="register";
    Log.d(TAG,"register="+register);
    communication.setMethod(register);

    communication.setRequestTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
//    Gson gson =new Gson();
//    String responseJson=gson.toJson(communication,Communication.class)+ Constant.LINE_SEPARATOR;

    ctx.writeAndFlush(communication.coverProtoMessage()).addListener(future->{
      if(future.isSuccess()){
        Log.d(TAG,"消息发送成功");
      }else {
        Log.d(TAG,"消息发送失败", future.cause());
      }

    });

    ctx.fireChannelActive();


  }

//  public static void main(String[] args) {
//
//    Communication communication=new Communication();
//    communication.setMethod("hello");
//
//    System.out.println(communication.coverProtoMessage());
//
//
//  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    NettyClient.getInstance().setConnectStatus(false);
    listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);
    NettyClient.getInstance().reconnect();
    System.out.println("停止时间是：" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
  }
}
