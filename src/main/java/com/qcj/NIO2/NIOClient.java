package com.qcj.NIO2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 描述：
 * 
 * 		异步非阻塞    客户端程序
 */
public class NIOClient {
	
	private static final int PORT = 9090;

	public static void main(String[] args) {
		
		new Thread(new ClientHandler("127.0.0.1", PORT)).start();
	}
}

class ClientHandler implements Runnable{
	
	private String hostname;
	private int port;
	
	private Selector selector = null;
	private SocketChannel open = null;
	
	private volatile boolean stop = false;
	
	// 启动客户端
	public ClientHandler(String hostname, int port){
		System.out.println("ClientHandler 构造中...");
		this.hostname = hostname;
		this.port = port;
		try {
			selector = Selector.open();
			open = SocketChannel.open();
			open.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void connect(){
		System.out.println("正在连接服务器");
		try {
			boolean connect = open.connect(new InetSocketAddress(hostname, port));
			if(connect){
				open.register(selector, SelectionKey.OP_READ);
				// 往服务器端写入数据
				writeDataToSever(open);
			}else{
				// 如果connect为false，不代表链接服务器失败，可以重新注册到selector，监听OP_CONNECT
				open.register(selector, SelectionKey.OP_CONNECT);
			}
		} catch (IOException e) {
			System.out.println("链接服务器失败");
		}
	}

	@Override
	public void run() {

		// 链接服务器
		System.out.println("开始连接服务器");
		connect();
		
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()){
					SelectionKey next = iterator.next();
					iterator.remove();
					
					try {
						handleInput(next);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("客户端处理  服务端 返回结果  异常    xxxx");
					}
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void handleInput(SelectionKey key){
		
		if(key.isValid()){
			SocketChannel sc = (SocketChannel) key.channel();
			
			// 表示服务端已经返回应答信息，当前的客户端Channel可连接了。
			if(key.isConnectable()){
				// 如果客户端完成连接
				boolean finishConnect = false;
				try {
					finishConnect = sc.finishConnect();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("未链接上服务器");
				}
				System.out.println(finishConnect == true ? "完成连接":"未完成连接");
				if(finishConnect){
					try {
						sc.register(selector, SelectionKey.OP_READ);
					} catch (ClosedChannelException e) {
						e.printStackTrace();
						System.out.println("注册 OP_READ 到 多路复用器上");
					}
					// 如果客户端连接服务端成功了。 那么就发送请求到服务端
					writeDataToSever(sc);
				}else{
					System.out.println("客户端无法完成连接， 放弃");
					System.exit(1);
				}
			}
			
			// 表示 服务端是一个 IO 请求。  肯定是客户端发送的请求，在服务端接收到之后进行处理之后返回的结果
			if(key.isReadable()){
				//开辟缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				//异步读取
				int readBytes = 0;
				try {
					readBytes = sc.read(readBuffer);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("读取数据失败.....");
				}
				if(readBytes > 0){
					readBuffer.flip();
					byte[] content = new byte[readBuffer.remaining()];
					readBuffer.get(content);
					String result = "";
					try {
						result = new String(content, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("Server Result  : " + result);
					this.stop = true;
				}else if(readBytes == 0){
					
				}else{
					try {
						sc.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					sc = null;
				}
				
				// 处理过后的channel就失效了，所以清空
				if(key != null){
					key.cancel();
					if(key.channel() != null){
						try {
							key.channel().close();
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("关闭 客户端的 请求 链接 成功  xxxxx");
						}
						System.out.println("关闭 客户端的 请求 链接 成功  yyyyy");
					}
				}
			}
		}
	}
	
	/**
	 * 日期：2017年8月25日上午7:25:20
	 * 说明： 往服务器端写入数据
	 */
	public void writeDataToSever(SocketChannel clientChannel){
		
		System.out.println("开始发送数据....");
//		String order = "getNow";
		String order = "getName";
		byte[] content = order.getBytes();
		int capacity = content.length;
		
		ByteBuffer bb = ByteBuffer.allocate(capacity);
		bb.put(content);
		bb.flip();
		
		try {
			clientChannel.write(bb);
			System.out.println("发送数据到 服务器 成功");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("写入数据到ByteBuffer失败");
		}
		
		if(!bb.hasRemaining()){
			System.out.println("客户端发送数据到服务器成功......");
		}
	}
}