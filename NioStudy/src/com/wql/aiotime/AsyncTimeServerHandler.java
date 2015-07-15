package com.wql.aiotime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author wuqinglong
 * @date 2015年4月10日 下午4:18:35
 */
public class AsyncTimeServerHandler implements Runnable {

	CountDownLatch latch;

	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandler(int port) {
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void doAccept() {
		asynchronousServerSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>() {
			public void completed(final AsynchronousSocketChannel channel, AsyncTimeServerHandler attachment) {
				attachment.asynchronousServerSocketChannel.accept(attachment, this);
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

					public void completed(Integer result, ByteBuffer attachment) {
						attachment.flip();
						byte[] body = new byte[attachment.remaining()];
						attachment.get(body);
						String req;
						try {
							req = new String(body, "utf-8");
							System.out.println("The time server receive order : " + req);
							String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date().toString() : "BAD ORDER";
							doWrite(currentTime);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

					private void doWrite(String currentTime) {
						if (currentTime != null && currentTime.trim().length() > 0) {
							byte[] bytes = currentTime.getBytes();
							ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
							writeBuffer.put(bytes);
							writeBuffer.flip();
							channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

								public void completed(Integer result, ByteBuffer buffer) {
									if (buffer.hasRemaining())
										channel.write(buffer, buffer, this);
								}

								public void failed(Throwable exc, ByteBuffer attachment) {
									try {
										channel.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
						}
					}

					public void failed(Throwable exc, ByteBuffer attachment) {
						try {
							channel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				});
			}

			public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
				exc.printStackTrace();
				attachment.latch.countDown();
			}
		});
	}

}
