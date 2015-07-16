package com.wql.nettyudp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author wuqinglong
 * @date 2015年7月17日 下午3:07:56
 */
public class ChineseProverbClient {

	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new ChineseProverbClient().connect(Integer.valueOf(args[0]));
	}

	public void connect(int port) {
		final EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		try {
			boot.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new SimpleChannelInboundHandler<DatagramPacket>() {

				@Override
				protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
					String resp = msg.content().toString(CharsetUtil.UTF_8);
					System.out.println(resp);
					ctx.close();
				}

				public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
					cause.printStackTrace();
					ctx.close();
				}

			});
			Channel channel = boot.bind(0).sync().channel();
			channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语字典查询？", CharsetUtil.UTF_8), new InetSocketAddress("255.255.255.255", port))).sync();
			if (!channel.closeFuture().await(15000)) {
				System.out.println("查询超时！");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

}
