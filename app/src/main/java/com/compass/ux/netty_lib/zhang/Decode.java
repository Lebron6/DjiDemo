package com.compass.ux.netty_lib.zhang;

import java.io.DataInput;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author zhangchuang
 * @Description
 * @date 2020/6/15 12:24 下午
 **/
public class Decode<T> extends ByteToMessageDecoder {

  private final Class<T> clazz;

  public Decode(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) throws Exception {

    DataInput byteBufInputStream = new ByteBufInputStream(byteBuf);
    list.add(JacksonDatabind.getInstance().readValue(byteBufInputStream, clazz));


  }
}
