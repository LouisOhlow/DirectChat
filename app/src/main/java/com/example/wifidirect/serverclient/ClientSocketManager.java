package com.example.wifidirect.serverclient;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.wifidirect.controller.MainActivityController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketManager extends Thread {
    Socket socket;
    String hostAdd;
    SendReceive sendReceive;

    boolean isConnecting = true;

    String TAG = "Wifidirect: ClientSockerManager";
    MainActivityController mMainActivityController;

    public ClientSocketManager(InetAddress address){
        hostAdd = address.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        super.run();

        mMainActivityController = MainActivityController.getSC();

        while(isConnecting) {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 3434), 500);
                Log.d(TAG, "connecting to Server...");
                mMainActivityController.serverConnected(true, socket);
                isConnecting = false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "could not connect to Server");
            }
        }
    }

}
