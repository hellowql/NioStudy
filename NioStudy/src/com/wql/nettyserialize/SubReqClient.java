package com.wql.nettyserialize;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author wuqinglong
 * @date 2015年7月15日 下午4:02:30
 */
public class SubReqClient {

	public static void main(String[] args) {

		if (null == args || args.length != 2)
			args = new String[] { "127.0.0.1", "8000" };
		new SubReqClient().connect(Integer.valueOf(args[1]), args[0]);
	}

	public void connect(int port, String host) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap boot = new Bootstrap();
			boot.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new ChannelHandlerAdapter() {
						public void channelActive(ChannelHandlerContext ctx) {
							for (int i = 0; i < 10; i++) {
								SubscribeReq req = new SubscribeReq();
								req.setAddress("合肥市高新区");
								req.setPhoneNumber("147********");
								req.setProductName("Netty 权威指南");
								req.setSubReqID(i);
								req.setUserName("Lilinfeng");
								ctx.write(req);
							}
							ctx.flush();
						}

						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println("Receive server response : [ " + msg + " ]");
						}

						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
							cause.printStackTrace();
							ctx.close();
						}
					});
				}
			});
			boot.connect(host, port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
