package com.wql.nettyhttpxml;

import java.io.StringReader;
import java.io.StringWriter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class JibxTest {

	public static void main(String[] args) {
		// String inputXml = "<?xml version=\"1.0\"
		// encoding=\"UTF-8\"?><Employee
		// id=\"237871\"><name>Cisco</name><hiredate>Jan 03,
		// 2011</hiredate></Employee>";
		// JibxTest jibxTest = new JibxTest();
		// jibxTest.unMarshalEmployee(inputXml);

		Order order = new Order();
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
		order.setShipping(Shipping.INTERNATIONAL_MAIL);
		System.out.println(marshalEmployee(order));
	}

	public static String marshalEmployee(Order order) {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Order.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
			StringWriter stringWriter = new StringWriter();
			mctx.setOutput(stringWriter);
			mctx.marshalDocument(order, "UTF-8", null);
			String output = stringWriter.toString();
			return output;
		} catch (JiBXException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void unMarshalEmployee(String inputXml) {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Order.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			StringReader stringReader = new StringReader(inputXml);
			Order order = (Order) uctx.unmarshalDocument(stringReader, null);
		} catch (JiBXException e) {
			e.printStackTrace();
		}
	}
}
