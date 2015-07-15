package com.wql.nettytime;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author wuqinglong
 * @date 2015年7月15日 上午10:34:43
 */
public class TimeClient {

	public static void main(String[] args) {
		if (null == args || args.length != 2)
			args = new String[] { "127.0.0.1", "8000" };
		new TimeClient().connect(Integer.valueOf(args[1]), args[0]);
	}

	public void connect(int port, String host) {
		final EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		try {
			boot.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new ChannelHandlerAdapter() {
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							byte[] req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
							// 发送粘包/拆包问题
							for (int i = 0; i < 100; i++) {
								ByteBuf msg = Unpooled.copiedBuffer(req);
								ctx.writeAndFlush(msg);
							}
						}

						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							// ByteBuf buf = (ByteBuf) msg;
							// byte[] req = new byte[buf.readableBytes()];
							// buf.readBytes(req);
							// String body = new String(req, "utf-8");
							// System.out.println("Now is : " + body);
							System.out.println("Now is : " + msg);
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							ctx.close();
						}
					});
				}
			}).connect(host, port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

}
