package com.wql.customeprotocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wql.customeprotocol.base.HeartBeatReqHandler;
import com.wql.customeprotocol.base.LoginAuthReqHandler;
import com.wql.customeprotocol.base.NettyMessageDecoder;
import com.wql.customeprotocol.base.NettyMessageEncoder;

public class NettyClient {

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		try {
			new NettyClient().connect("127.0.0.1", 8000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(final String host, final int port) throws Exception {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					// -8表示lengthAdjustment，让解码器从0开始截取字节，并且包含消息头
					ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
					ch.pipeline().addLast(new NettyMessageEncoder());
					ch.pipeline().addLast(new ReadTimeoutHandler(50));
					ch.pipeline().addLast(new LoginAuthReqHandler());
					ch.pipeline().addLast(new HeartBeatReqHandler());
				}
			});
			ChannelFuture f = b.connect(new InetSocketAddress(host, port)).sync();
			System.out.println("Netty time Client connected at port " + port);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("Client offline, begin to re-connection");
					try {
						TimeUnit.SECONDS.sleep(5);
						try {
							connect(host, port);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
