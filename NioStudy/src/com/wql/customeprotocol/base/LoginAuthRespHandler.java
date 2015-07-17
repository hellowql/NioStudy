package com.wql.customeprotocol.base;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends ChannelHandlerAdapter {

	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

	private List<String> whiteList = Arrays.asList(new String[] { "127.0.0.1", "10.6.41.202" });

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
			String nodeIndex = ctx.channel().remoteAddress().toString();
			NettyMessage loginResp = null;
			if (nodeCheck.containsKey(nodeIndex)) {
				// 重复登录拒绝
				System.out.println("重复登录拒绝");
				loginResp = buildLoginResponse((byte) -1);
			} else {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = whiteList.contains(ip);
				loginResp = isOK ? buildLoginResponse((byte) 0) : buildLoginResponse((byte) -1);
				if (isOK)
					nodeCheck.put(nodeIndex, true);
			}
			System.out.println("The login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
			ctx.writeAndFlush(loginResp);
			// System.out.println("Login is OK");
			// String body = (String) message.getBody();
			// System.out.println("Recevied message body from client is " + body);
		} else {
			ctx.fireChannelRead(msg);
		}
		// ctx.writeAndFlush(buildLoginResponse((byte) 3));
	}

	private NettyMessage buildLoginResponse(byte result) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result);
		return message;
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		nodeCheck.remove(ctx.channel().remoteAddress().toString());
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}