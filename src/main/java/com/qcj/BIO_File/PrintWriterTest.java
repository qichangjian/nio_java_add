package com.qcj.BIO_File;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述：
 * 通过PrintWriter进行往文件写入数据
 */
public class PrintWriterTest {
	public static void main(String[] args) throws IOException {
		PrintWriter pw = new PrintWriter(new File("c:/sss.txt"));
		
		String str = "huangbo";
		for(int i = 0; i<100; i++){
			pw.println(str + i);
		}
		
		pw.close();
	}
}
