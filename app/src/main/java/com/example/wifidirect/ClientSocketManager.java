package com.example.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import com.example.wifidirect.activities.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.SocketHandler;

public class ClientSocketManager extends AsyncTask {
    Socket socket;
    String hostAdd;

    String TAG = "Wifidirect: ClientSockerManager";
    MainActivityController mMainActivityController;

    public ClientSocketManager(InetAddress address){
        hostAdd = address.getHostAddress();
        socket = new Socket();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        mMainActivityController = MainActivityController.getSC();
        try{
            socket.connect(new InetSocketAddress(hostAdd, 3434), 500);
            Log.d(TAG, "connecting to Server...");
            mMainActivityController.serverConnected(true);
        }catch(IOException e){
            e.printStackTrace();
            Log.d(TAG, "could not connect to Server");
            mMainActivityController.serverConnected(false);
        }
        return null;
    }
}
