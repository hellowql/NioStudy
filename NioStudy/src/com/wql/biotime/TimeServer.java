package com.wql.biotime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @author wuqinglong
 * @date 2015年4月10日 下午2:15:17
 */
public class TimeServer {
	public static void main(String[] args) {
		int port = 8000;
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port: " + port);
			while (true) {
				final Socket socket = server.accept();
				new Thread(new Runnable() {
					public void run() {
						BufferedReader in = null;
						PrintWriter out = null;
						try {
							in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							out = new PrintWriter(socket.getOutputStream(), true);
							String currentTime = null, body = null;
							while (true) {
								body = in.readLine();
								if (body == null)
									break;
								System.out.println("The time server receive order: " + body);
								currentTime = "query".equalsIgnoreCase(body) ? new Date().toString() : "bad";
								out.println(currentTime);
							}
						} catch (IOException e) {
							e.printStackTrace();
							if (in != null) {
								try {
									in.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							if (out != null) {
								out.close();
								out = null;
							}
							if (socket != null) {
								try {
									socket.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}

					}
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
