package com.qcj.BIO_Client_Server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模拟时间同步
 */
public class DateTimeUtil {
    /**
     * 得到当前系统时间
     */
    public static String getNow(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sf.format(new Date());
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.getNow());
    }
}
