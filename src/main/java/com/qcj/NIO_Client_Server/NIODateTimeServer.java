package com.qcj.NIO_Client_Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIODateTimeServer {

	public static void main(String[] args) {

		NIODateTimeServer server = new NIODateTimeServer();

		server.init();
		System.out.println("服务端程序已经初始化成功。。。。启动成功");
		server.listen();
	}

	private Selector selector;

	/**
	 * 启动服务器
	 */
	private void init() {
		// 启动服务器程序
		try {

			// 等待客户端发送请求过来建立建立连接的
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

			// 启动多路复用器
			selector = Selector.open();

			serverSocketChannel.bind(new InetSocketAddress("localhost", 9527));
			serverSocketChannel.configureBlocking(false);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动监听，监测是否有客户端发送请求过来，进行处理
	 */
	private void listen() {
		// TODO Auto-generated method stub

		while (true) {

			try {
				// selector不停的去轮询注册在其上的所有的channel
				// 监测有那个channel上的IO操作已经完成，
				// 那么就相当于触发了对应的某个channel的某个事件
				// number就是准备就绪的channel的个数
				int number = selector.select(2000);
				// System.out.println("服务器开始轮询。。。。。。。准备就绪的channel的数量： " +
				// number);

				if (number != 0) {
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectedKeys.iterator();

					/**
					 * 为什么不适用增强for循环？ 这里使用迭起器进行操作就是为了每次处理成功一个channel之后就移除掉
					 */
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						// 处理连接请求的
						handleKey(key);
						/**
						 * 但是这个set是一个复用的set
						 */
						iterator.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	ByteBuffer buffer = ByteBuffer.allocate(1024);

	/**
	 * 针对准备就绪的key进行处理
	 * 
	 * 1、key 当中：
	 * 
	 * channel: ServerSocketChannel 事件： OP_ACCEPT
	 */
	private void handleKey(SelectionKey key) {

		// 判断key是否失效
		if (key.isValid()) {

			// 判断key的时间是否是 建立连接
			if (key.isAcceptable()) {

				// 获取到了准备就绪的 channel
				ServerSocketChannel channel = (ServerSocketChannel) key.channel();

				// 允许创建连接
				try {
					SocketChannel clientChannel = channel.accept();
					clientChannel.configureBlocking(false);

					clientChannel.register(selector, SelectionKey.OP_READ);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			/**
			 * key: channel: SocketChannel 事件： OP_READ
			 */
			if (key.isReadable()) {

				/**
				 * 第一步：读取数据
				 */
				SocketChannel clientChannel = (SocketChannel) key.channel();

				buffer.clear();

				int content_length = 0;
				try {
					content_length = clientChannel.read(buffer);
					System.out.println("服务端接收的到的数据的长度：" + content_length);
				} catch (IOException e) {
					e.printStackTrace();
				}

				/**
				 * 第二步：处理请求
				 */
				String request = new String(buffer.array(), 0, content_length);
				System.out.println("server接收到客户端的请求数据是： " + request);
				
				String result = handleBusiness(request);

				/**
				 * 第三步：返回结果
				 */
				writeResultToClient(clientChannel, result, buffer);

				/**
				 * 关闭channel
				 */
				if (key != null) {
					key.cancel();
					SocketChannel channel = (SocketChannel) key.channel();
					if (channel != null) {
						try {
							channel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 返回处理结果给客户端
	 */
	private void writeResultToClient(SocketChannel clientChannel, String result, ByteBuffer buffer) {

		buffer.clear();
		buffer.put(result.getBytes());
		buffer.flip();
		/**
		 * 1、对于clientChannel来说 是把buffer中的数据写出去
		 * 
		 * 2、针对buffer来说，数据被clientChannel读取
		 */
		try {
			clientChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String handleBusiness(String request) {

		String result = null;
		if (request.equals("getNow")) {
			result = DateTimeUtil.getNow();
		}else{
			result = "error";
		}
		return result;
	}
}
