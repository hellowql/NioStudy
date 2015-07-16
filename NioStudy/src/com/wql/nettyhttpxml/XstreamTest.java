package com.wql.nettyhttpxml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.wql.nettyhttpxml.base.Address;
import com.wql.nettyhttpxml.base.Order;
import com.wql.nettyhttpxml.base.Shipping;

/**
 * @author wuqinglong
 * @date 2015年7月16日 上午11:32:31
 */
public class XstreamTest {

	private XStream xstream = null;

	private ObjectOutputStream out = null;

	private ObjectInputStream in = null;

	private Order order = null;

	@Test
	public void writeBean2XML() {
		try {
			fail("------------Bean->XML------------");
			List<Order> list=new ArrayList<Order>();
			list.add(order);
			list.add(order);
			fail(xstream.toXML(list));
			// 类重命名
			// xstream.alias("account", Student.class);
			// xstream.alias("生日", Birthday.class);
			// xstream.aliasField("生日", Student.class, "birthday");
			// xstream.aliasField("生日", Birthday.class, "birthday");
			// fail(xstream.toXML(bean));
			// 属性重命名
			// xstream.aliasField("邮件", Order.class, "email");
			// 包重命名
			// xstream.aliasPackage("hoo", "com.hoo.entity");
			// fail(xstream.toXML(order));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void init() {
		try {
			xstream = new XStream();
			// xstream = new XStream(new DomDriver()); // 需要xpp3 jar
		} catch (Exception e) {
			e.printStackTrace();
		}
		order = new Order();
		order.setOrderNumber(999);
		order.setTotal(99990F);
		Address billTo = new Address();
		billTo.setCity("HeFei");
		billTo.setCountry("China");
		billTo.setPostCode("230000");
		billTo.setState("state");
		billTo.setStreet1("street1");
		billTo.setStreet2("street2");
		order.setBillTo(billTo);
		Address shipTo = new Address();
		shipTo.setCity("NanJing");
		shipTo.setCountry("China");
		shipTo.setPostCode("210000");
		shipTo.setState("state");
		shipTo.setStreet1("street1");
		shipTo.setStreet2("street2");
		order.setShipTo(shipTo);
		order.setShipping(Shipping.INTERNATIONAL_EXPRESS);
	}

	@After
	public void destory() {
		xstream = null;
		order = null;
		try {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public final void fail(String string) {
		System.out.println(string);
	}

	public final void failRed(String string) {
		System.err.println(string);
	}
}
