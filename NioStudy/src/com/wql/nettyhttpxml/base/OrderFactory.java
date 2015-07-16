package com.wql.nettyhttpxml.base;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:41:05
 */
public class OrderFactory {

	public static Order create(long orderID) {
		Address billTo = new Address();
		billTo.setCity("HeFei");
		billTo.setCountry("China");
		billTo.setPostCode("230000");
		billTo.setState("state");
		billTo.setStreet1("street1");
		billTo.setStreet2("street2");
		Address shipTo = new Address();
		shipTo.setCity("NanJing");
		shipTo.setCountry("China");
		shipTo.setPostCode("210000");
		shipTo.setState("state");
		shipTo.setStreet1("street1");
		shipTo.setStreet2("street2");

		Order order = new Order();
		order.setOrderNumber(orderID);
		order.setTotal(10000F);
		order.setBillTo(billTo);
		order.setShipTo(shipTo);
		order.setShipping(Shipping.INTERNATIONAL_EXPRESS);
		return order;
	}

}
