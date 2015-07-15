package com.wql.nettytime;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * @author wuqinglong
 * @date 2015年7月15日 上午10:16:19
 */
public class TimeServer {

	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new TimeServer().bind(Integer.valueOf(args[0]));
	}

	public void bind(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new ChannelHandlerAdapter() {
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						// ByteBuf buf = (ByteBuf) msg;
						// byte[] req = new byte[buf.readableBytes()];
						// buf.readBytes(req);
						// String body = new String(req, "utf-8");
						// System.out.println("The time server receive order : " + body);
						// String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() :
						// "BAD ORDER";
						// ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
						// ctx.write(resp);

						String body = (String) msg;
						System.out.println("The time server receive order : " + body);
						String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
						currentTime = currentTime + System.getProperty("line.separator");
						ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
						ctx.write(resp);
					}

					public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
						ctx.flush();
					}

					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						ctx.close();
					}
				});
			}
		});
		try {
			ChannelFuture channelFuture = server.bind(port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
