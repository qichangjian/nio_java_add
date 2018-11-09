package com.qcj.BIO_Client_Server.client;

import java.io.*;
import java.net.Socket;

/**
 * 客户端程序
 *
 *  BIO编程模型：同步阻塞的网络编程模型
 *  两大缺点：
 *      阻塞：导致后边业务没法继续进行
 *      同步：运行当前这个应用程序的线程，不能去做其他的任务处理
 *          只能等待这股代码返回结果，然后继续
 */

/**
 * 使用线程池：就是把一对一的服务模式改为M:N模式
 */
public class DateTimeClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost",9527);
            //提交请求
            handleRequest(socket);
        } catch (IOException e) {
            System.out.println("服务器连接异常");
        }
    }

    private static void handleRequest(Socket socket) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream,true);//true自动刷新

            //模拟客户端取得线程后去干别的了，服务端线程阻塞
            Thread.sleep(20000);

            //发送请求到服务器
            printWriter.println("getNow");
            System.out.println("发送请求成功");

            inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //处理接收的结果
            String result = bufferedReader.readLine();
            System.out.println("接收服务器返回来的处理结果成功：" + result);

        } catch (IOException e) {
            System.out.println("客户端获取输入输出流进行数据读写异常");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                System.exit(0);
            }
        }
    }
}
