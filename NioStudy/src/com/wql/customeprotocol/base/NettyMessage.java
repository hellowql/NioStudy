package com.wql.customeprotocol.base;

/**
 * @author wuqinglong
 * @date 2015年7月17日 下午5:24:15
 */
public final class NettyMessage {

	private Header header;

	private Object body;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "NettyMessage [header=" + header + ", body=" + body + "]";
	}
}
