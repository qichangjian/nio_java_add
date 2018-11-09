package com.qcj.Netty_version3;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * netty3:经典案例
 * Netty3 Server
 */
public class Server {
    public static void main(String[] args) {
        // 服务类,用于启动netty 在netty5中同样使用这个类来启动
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 新建两个线程池  boss线程监听端口，worker线程负责数据读写
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();
        // 设置niosocket工厂  类似NIO程序新建ServerSocketChannel和SocketChannel
        bootstrap.setFactory(new NioServerSocketChannelFactory(boss, worker));
        // 设置管道的工厂
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("helloHandler", new HelloHandler());  //添加一个Handler来处理客户端的事件，Handler需要继承ChannelHandler
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(10101));
        System.out.println("start!!!");
    }
}

class HelloHandler extends SimpleChannelHandler {
    /** 接收消息*/
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String s = (String) e.getMessage();
        System.out.println(s);
        //回写数据
        ctx.getChannel().write("HelloWorld");
        super.messageReceived(ctx, e);
    }
    /** 捕获异常*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, e);
    }
    /** 重新连接*/
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected");
        super.channelConnected(ctx, e);
    }
    /** 必须是链接已经建立，关闭通道的时候才会触发  */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }
    /** channel关闭的时候触发 */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed");
        super.channelClosed(ctx, e);
    }
}
