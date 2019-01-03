package com.aofei.kettle.utils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ServerChecker implements Callable<Integer> {

	private String host;
	private int port;

	public ServerChecker(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public Integer call() throws Exception {
//		long start = System.currentTimeMillis();
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port),  1000);
			socket.close();

			return 1;
		} catch (Exception e) {
			return 0;
//		} finally {
//			System.out.println("use time[ "+ host + ", " + port + "]: " + (System.currentTimeMillis() - start));
		}


	}

}
