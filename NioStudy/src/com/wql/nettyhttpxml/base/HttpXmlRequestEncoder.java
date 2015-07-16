package com.wql.nettyhttpxml.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetAddress;
import java.util.List;

/**
 * @author wuqinglong
 * @date 2015年7月16日 上午11:43:53
 */
public class HttpXmlRequestEncoder extends AbstractHttpXmlEncoder<HttpXmlRequest> {

	@Override
	protected void encode(ChannelHandlerContext ctx, HttpXmlRequest msg, List<Object> out) throws Exception {
		System.out.println("HttpXmlRequestEncoder");
		ByteBuf body = encode0(ctx, msg.getBody());
		FullHttpRequest request = msg.getRequest();
		if (null == request) {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/do", body);
			HttpHeaders headers = request.headers();
			headers.set(HttpHeaderNames.HOST, InetAddress.getLocalHost().getHostAddress());
			headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString() + ',' + HttpHeaderValues.DEFLATE.toString());
			headers.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "zh");
			headers.set(HttpHeaderNames.USER_AGENT, "Netty xml Http Client side");
			headers.set(HttpHeaderNames.ACCEPT, "text/xml,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		}
		HttpHeaderUtil.setContentLength(request, body.readableBytes());
		out.add(request);
	}

}
