package com.qcj.BIO_ThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 描述：用来对外提供服务器，   指定一种请求格式，  服务端程序可以解析客户端发送过来的数据，然后在服务端调用这个服务器得到结果  返回给 客户端
 * 
 * BIO编程模型的缺点：
 * 
 * 1、当客户端多时，会创建大量的处理线程。且每个线程都要占用栈空间和一些CPU时间
 * 2、accept方法是阻塞的，阻塞可能带来频繁的上下文切换，且大部分上下文切换可能是无意义的。
 */
public class TimeServer {
	
	private static final int PORT = 9527;

	// 程序入口
	public static void main(String[] args) {
		
		ServerSocket ss = null;
		try {
			
			/**
			 * 启动服务器端程序，绑定端口
			 */
			ss = new ServerSocket(PORT);
			System.out.println("The time server is start in port :" +  PORT);
			
			/**
			 * 模拟实现服务器程序一直运行 等待客户端发送请求过来进行连接
			 */
			Socket socket = null;
			while(true){
				
				// accept 方法是一个阻塞的方法， 作用是用来等待客户端发送请求过来进行连接
				socket = ss.accept();
				
				/**
				 * 此处的最大问题就是：每次有客户端发送请求，都会启动一个线程去执行这个客户端的请求
				 * 并且readLine方法还是阻塞的方法。
				 * 属于一种一对一的服务模式， 很浪费资源
				 */
				new Thread(new TaskThread(socket)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class TaskThread implements Runnable{
	
	private Socket socket = null;
	
	public TaskThread(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			// 获取到 socket 连接的 输入输出流， 用来和 客户端进行交互， 当然最好的方式，应该是和客户端进行 字符串的交互
			// 比如客户端给服务器发送的数据是字符串格式， 那么服务端给客户端返回的数据也是 字符串格式是最好的形式
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			in = new BufferedReader(new InputStreamReader(inputStream));
			out = new PrintWriter(outputStream, true);  // true的作用表示每次写入一条数据之后自动进行flush
			
			while(true){
				System.out.println("开始等待获取客户端发送过来的数据：......");
				String request = in.readLine();
				
				// 输出一下服务端接收的参数格式
				System.out.println("服务端接收到客户端发送过来的数据 : " + request);
				
				if(request == null){
					System.out.println("当前客户端没有任何请求了，关闭链接");
					break;
				}
				
				// 正常业务处理
				if(request.equals("QUERY SERVER TIME")){
					String serverTime = TimeUtil.getNowTime();
					out.println(serverTime);
				}else if(request.equals("QUERY SERVER DATE")){
					String serverTime = TimeUtil.getNowDate();
					out.println(serverTime);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			// 关闭输入流
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			
			// 关闭输出流
			if(out != null){
				out.close();
			}
			
			// 关闭当前已经处理完毕的Socket链接
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}
		}
		
	}
}
