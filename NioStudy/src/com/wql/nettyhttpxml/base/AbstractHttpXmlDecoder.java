package com.wql.nettyhttpxml.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:04:49
 */
public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {

	private Class<?> clazz;

	private boolean isPrint;

	final static String CHARSET_NAME = "UTF-8";

	final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

	public AbstractHttpXmlDecoder(Class<?> clazz) {
		this(clazz, false);
	}

	public AbstractHttpXmlDecoder(Class<?> clazz, boolean isPrint) {
		this.clazz = clazz;
		this.isPrint = isPrint;
	}

	protected Object decode0(ChannelHandlerContext ctx, ByteBuf body) throws Exception {
		String content = body.toString(UTF_8);
		if (isPrint)
			System.out.println("The body is : " + content);
		return BeanXMLUtils.xml2bean(content, clazz);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
