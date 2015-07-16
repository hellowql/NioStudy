package com.wql.nettyfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * @author wuqinglong
 * @date 2015年7月17日 下午3:26:45
 */
public class FileServer {
	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new FileServer().run(Integer.parseInt(args[0]));
	}

	public void run(final int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8), new LineBasedFrameDecoder(1024), new StringDecoder(CharsetUtil.UTF_8));
					ch.pipeline().addLast("xmlServerHandler", new SimpleChannelInboundHandler<String>() {

						private final String CR = System.getProperty("line.separator");

						@Override
						protected void messageReceived(final ChannelHandlerContext ctx, String msg) throws Exception {
							File file = new File(msg);
							if (file.exists()) {
								if (!file.isFile()) {
									ctx.writeAndFlush("Not a file : " + file + CR);
									return;
								}
								ctx.write(file + " " + file.length() + CR);
								RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
								FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
								ctx.write(region);
								ctx.writeAndFlush(CR);
								randomAccessFile.close();
							} else {
								ctx.writeAndFlush("File not found : " + file + CR);
							}
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}
					});
				}
			});
			System.out.println("Start file server at port : " + port);
			server.bind(new InetSocketAddress(port)).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
