package com.webserver.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {

    private int port = 0;
    private SocketChannel sc = null;
    private Selector selector = null;
    
    private ByteBuffer send = ByteBuffer.wrap("data come from client".getBytes());  
    private ByteBuffer receive = ByteBuffer.allocate(1024); 
    
    public NioClient(){
        this.port = 8085;
    }
    
    public void process() throws IOException{
        sc = SocketChannel.open();
        selector = Selector.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", this.port));
        sc.register(selector, SelectionKey.OP_CONNECT|SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        
        while(true){
            if(selector.select() == 0){
                continue;
            }
            
            System.out.println("process.....");
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                sc = (SocketChannel) key.channel();
                if(key.isConnectable()){
                    System.out.println("Connect....");
                    if(sc.isConnectionPending()){
                        sc.finishConnect();
                        sc.write(send);
                        System.out.println("Pending....and write....");
                    }
                }else{
                    if(key.isWritable()){
                        receive.flip();
                        send.flip();
                        sc.write(send);
                        System.out.println("send....");
                    }else if(key.isReadable()){
                        receive.flip();
                        send.flip();
                        sc.read(receive);
                        System.out.println(new String(receive.array()));
                        System.out.println("receive......");
                    }
                }
            }
        }
    }
    
    public static void main(String args[]){
        NioClient client = new NioClient();
        try {
            client.process();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
}
