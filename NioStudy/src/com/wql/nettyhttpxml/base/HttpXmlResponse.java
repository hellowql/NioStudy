package com.wql.nettyhttpxml.base;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:15:47
 */
public class HttpXmlResponse {

	private FullHttpResponse response;

	private Object result;

	public HttpXmlResponse(FullHttpResponse response, Object result) {
		this.response = response;
		this.result = result;
	}

	public final FullHttpResponse getResponse() {
		return response;
	}

	public final void setResponse(FullHttpResponse response) {
		this.response = response;
	}

	public final Object getResult() {
		return result;
	}

	public final void setResult(Object result) {
		this.result = result;
	}

}
