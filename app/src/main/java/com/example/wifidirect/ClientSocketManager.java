package com.example.wifidirect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class ClientSocketManager extends Thread{
    Socket socket;
    String hostAdd;

    public ClientSocketManager(InetAddress address){
        hostAdd = address.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try{
            socket.connect(new InetSocketAddress(hostAdd, 3434), 500);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
