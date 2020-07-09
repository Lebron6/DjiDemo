package com.compass.ux.netty_lib.zhang;

import java.io.DataOutput;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhangchuang
 * @Description
 * @date 2020/6/15 12:36 下午
 **/
public class Encode extends MessageToByteEncoder<Communication> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Communication msg, ByteBuf byteBuf)
      throws Exception {
    DataOutput byteBufOutputStream = new ByteBufOutputStream(byteBuf);
    JacksonDatabind.getInstance().writeValue(byteBufOutputStream, msg);
  }
}
