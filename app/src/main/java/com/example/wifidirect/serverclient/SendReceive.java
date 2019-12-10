package com.example.wifidirect.serverclient;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread{
    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;
    private Handler handler;

    private static final int MESSAGE_READ = 1;
    private String TAG = "wifidirect: SendReceive ";

    public SendReceive(Socket socket){
        this.socket = socket;
        try{
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[1024];
        int bytes;

        while (socket != null) {
            try {
                bytes = iStream.read(buffer);
                if (bytes > 0) {
                    Log.d(TAG, "successfully fetched inputstream from server");
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "failed to get inputstream from server");
            }
        }
    }

    public void write(byte[] bytes){
        try{
            oStream.write(bytes);
            Log.d(TAG, "successfully wrote message to server");

        } catch (IOException e){
            Log.d(TAG, "failed to write message to server");
            e.printStackTrace();
        }
    }
}
