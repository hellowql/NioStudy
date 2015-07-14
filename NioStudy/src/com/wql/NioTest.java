package com.wql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wuqinglong
 * @date 2014年11月4日 下午3:25:48
 */
public class NioTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		// test1();
		// test2();
		// test3();
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(ByteOrder.BIG_ENDIAN);
		System.out.println(ByteOrder.LITTLE_ENDIAN);
		System.out.println(ByteOrder.nativeOrder());
	}

	private static void test3() {
		int[] ii = new int[] { 1, 2, 3, 4, 5 };
		IntBuffer bb = IntBuffer.wrap(ii, 3, 2);
		// bb.clear();
		System.out.println(bb.slice().arrayOffset());
		while (bb.hasRemaining()) {
			System.out.println(bb.get());
		}
	}

	private static void test2() {
		IntBuffer bb = IntBuffer.allocate(10);
		for (int i = 0; i < bb.capacity(); i++)
			bb.put(i);
		bb.position(3);
		bb.limit(5);
		IntBuffer ib = bb.slice();
		System.out.println(bb.get());
		System.out.println(ib.capacity());
		System.out.println(ib.get(0));
		System.out.println(ib.get(1));
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void test1() throws FileNotFoundException, IOException {
		FileInputStream fin = new FileInputStream("f:/1.txt");
		FileChannel fc = fin.getChannel();
		ByteBuffer bb = ByteBuffer.allocate(60);
		while (fc.read(bb) != -1) {
			System.out.println(new String(bb.array()));
			bb.clear();
		}
		System.out.println(bb.toString());
		fin.close();
	}

}
