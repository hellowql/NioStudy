package com.wql.nettyhttpxml;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

import com.wql.nettyhttpxml.base.HttpXmlRequest;
import com.wql.nettyhttpxml.base.HttpXmlRequestEncoder;
import com.wql.nettyhttpxml.base.HttpXmlResponse;
import com.wql.nettyhttpxml.base.HttpXmlResponseDecoder;
import com.wql.nettyhttpxml.base.Order;
import com.wql.nettyhttpxml.base.OrderFactory;

/**
 * @author wuqinglong
 * @date 2015年7月16日 下午2:30:09
 */
public class HttpXmlClient {

	public static void main(String[] args) {
		if (null == args || args.length != 2)
			args = new String[] { "127.0.0.1", "8000" };
		new HttpXmlClient().connect(Integer.valueOf(args[1]), args[0]);
	}

	public void connect(int port, String host) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap boot = new Bootstrap();
			boot.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpResponseDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true));
					ch.pipeline().addLast("http-encoder", new HttpRequestEncoder());
					ch.pipeline().addLast("xml-encoder", new HttpXmlRequestEncoder());
					ch.pipeline().addLast("xmlClientHandler", new SimpleChannelInboundHandler<HttpXmlResponse>() {

						public void channelActive(ChannelHandlerContext ctx) {
							HttpXmlRequest request = new HttpXmlRequest(null, OrderFactory.create(123L));
							ctx.writeAndFlush(request);
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
							cause.printStackTrace();
							ctx.close();
						}

						@Override
						protected void messageReceived(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
							System.out.println("The client receive response of http header is : " + msg.getResponse().headers().names());
							System.out.println("The client receive response of http body is : " + msg.getResult());
						}
					});
				}
			});
			boot.connect(new InetSocketAddress(port)).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
