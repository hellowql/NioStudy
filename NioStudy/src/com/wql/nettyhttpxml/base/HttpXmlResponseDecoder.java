package com.wql.nettyhttpxml.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:23:05
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

	public HttpXmlResponseDecoder(Class<?> clazz) {
		this(clazz, false);
	}

	public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
		super(clazz, isPrint);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out) throws Exception {
		System.out.println("HttpXmlResponseDecoder");
		HttpXmlResponse response = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
		out.add(response);
	}

}
