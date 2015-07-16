package com.wql.nettyhttpxml.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;

/**
 * @author wuqinglong
 * @date 2015年7月16日 上午11:47:40
 */
public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {

	final static String CHARSET_NAME = "UTF-8";

	final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

	protected ByteBuf encode0(ChannelHandlerContext ctx, Object body) throws Exception {
		return Unpooled.copiedBuffer(BeanXMLUtils.bean2xml(body), UTF_8);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
