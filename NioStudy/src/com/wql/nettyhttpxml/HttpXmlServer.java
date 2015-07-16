package com.wql.nettyhttpxml;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import com.wql.nettyhttpxml.base.HttpXmlRequest;
import com.wql.nettyhttpxml.base.HttpXmlRequestDecoder;
import com.wql.nettyhttpxml.base.HttpXmlResponse;
import com.wql.nettyhttpxml.base.HttpXmlResponseEncoder;
import com.wql.nettyhttpxml.base.Order;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:53:38
 */
public class HttpXmlServer {

	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new HttpXmlServer().run(Integer.parseInt(args[0]));
	}

	public void run(final int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("xml-decoder", new HttpXmlRequestDecoder(Order.class, true));
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
					ch.pipeline().addLast("xml-encoder", new HttpXmlResponseEncoder());
					ch.pipeline().addLast("xmlServerHandler", new SimpleChannelInboundHandler<HttpXmlRequest>() {

						@Override
						protected void messageReceived(final ChannelHandlerContext ctx, HttpXmlRequest msg) throws Exception {
							HttpRequest request = msg.getRequest();
							Order order = (Order) msg.getBody();
							System.out.println("Http server receive request : " + order);
							// do business
							order.setTotal((float) (order.getTotal() * 0.5));
							ChannelFuture future = ctx.writeAndFlush(new HttpXmlResponse(null, order));
							if (!HttpHeaderUtil.isKeepAlive(request)) {
								future.addListener(new GenericFutureListener<Future<? super Void>>() {
									@Override
									public void operationComplete(Future<? super Void> future) throws Exception {
										ctx.close();
									}
								});
							}
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							if (ctx.channel().isActive()) {
								FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer("失败: " + HttpResponseStatus.INTERNAL_SERVER_ERROR.toString() + "\r\n", CharsetUtil.UTF_8));
								response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
								ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
							}
						}
					});
				}
			});
			server.bind(new InetSocketAddress(port)).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
