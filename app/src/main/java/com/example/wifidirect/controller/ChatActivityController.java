package com.example.wifidirect.controller;

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
    private String deviceName;
    private String deviceNamePartner;

    private String MACKEY = "Ab6N^C=/QI^[p:<_L.4:_Hh+;~Om3|96]y'u:&(iXjaAerWf2`Nx:<7Qh7+oSu";
    private String TAG = "wifidirect: ChatActivityController";

    private ChatActivity chatActivity;
    private MainActivityController mMainActivityController;

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
        deviceName = mMainActivityController.deviceName;

        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());

        sendMacAddress();
    }

    public void receiveMessage(String tempMessage) {
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());
        Log.d(TAG, "setting message: " + tempMessage);

        if(tempMessage.contains(MACKEY)){
            loadChathistory(tempMessage);
        }
        else {
            Log.d(TAG, "adding message to chat");
            chat.add(deviceNamePartner.split("Phone]")[1] + ": " + tempMessage);
        }
        chatActivity.loadChat(tempMessage);
    }

    public void sendMessage(final String message) {
        Log.d(TAG, "starting Thread to send message..");
        Log.d(TAG, "connection status: " + mMainActivityController.socket.isConnected());

        if(message.contains(MACKEY)){
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

    public String[] getChatList() {
        Log.d(TAG, "getting chat list..");
        return chat.toArray(new String[0]);
    }

    public void sendMacAddress(){
        final String messagePacket = MACKEY + "-----" + getMacAddr() + "-----" + deviceName;
        sendMessage(messagePacket);
    }

    private void loadChathistory(String tempMessage) {
        Log.d(TAG, "received MAC address");
        String[] partnerInfos = tempMessage.split("-----");

        String PARTNERMACADDRESS = partnerInfos[1];
        deviceNamePartner = partnerInfos[2];

        Log.d(TAG, "loading chat history by MAC address: " + PARTNERMACADDRESS);
        //TODO todo4Emily: load message table
        // Am besten 3 Arraylists, eine für die Nachrichten, eine für den boolean wer es geschickt hat
        // und die dritte für den timestamp (Auch wenn wir den evt nicht brauchen werden)
    }

    // TODO Arthur testing
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
        } catch (Exception ignored) {
        }
        return "";
    }
}
