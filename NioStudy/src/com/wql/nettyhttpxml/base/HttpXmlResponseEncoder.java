package com.wql.nettyhttpxml.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:18:44
 */
public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg, List<Object> out) throws Exception {
		System.out.println("HttpXmlResponseEncoder");
		ByteBuf body = encode0(ctx, msg.getResult());
		FullHttpResponse response = msg.getResponse();
		if (null == response) {
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
		} else {
			response = new DefaultFullHttpResponse(response.protocolVersion(), response.status(), body);
		}
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/xml");
		HttpHeaderUtil.setContentLength(response, body.readableBytes());
		out.add(response);
	}

}
