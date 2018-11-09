package com.qcj.NIO_File;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * 描述： FileChannel测试
 *
 * 读取数据到
 */
public class FileChannelTest {
	public static void main(String[] args) throws Exception {
		byteBuffer();
	}
	
	private static void byteBuffer() throws IOException {
		//rw代表读写
		RandomAccessFile randomAccessFile = new RandomAccessFile("c:/words.txt", "rw");
		FileChannel channel = randomAccessFile.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(10);//将缓冲区建立在JVM的内存中，每次10byte大小
		int bytesRead = channel.read(buffer);//从channel读到buffer
		while (bytesRead != -1) {
			System.out.println("读取字节数："+bytesRead);
			//之前是写buffer，现在要读buffer
			buffer.flip();// 切换模式，写->读
			System.out.print(Charset.forName("utf-8").decode(buffer)); // 这样读取，如果10字节恰好分割了一个字符将出现乱码
			buffer.clear();// 清空,position位置为0，limit=capacity
			//  继续往buffer中写
			bytesRead = channel.read(buffer);
		}
		randomAccessFile.close();
	}
}
