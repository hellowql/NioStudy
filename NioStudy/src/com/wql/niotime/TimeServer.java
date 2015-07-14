package com.wql.niotime;

/**
 * @author wuqinglong
 * @date 2015年4月10日 下午2:15:17
 */
public class TimeServer {
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new MultiplexerTimeServe(port), "NIO-MultiplexerTimeServer-001").start();
	}
}
