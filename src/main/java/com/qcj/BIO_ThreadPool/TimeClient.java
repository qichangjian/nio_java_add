package com.qcj.BIO_ThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 描述：负责发送请求给服务器用来返回服务器的时间， 当然也可以是其他服务的调用
 */
public class TimeClient {
	
	private static final int PORT = 9527;
	private static final String HOST = "localhost";
	
	public static void main(String[] args) {
		
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			
			/**
			 * 第一个参数：服务端程序的地址
			 * 第二个参数：服务端的程序运行时绑定的端口号
			 * 这两个参数必须对应，否则找不到对应的服务端程序
			 */
			socket = new Socket(HOST, PORT);
			
			/**
			 * 获取Socket链接的输入输出流用来和服务端程序进行交互
			 * 
			 * 输入流用来接收 服务端 发送过来的数据
			 * 输出流用来 向服务端发送数据
			 */
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			// String r = "TIME";
			String r = "DATE";
			String request = "QUERY SERVER " + r;
			out.println(request);
			System.out.println("客户端发送请求成功：" + request);
			
			// 这个 readLine 方法是一个阻塞方法， 会一直等到服务端有数据返回的时候，才会返回
			String response = in.readLine();
			System.out.println("Now " + r + " is: " + response);
			
		} catch (UnknownHostException e) {
			System.out.println("主机名 " + HOST + " 不能正常解析 ");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			if (socket != null) {
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
