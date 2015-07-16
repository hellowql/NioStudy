package com.wql.nettyhttpxml.base;

import java.util.List;

/**
 * @author wuqinglong
 * @date 2015年7月15日 下午6:59:39
 */
public class Customer {

	private long customerNumber;

	private String firstName;

	private String lastName;

	private List<String> middleNames;

	public long getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(long customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<String> getMiddleNames() {
		return middleNames;
	}

	public void setMiddleNames(List<String> middleNames) {
		this.middleNames = middleNames;
	}

}
