package com.webserver.base;

public class Server {
	public static void main(String[] args){
		SocketConnector server = new SocketConnector(9000);
		new Thread(server).start();
		try {
		    Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
