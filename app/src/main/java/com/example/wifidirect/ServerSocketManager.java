package com.example.wifidirect;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager extends AsyncTask {

    private Socket socket;
    private ServerSocket serverSocket;

    String TAG = "Wifidirect: ServerSockerManager";


    @Override
    protected String doInBackground(Object[] objects) {
        try{
            serverSocket = new ServerSocket(3434);
            Log.d(TAG, "connecting to Server...");
            socket = serverSocket.accept();
            Log.d(TAG, "successfully connected to Client...");
            MainActivityController.getSC().serverConnected(true);

        }catch(IOException e){
            e.printStackTrace();
            Log.d(TAG, "could not connect to Server");
            MainActivityController.getSC().serverConnected(false);
        }
        return null;
    }
}
