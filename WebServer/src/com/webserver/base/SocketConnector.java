package com.webserver.base;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketConnector implements Runnable {
	
	private int port = 9000;
	private ServerSocket  serverSocket = null;
	private boolean isStopped = false;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	public SocketConnector(int port){
		this.port = port;
	}
	
	public void run() {
		openServerSocket();
		while (!isStopped()) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				if(isStopped()){
					System.out.println("Server is stopped");
					return;
				}
				throw new RuntimeException("accept socket error",e);
			}
			this.threadPool.execute(new Work(socket));
		}
		this.threadPool.shutdown();
		System.out.println("Server socket is stopped.");
	}
	
	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO: handle exception
			throw new RuntimeException("Server stop error",e);
		}
	}
	
	private void openServerSocket(){
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			throw new RuntimeException("Open server socket error.",e);
		}
	}
}
