package com.example.wifidirect.controller;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;


import com.example.wifidirect.serverclient.SendReceive;
import com.example.wifidirect.activities.ChatActivity;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatActivityController {
    private static ChatActivityController mChatActivityController;
    private SendReceive sendReceive;
    private ArrayList<String> chat;
    private String chatPartner;

    private String PARTNERMACADDRESS;
    private String MACKEY = "Ab6N^C=/QI^[p:<_L.4:_Hh+;~Om3|96]y'u:&(iXjaAerWf2`Nx:<7Qh7+oSu";
    private String TAG = "wifidirect: ChatActivityController";

    public WifiP2pManager manager;

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
        this.chatActivity = chatActivity;

        chat = new ArrayList<>();

        sendReceive.setHandler(handler);
        sendReceive.start();
        manager = mMainActivityController.manager;
        chatPartner = mMainActivityController.chatPartnerName;
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());

        //#TODO send message with MAC Address
        sendMessage(MACKEY + "----" + getMacAddr() + "----" + mMainActivityController.chatPartnerName);
    }


    public void receiveMessage(String tempMessage) {
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());
        Log.d(TAG, "setting message: " + tempMessage);

        if(isMacAddress(tempMessage)){
            loadMacTable(tempMessage);
        }
        else {
            Log.d(TAG, "adding message to chat");
            chat.add(chatPartner + ": " + tempMessage);
            chatActivity.loadChat(tempMessage);
        }
    }

    private void loadMacTable(String tempMessage) {
        Log.d(TAG, "received MAC address");
        String[] partnerInfos = tempMessage.split("----");

        chatPartner = partnerInfos[2];
        PARTNERMACADDRESS = partnerInfos[1];

        Log.d(TAG, "loading chat history by MAC address: " + PARTNERMACADDRESS);
        //TODO todo4Emily: load message table
        // Am besten 3 Arraylists, eine für die Nachrichten, eine für den boolean wer es geschickt hat
        // und die dritte für den timestamp (Auch wenn wir den evt nicht brauchen werden)
    }

    private boolean isMacAddress(String message){
        return message.contains(MACKEY);
    }

    public void sendMessage(final String message) {
        Log.d(TAG, "starting Thread to send message..");
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());

        if(isMacAddress(message)){
            Log.d(TAG, "sending MAC address..");
        }
        else {
            chat.add("Me: " + message);
            chatActivity.loadChat(message);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Thread: message sending... ");
                sendReceive.write(message.getBytes());
            }
        }).start();
    }

    private String getMacAddr() {
        Log.d(TAG, "getting MAC address..");
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    public String[] getChatList() {
        Log.d(TAG, "getting chat list..");
        String[] chatHistory = chat.toArray(new String[chat.size()]);
        return chatHistory;
    }
}
