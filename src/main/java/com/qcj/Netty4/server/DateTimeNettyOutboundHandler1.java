package com.qcj.Netty4.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * 描述：处理输出数据的Handler
 */
public class DateTimeNettyOutboundHandler1 extends ChannelOutboundHandlerAdapter {
	@Override
    // 向client发送消息
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("out1");
        System.out.println(msg);
        
        ctx.write(msg);
        ctx.flush();
       }
}
