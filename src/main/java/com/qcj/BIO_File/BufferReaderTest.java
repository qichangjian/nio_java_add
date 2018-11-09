package com.qcj.BIO_File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 描述：
 *  通过BufferedReader进行文件的读取
 */
public class BufferReaderTest {

	public static void main(String[] args) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader(new File("c:/student.txt")));
		
		String line = null;
		while( (line = br.readLine()) != null){
			System.out.println(line);
		}
	
		br.close();
	}
}
