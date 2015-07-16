package com.wql.nettyudp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

import java.net.InetSocketAddress;

/**
 * @author wuqinglong
 * @date 2015年7月17日 下午2:44:35
 */
public class ChineseProverbServer {

	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new ChineseProverbServer().run(Integer.parseInt(args[0]));
	}

	public void run(final int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap boot = new Bootstrap();
			boot.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new SimpleChannelInboundHandler<DatagramPacket>() {

				private final String[] DICTIONARY = { "只要功夫深，铁棒磨成针。", "旧时王谢堂前燕，飞入寻常百姓家。", "洛阳亲友如相问，一片冰心在玉壶。", "一寸光阴一寸金，寸金难买寸光阴。", "老骥伏枥，志在千里。烈士暮年，壮心不已。" };

				@Override
				protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
					String req = msg.content().toString(CharsetUtil.UTF_8);
					System.out.println("req=" + req);
					if ("谚语字典查询？".equals(req)) {
						ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语查询结果： " + nextQuote(), CharsetUtil.UTF_8), msg.sender()));
					}
				}

				public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
					cause.printStackTrace();
					ctx.close();
				}

				private String nextQuote() {
					return DICTIONARY[ThreadLocalRandom.current().nextInt(DICTIONARY.length)];
				}

			});
			boot.bind(new InetSocketAddress(port)).sync().channel().closeFuture().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
