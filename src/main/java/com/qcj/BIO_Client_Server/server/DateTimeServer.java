package com.qcj.BIO_Client_Server.server;

import com.qcj.BIO_Client_Server.util.DateTimeUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DateTimeServer {
    private static final int PORT = 9527;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);

        while(true){
            //如果这句代码返回，就证明客户端发送了一个连接请求过来了
            System.out.println("等待客户端连接中......(Main Thread)");
            Socket socket = server.accept();
            System.out.println("已经有一个客户端连接上！");
            //客户端连接后开启线程
            new Thread(new TaskThread(socket)).start();
            /**
             * 最终就两点坏处：
             * 1、本身server就是资源有限，不可能无限制的创建很多的资源。
             * 2、而且还有很多的线程都是闲置（而是处理等待状态中， 没有进行任何的业务处理）的
             */

            /**
             * 1、改变服务模式
             */
        }

    }
}

class TaskThread implements Runnable{
    private Socket socket;

    public TaskThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 针对每一个连接进来的用户都进行对应的处理
        // 我们的处理方式：一个用户连接就创建一个线程进行单独处理
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            //读取请求数据
            inputStream = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println("正在读取请求......（quickly）");

            /**
             *阻塞的方法：如果客户端一直不输入，就一直阻塞
             */
            //当前这个放在在执行过程中，一定会等待这个方法的返回
            String request = br.readLine();
            System.out.println("请求的参数是：" + request);
            //处理请求得到的结果
            String result = null;
            if(request.equals("getNow")){
                result = DateTimeUtil.getNow();
            }else {
                result = "error";
            }

            //返回时间结果到client
            outputStream = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(outputStream,true);
            pw.println(result);
            System.out.println("server返回结果给client成功");

        } catch (IOException e) {
            System.out.println("服务端程序异常！！！");
        }finally {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("服务端关闭异常！！！");
                System.exit(0);
            }
        }

    }
}
