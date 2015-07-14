package com.wql.niotime;

/**
 * @author wuqinglong
 * @date 2015年4月10日 下午2:27:22
 */
public class TimeClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new TimeClientHandle("localhost", port), "TimeClient-001").start();
	}
}
