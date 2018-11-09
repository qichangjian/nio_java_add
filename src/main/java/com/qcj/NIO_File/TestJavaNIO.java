package com.qcj.NIO_File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * 描述： 测试 InputStream 和 OutputStream 的 NIO 读写！！！！
 */
public class TestJavaNIO {
	
	public static void main(String[] args) {
		
//		testReadAndWriteNIO();
//		readNIO();
		writeNIO();
	}
	
	public static void readNIO() {
		String pathname = "C:\\words.txt";
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(new File(pathname));
			FileChannel channel = fin.getChannel();
			
			int capacity = 100;// 字节  
			ByteBuffer bf = ByteBuffer.allocate(capacity);
			System.out.println("限制是：" + bf.limit() + "\t容量是：" + bf.capacity() + "\t位置是：" + bf.position());
			int length = -1;
			
			while ((length = channel.read(bf)) != -1) {
				byte[] bytes = bf.array();
				System.out.write(bytes, 0, length);
				System.out.println();
				
				System.out.println("限制是：" + bf.limit() + "\t容量是：" + bf.capacity() + "\t位置是：" + bf.position());
				
				/*  
				 * 注意，读取后，将位置置为0，将limit置为容量, 以备下次读入到字节缓冲中，从0开始存储  
				 */
				bf.clear();
			}
			channel.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeNIO() {
		String filename = "c:/nio_out.txt";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename));
			FileChannel channel = fos.getChannel();
			ByteBuffer src = Charset.forName("utf8").encode("你好你好你好你好你好");
			// 字节缓冲的容量和limit会随着数据长度变化，不是固定不变的  
			System.out.println("初始化容量:" + src.capacity() + ",\tlimit:" + src.limit());
			int length = 0;
			
			while ((length = channel.write(src)) != 0) {
				/*  
				 * 注意，这里不需要clear，将缓冲中的数据写入到通道中后 第二次接着上一次的顺序往下读  
				 */
				System.out.println("写入长度:" + length);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void testReadAndWriteNIO() {
		String pathname = "C:\\words.txt";
		FileInputStream fin = null;
		
		String filename = "C:\\test-out.txt";
		FileOutputStream fos = null;
		try {
			fin = new FileInputStream(new File(pathname));
			FileChannel channel = fin.getChannel();
			
			int capacity = 100;// 字节  
			ByteBuffer bf = ByteBuffer.allocate(capacity);
			System.out.println("限制是：" + bf.limit() + "\t容量是：" + bf.capacity() + "\t位置是：" + bf.position());
			int length = -1;
			
			fos = new FileOutputStream(new File(filename));
			FileChannel outchannel = fos.getChannel();
			
			while ((length = channel.read(bf)) != -1) {
				//将当前位置置为limit，然后设置当前位置为0，也就是从0到limit这块，都写入到同道中  
				bf.flip();
				
				int outlength = 0;
				while ((outlength = outchannel.write(bf)) != 0) {
					System.out.println("读:" + length + "\t写:" + outlength);
				}
				
				//将当前位置置为0，然后设置limit为容量，也就是从0到limit（容量）这块，  
				//都可以利用，通道读取的数据存储到  
				//0到limit这块  
				bf.clear();
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}