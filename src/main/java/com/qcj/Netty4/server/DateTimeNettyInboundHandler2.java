package com.qcj.Netty4.server;

import com.qcj.Netty4.util.DateTimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DateTimeNettyInboundHandler2 extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		// 接收请求
		System.out.println("EchoInboundHandler2");
        ByteBuf buffer = (ByteBuf) msg;
        byte[] requestByteArray = new byte[buffer.readableBytes()];
		buffer.readBytes(requestByteArray);
        String request = new String(requestByteArray, "UTF-8");
        System.out.println("接收客户端数据:" + request);

        // 处理请求
        String currentTime = DateTimeUtil.getNow();

		//向客户端写数据
		System.out.println("server向client发送数据");
        ByteBuf response = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.flush();//刷新后才将数据发出到SocketChannel
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
