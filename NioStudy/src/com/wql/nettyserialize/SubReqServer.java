package com.wql.nettyserialize;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author wuqinglong
 * @date 2015年7月15日 下午3:15:22
 */
public class SubReqServer {

	public static void main(String[] args) {
		if (null == args || args.length < 1)
			args = new String[] { "8000" };
		new SubReqServer().bind(Integer.parseInt(args[0]));
	}

	public void bind(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		ServerBootstrap server = new ServerBootstrap();
		try {
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					ch.pipeline().addLast(new ObjectEncoder());
					ch.pipeline().addLast(new ChannelHandlerAdapter() {
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							SubscribeReq req = (SubscribeReq) msg;
							if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
								System.out.println("Service accept client subscribe req : [" + req.toString() + " ]");
								SubscribeResp resp = new SubscribeResp();
								resp.setSubReqID(req.getSubReqID());
								resp.setRespCode(0);
								resp.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
								ctx.writeAndFlush(resp);
							}
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}
					});
				}
			});
			server.bind(port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
