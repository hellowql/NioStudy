package com.wql.nettyhttpxml.base;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午1:43:50
 */
@SuppressWarnings("unchecked")
public class BeanXMLUtils {

	private static final XStream XSTREAM = new XStream();

	public static String bean2xml(Object bean) {
		return XSTREAM.toXML(bean);
	}

	public static <T> T xml2bean(String xml, Class<T> cls) {
		return (T) XSTREAM.fromXML(xml);
	}

	public static <T> T xml2bean(InputStream xml, Class<T> cls) {
		return (T) XSTREAM.fromXML(xml);
	}
}
