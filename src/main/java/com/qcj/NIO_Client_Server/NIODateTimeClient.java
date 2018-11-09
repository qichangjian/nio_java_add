package com.qcj.NIO_Client_Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class NIODateTimeClient {

    public static void main(String[] args) {
        NIODateTimeClient server = new NIODateTimeClient();
        server.init();
        server.listen();
    }

    private Selector selector;
    /**
     * 启动客户端
     */
    private void init() {
        try {
            // 启动客户端程序的一个启动类clientChannel
            SocketChannel clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);

            // 启动一个管家线程：多路复用器
            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_CONNECT);

            // 发起请求和服务器建立连接
            clientChannel.connect(new InetSocketAddress("localhost", 9527));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean flag = false;
    /**
     * 启动监听，监测是否服务端返回来结果，进行处理
     */
    private void listen() {
        while (true) {
            if (!flag) {
                try {
                    /**
                     * 管家线程每隔2s钟就去轮询一次，查看哪些channel上的IO操作已经操作，其实就是对应注册的事件是否响应
                     * 如果响应，就会把对应的SelectionKey 放在一个set集合中
                     */
                    int number = selector.select(2000);
                    // System.out.println("服务器开始轮询。。。。。。。准备就绪的channel的数量： " +
                    // number);

                    if (number != 0) {
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iter = selectedKeys.iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();

                            /**
                             * 处理这个准备就绪的channel
                             */
                            handleKey(key);

                            // 处理完毕之后应该要移除
                            iter.remove();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }

        }
    }

    private void handleKey(SelectionKey key) {

        if (key.isValid()) {

            SocketChannel clientChannel = (SocketChannel) key.channel();

            if (key.isReadable()) {

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                /**
                 * clientChannel 把服务器写过的数据，写入到 buffer
                 */
                int length = 0;
                try {
                    length = clientChannel.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String result = new String(buffer.array(), 0, length);

                System.out.println("client接收到来自于服务端的处理结果： " + result);

                /**
                 * 到此为止，一次完整的网络交互：
                 *
                 * client send reqeust to server : getNow server send response
                 * to client : datetime
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


                /**
                 * 管家线程没有关闭
                 */
                flag = true;

            } else if (key.isConnectable()) {
                try {

                    /**
                     * 完成连接
                     */
                    boolean finishConnect = clientChannel.finishConnect();

                    // 如果成功
                    if (finishConnect) {
                        clientChannel.register(selector, SelectionKey.OP_READ);


                        /**
                         * 怎么获取到请求数据
                         */
                        String request = handbleRequest();


                        System.out.println("客户端准备好的请求数据是：" + request);

                        // 发送请求数据到服务器
                        writeDataToServer(clientChannel, request);
                    } else {
                        System.out.println("和服务端建立连接失败");
                        System.exit(1);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送请求到服务器
     *
     * @param clientChannel
     * @param request
     */
    private void writeDataToServer(SocketChannel clientChannel, String request) {

        // 把请求数据放置在buffer中，那么其实这个buffer就是写模式
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(request.getBytes());

        // 转换到读模式
        buffer.flip();

        try {

            /**
             * clientChannel 把 buffer 中的数据读取出来，然后写出到 server中去
             */
            clientChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String handbleRequest() {

        String[] requests = new String[]{"getNow", "abcde", "aaaaa"};

        int index = new Random().nextInt(requests.length);

        return requests[index];
    }
}
