package com.qcj.BIO_Client_Server.test;

import java.util.Scanner;

/**
 * 模拟通信时：client端不输入，server端就一直阻塞
 */
public class ScannerTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("1111111111111111111");
        String nextLine = sc.nextLine();
        System.out.println(nextLine);
        System.out.println("2222222222222222222");
    }
}
