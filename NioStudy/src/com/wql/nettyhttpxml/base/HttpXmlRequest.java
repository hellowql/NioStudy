package com.wql.nettyhttpxml.base;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午1:53:34
 */
public class HttpXmlRequest {

	private FullHttpRequest request;

	private Object body;

	public HttpXmlRequest(FullHttpRequest request, Object body) {
		this.request = request;
		this.body = body;
	}

	public final FullHttpRequest getRequest() {
		return request;
	}

	public final void setRequest(FullHttpRequest request) {
		this.request = request;
	}

	public final Object getBody() {
		return body;
	}

	public final void setBody(Object body) {
		this.body = body;
	}
}
