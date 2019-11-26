package com.example.wifidirect;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager extends AsyncTask {

    private Socket socket;
    private ServerSocket serverSocket;

    String TAG = "ServerSockerManager";


    @Override
    protected String doInBackground(Object[] objects) {
        try{
            serverSocket = new ServerSocket(3434);
            socket = serverSocket.accept();
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
