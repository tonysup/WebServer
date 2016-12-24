package com.webserver.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Work implements Runnable {

	private Socket clientSocket = null;
	
	public Work(Socket socket){
		this.clientSocket = socket;
	}
	
	@Override
	public void run() {
		try {
			InputStream inputStream = this.clientSocket.getInputStream();
			OutputStream outputStream = this.clientSocket.getOutputStream();
			long time = System.currentTimeMillis();
			outputStream.write(("HTTP/1.1 200 OK\n\nWorke: " +time +"").getBytes());
			outputStream.close();
			inputStream.close();
            System.out.println("Request processed: " + time);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
