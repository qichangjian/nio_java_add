package com.qcj.Netty4.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 配置服务器功能，如线程、端口
 * 实现服务器处理程序，它包含业务逻辑
 * 决定当有一个请求连接或接收数据时该做什么
 */
public class DateTimeNettyServer {

	private final int port;

	public DateTimeNettyServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		EventLoopGroup eventLoopGroup = null;
		try {
			// server端引导类
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			// 连接池处理数据
			eventLoopGroup = new NioEventLoopGroup();
			// 指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
			serverBootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class)
					// 设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
					.localAddress("localhost", port)
					// 设置childHandler执行所有的连接请求
					.childHandler(new ChannelInitializer<Channel>() {
								@Override
								protected void initChannel(Channel ch) throws Exception {
									// 注册两个InboundHandler，执行顺序为注册顺序，
									// 所以应该是InboundHandler1,InboundHandler2
									ch.pipeline().addLast(new DateTimeNettyInboundHandler1());
									ch.pipeline().addLast(new DateTimeNettyInboundHandler2());

									// 注册两个OutboundHandler，执行顺序为注册顺序的逆序，
									// 所以应该是OutboundHandler2,OutboundHandler1
									ch.pipeline().addLast(new DateTimeNettyOutboundHandler1());
									ch.pipeline().addLast(new DateTimeNettyOutboundHandler2());
								}
							});
			// 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			System.out.println("开始监听，端口为：" + channelFuture.channel().localAddress());
			// 等待channel关闭，因为使用sync()，所以关闭操作也会被阻塞。
			channelFuture.channel().closeFuture().sync();
		} finally {
			// 阻塞等待线程组关闭
			eventLoopGroup.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		new DateTimeNettyServer(9527).start();
	}
}
