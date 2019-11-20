package com.example.wifidirect;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try{
            serverSocket = new java.net.ServerSocket(3434);
            socket = serverSocket.accept();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
