package com.qcj.BIO_ThreadPool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：这表示是一个业务。 用来查询服务器的系统时间
 */
public class TimeUtil {

	/**
	 * 日期： 2018年2月27日下午7:50:36
	 * 描述： 查询日期
	 */
	public static String getNowDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	/**
	 * 日期： 2018年2月27日下午7:51:55
	 *
	 * 描述： 查询时间
	 */
	public static String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}
	
	public static void main(String[] args) {
		System.out.println(getNowDate());
		System.out.println(getNowTime());
	}
}
