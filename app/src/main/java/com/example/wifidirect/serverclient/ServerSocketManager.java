package com.example.wifidirect.serverclient;


import android.os.Looper;
import android.util.Log;

import com.example.wifidirect.activities.MainActivity;
import com.example.wifidirect.controller.MainActivityController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private SendReceive sendReceive;
    MainActivityController mMainActivityController = MainActivityController.getSC();

    String TAG = "Wifidirect: ServerSockerManager";

    @Override
    public void run() {
        super.run();
        Looper.prepare();
            try{
                serverSocket = new ServerSocket(3434);
                Log.d(TAG, "connecting to Server...");
                socket = serverSocket.accept();
                Log.d(TAG, "successfully connected to Client...");

                mMainActivityController.serverConnected(true, socket);


            }catch(IOException e){
                e.printStackTrace();
                Log.d(TAG, "could not connect to Server");
                mMainActivityController.serverConnected(false, socket);
            }

    }

}
