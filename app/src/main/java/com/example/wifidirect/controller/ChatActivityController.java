package com.example.wifidirect.controller;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;


import com.example.wifidirect.serverclient.SendReceive;
import com.example.wifidirect.activities.ChatActivity;

import java.net.Socket;


public class ChatActivityController {
    private static ChatActivityController mChatActivityController;
    private SendReceive sendReceive;
    Socket socket;

    private String TAG = "wifidirect: ChatActivityController";

    WifiP2pManager manager;

    ChatActivity chatActivity;
    MainActivityController mMainActivityController;

    private ChatActivityController(){
        mMainActivityController = MainActivityController.getSC();
    }

    public static ChatActivityController getSC(){
        if(ChatActivityController.mChatActivityController == null){
            ChatActivityController.mChatActivityController = new ChatActivityController();
        }
        return ChatActivityController.mChatActivityController;
    }


    public void init(ChatActivity chatActivity, Handler handler){
        this.sendReceive = new SendReceive(mMainActivityController.socket);
        sendReceive.setHandler(handler);
        sendReceive.start();
        this.chatActivity = chatActivity;
        manager = mMainActivityController.manager;
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());
    }


    public void receiveMessage(String tempMessage) {
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());
        Log.d(TAG, "setting message: " + tempMessage);
        chatActivity.tempText.setText(tempMessage);
    }

    public void sendMessage(final String message) {
        Log.d(TAG, "starting Thread to send message..");
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Thread: message sending... ");
                sendReceive.write(message.getBytes());
            }
        }).start();
    }



}
