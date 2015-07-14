package com.wql.biotimetask;

import java.util.Date;

/**
 * @author wuqinglong
 * @date 2015年4月10日 下午2:56:09
 */
public class TimeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				new TimeServer().main(null);
			}
		}).start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long t1 = new Date().getTime();
		for (int i = 0; i < 1000; i++) {
			new TimeClient().main(null);
		}
		System.out.println((new Date().getTime() - t1));
	}

}
