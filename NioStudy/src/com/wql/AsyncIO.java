package com.wql;


/**
 * @author wuqinglong
 * @date 2014年11月4日 下午4:29:19
 */
public class AsyncIO {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Selector s = Selector.open();
		ClassLoader loader=Thread.currentThread().getContextClassLoader();
		System.out.println("current loader:"+loader);
		System.out.println("parent loader:"+loader.getParent());
		System.out.println("grandparent loader:"+loader.getParent().getParent());
	}

}
