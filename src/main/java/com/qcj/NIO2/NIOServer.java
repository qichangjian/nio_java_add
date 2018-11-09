package com.qcj.NIO2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 描述：
 * <p>
 * 异步非阻塞    服务端程序
 */
public class NIOServer {
    private static final int PORT = 9090;
    public static void main(String[] args) {
        new Thread(new ServerSelectorThread(PORT)).start();
    }
}

class ServerSelectorThread implements Runnable {
    private volatile boolean stop = false;
    private Selector selector = null;
    // 当在main线程中启动了服务器的这个Selector线程之后，就应该让服务器准备好各种运行条件
    public ServerSelectorThread(int port) {
        try {
            this.selector = Selector.open();
            ServerSocketChannel open = ServerSocketChannel.open();
            open.configureBlocking(false);
            open.socket().bind(new InetSocketAddress(port), 1024);
            open.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server is start listening port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {

        // 在调用服务器的停止方法之前，一直运行
        while (!stop) {
            try {
                //				System.out.println("每隔1秒轮询一次......");
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    /**
                     * 重点代码
                     */
                    handleInput(next);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭服务器失败");
            }
        }

    }

    private void handleInput(SelectionKey key) {
        if (key.isValid()) {
            // 表示客户端是一个 链接 请求
            if (key.isAcceptable()) {
                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                // accept的作用就是让服务器和客户端 通过三次握手 建立起 链接
                SocketChannel client;
                try {
                    client = server.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("客户端 连接 成功");
            }
            // 表示客户端是一个 IO 请求
            if (key.isReadable()) {

                // 拿到链接上来的客户端Channel，读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bb = ByteBuffer.allocate(1024);
                /*
                 * read>0  读到了字节，对字节进行编解码；
                 * read=0  没有读取到字节，属于正常场景，忽略；
                 * read=-1  链路已经关闭，需要关闭SocketChannel，释放资源
                 */
                int read = 0;
                try {
                    read = sc.read(bb);
                } catch (IOException e) {
                    //					e.printStackTrace();
                    System.out.println("服务端程序读取数据失败 xxx");
                    System.exit(1);
                }
                if (read > 0) {
                    bb.flip();
                    int remaining = bb.remaining();
                    byte[] content = new byte[remaining];
                    bb.get(content);

                    String order = null;
                    try {
                        order = new String(content, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        //						e.printStackTrace();
                        System.out.println("命令编码失败");
                    }
                    System.out.println("服务器接收到客户端的数据：" + order);

                    String result = "";
                    if (order.equals("getNow")) {
                        result = getNow();
                    } else {
                        result = "error order";
                    }
                    writeDataToClient(sc, result);

                    // 处理过后的channel就失效了，所以清空小
                    if (key != null) {
                        key.cancel();
                        if (key.channel() != null) {
                            try {
                                key.channel().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("关闭 客户端的 请求 链接 成功");
                        }
                    }

                } else if (read == 0) {
                    // 未读取到任何数据， 不做任何处理
                } else {
                    // 既然客户端已经关闭链接，那么服务端就没有必要再监听这个链接的OP_READ
                    key.cancel();
                    // 关闭SocketChannel
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void writeDataToClient(SocketChannel sc, String result) {
        if (result != null && result.trim().length() > 0) {
            byte[] content = result.getBytes();
            int length = content.length;
            ByteBuffer bb = ByteBuffer.allocate(length);

            bb.put(content);
            bb.flip();

            try {
                sc.write(bb);
                System.out.println("服务端  发送 结果 到  客户端成功 .... ");
            } catch (IOException e) {
                System.out.println("服务响应数据到客户端失败");
            }
            /**
             * 由于SocketChannel是异步的。 所以可能会一次没有写出数据成功。 那么就有可能出现写半包的情况。
             * 那么就需要该channel注册OP_READ, 不断轮询selector，直到把所有数据写出成功
             * 可以通过ByteBuffer的hasRemaining()方法判断消息是否发送完成。
             * 此处仅仅是各简单的入门级例程，没有演示如何处理“写半包”场景，后面会说到。
             */
        }
    }

    // 调用stop 方法表示 停止服务器
    public void stop() {
        this.stop = true;
    }
    public static String getNow() {
        return new SimpleDateFormat("yy-MM-dd HH:MM:ss").format(new Date());
    }
}
