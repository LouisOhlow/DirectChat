package com.example.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class ClientSocketManager extends AsyncTask {
    Socket socket;
    String hostAdd;

    String TAG = "ClientSockerManager";

    public ClientSocketManager(InetAddress address){
        hostAdd = address.getHostAddress();
        socket = new Socket();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            socket.connect(new InetSocketAddress(hostAdd, 3434), 500);
            MainActivityController.getSC().serverConnected(true);
            Log.d(TAG, "successfully connected to Server...");
        }catch(IOException e){
            e.printStackTrace();
            MainActivityController.getSC().serverConnected(false);
            Log.d(TAG, "could not connect to Server");
        }
        return null;
    }
}
