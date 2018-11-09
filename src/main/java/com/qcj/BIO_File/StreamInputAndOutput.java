package com.qcj.BIO_File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 描述：
 * 测试文件字节输入 输出流
 */
public class StreamInputAndOutput {
	public static void main(String[] args) throws Exception {
		InputStream input = new FileInputStream(new File("c:/student.txt"));
		OutputStream output = new FileOutputStream(new File("c:/s.txt"));
		
		byte[] buffer = new byte[4096];
		int length = 0;
		while((length = input.read(buffer)) != -1){
			output.write(buffer, 0, length);
		}
		
		input.close();
		output.close();
	}
}
