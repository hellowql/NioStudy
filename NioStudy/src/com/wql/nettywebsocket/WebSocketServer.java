package com.wql.nettywebsocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * @author wuqinglong
 * @date 2015年7月17日 上午10:31:05
 */
public class WebSocketServer {

	// client:
	// var socket=new WebSocket("ws://localhost:8000/websocket");
	// socket.onmessage=function(e){console.log('msg:',e)};
	// socket.onopen=function(e){console.log('open');socket.send('wql');};
	// socket.onclose=function(e){console.log('close')};
	public static void main(String[] args) {
		if (null == args || args.length != 1)
			args = new String[] { "8000" };
		new WebSocketServer().run(Integer.parseInt(args[0]));
	}

	public void run(final int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(final SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-codec", new HttpServerCodec());
					ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

					ch.pipeline().addLast("xmlServerHandler", new SimpleChannelInboundHandler<Object>() {

						private WebSocketServerHandshaker handshaker;

						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}

						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}

						protected void messageReceived(final ChannelHandlerContext ctx, Object msg) throws Exception {
							if (msg instanceof FullHttpRequest) {
								FullHttpRequest request = (FullHttpRequest) msg;
								// deal with http
								if (!request.decoderResult().isSuccess() || !"websocket".equalsIgnoreCase(null != request.headers().get("Upgrade") ? request.headers().get("Upgrade").toString() : null)) {
									FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
									if (!HttpResponseStatus.OK.equals(response.status())) {
										ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
										response.content().writeBytes(buf);
										buf.release();
										HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
									}
									ChannelFuture future = ctx.channel().writeAndFlush(response);
									if (!HttpHeaderUtil.isKeepAlive(request) || !HttpResponseStatus.OK.equals(response.status())) {
										future.addListener(ChannelFutureListener.CLOSE);
									}
									return;
								}
								// deal with websocket
								WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8000/websocket", null, false);
								handshaker = wsFactory.newHandshaker(request);
								if (null == handshaker) {
									WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
								} else {
									handshaker.handshake(ctx.channel(), request);
								}
							} else if (msg instanceof WebSocketFrame) {
								WebSocketFrame frame = (WebSocketFrame) msg;
								if (frame instanceof CloseWebSocketFrame) {
									handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
									return;
								}
								if (frame instanceof PingWebSocketFrame) {
									ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
									return;
								}
								if (frame instanceof TextWebSocketFrame) {
									String requestStr = ((TextWebSocketFrame) frame).text();
									ctx.channel().writeAndFlush(new TextWebSocketFrame(requestStr + " , 欢迎使用Netty WebSocket服务，现在时刻是：" + new Date()));
									return;
								}
								throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
							}
						}

					});
				}
			});
			System.out.println("Web socket server started at port " + port + ".");
			server.bind(new InetSocketAddress(port)).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
