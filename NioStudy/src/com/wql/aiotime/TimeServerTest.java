package com.wql.aiotime;

/**
 * @author wuqinglong
 * @date 2015年7月14日 下午7:11:17
 */
public class TimeServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new AsyncTimeServerHandler(port), "AIO-AsyncTimeServerHandler-001").start();
		new Thread(new AsyncTimeClientHandler("localhost", port), "AIO-AsyncTimeClientHandler-001").start();
	}
}
