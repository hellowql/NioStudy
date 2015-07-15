package com.wql.nettyhttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wuqinglong
 * @date 2015年7月15日 下午4:49:43
 */
public class HttpFileServer {
	private static final String DEFAULT_URL = "/src/com/wql/";

	public static void main(String[] args) {
		if (null == args || args.length != 2)
			args = new String[] { "8000", DEFAULT_URL };
		new HttpFileServer().run(Integer.parseInt(args[0]), args[1]);

	}

	public void run(final int port, final String url) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
					ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
				}
			});
			System.out.println("HTTP 文件目录服务器启动，网站是 ： http://localhost:" + port + url);
			boot.bind("localhost", port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
