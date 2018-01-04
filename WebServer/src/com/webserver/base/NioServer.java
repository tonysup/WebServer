package com.webserver.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private int port = 0;
    private Selector selector = null;
    
    private ByteBuffer send = ByteBuffer.allocate(1024);  
    private ByteBuffer receive = ByteBuffer.allocate(1024); 
    
    public NioServer(){
        this.port = 8085;
    }
    
    public void init() throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(this.port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Start.....");
    }
    
    public void listen() throws IOException{
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next(); 
                iterator.remove();
                process(selectionKey);
            }
        }
    }
    
    private void process(SelectionKey key) throws IOException{
        ServerSocketChannel server = null;
        SocketChannel client = null;
        if(key.isAcceptable()){
            server = (ServerSocketChannel)key.channel();
            client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ  | SelectionKey.OP_WRITE);
            System.out.println("accept....");
        }else{
            if(key.isReadable()){
                client = (SocketChannel) key.channel();
                receive.clear();
                client.read(receive);
                System.out.println(new String(receive.array())); 
                key.interestOps(SelectionKey.OP_WRITE);
            }else if(key.isWritable()){
                send.flip();
                client = (SocketChannel) key.channel();
                client.write(ByteBuffer.wrap("Just Test".getBytes()));
                key.interestOps(SelectionKey.OP_READ);
                System.out.println("send....");
            }
        }
    }
    
    public static void main(String args[]){
        NioServer server = new NioServer();
        try {
            server.init();
            server.listen();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
