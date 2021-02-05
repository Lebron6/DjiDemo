package com.compass.ux.netty_lib.zhang;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author chuangzhang
 */
@Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

//  private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
//      .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
//          CharsetUtil.UTF_8));

  //private static final ProtoMessage.Message HEARTBEAT= ProtoMessage.Message.newBuilder().setMethod("Heartbeat").build();

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();

      if (state == IdleState.WRITER_IDLE) {
        // write heartbeat to server
        ProtoMessage.Message HEARTBEAT= ProtoMessage.Message.newBuilder().setMethod("Heartbeat").build();
        ctx.writeAndFlush(HEARTBEAT);
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
