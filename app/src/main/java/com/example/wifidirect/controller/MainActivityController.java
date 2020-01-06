package com.example.wifidirect.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.wifidirect.serverclient.ClientSocketManager;
import com.example.wifidirect.serverclient.ServerSocketManager;
import com.example.wifidirect.activities.MainActivity;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;


public class MainActivityController {

    @SuppressLint("StaticFieldLeak")
    private static MainActivityController mMainActivityController;

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;

    private Context context;
    private MainActivity mainActivity;

    private ArrayList<WifiP2pDevice> peers;

    Socket socket;

    public boolean isConnected = false;

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    public String deviceName;

    private String TAG = "Wifidirect: MainActivityController: ";

    private MainActivityController(){

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo p2PInfo){
                final InetAddress groupOwnerAddress = p2PInfo.groupOwnerAddress;

                if(p2PInfo.groupFormed && p2PInfo.isGroupOwner){
                    ServerSocketManager serverSocketManager = new ServerSocketManager();
                    serverSocketManager.start();

                }else if(p2PInfo.groupFormed){
                    ClientSocketManager client = new ClientSocketManager(groupOwnerAddress);
                    client.start();
                }
                else{
                    Log.d(TAG, " not connected");
                    isConnected = false;
                }
            }
        };

        peers = new ArrayList<>();
    }

    public static MainActivityController getSC(){
        if(MainActivityController.mMainActivityController == null){
            MainActivityController.mMainActivityController = new MainActivityController();
        }
        return MainActivityController.mMainActivityController;
    }

    public void initialize(final WifiP2pManager.Channel channel, final WifiP2pManager manager, MainActivity mainActivity) {
        this.channel = channel;
        this.manager = manager;
        this.mainActivity = mainActivity;
        context = mainActivity.getApplicationContext();

        final Handler updateHandler = new Handler();
        final int delay = 7000; //milliseconds

        updateHandler.postDelayed(new Runnable(){
            public void run(){
                //do something
                startSearch();
                updateHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void disconnect(){
        Toast.makeText(context, "could not connect", Toast.LENGTH_LONG).show();
        if(!mainActivity.loadingDialog.isHidden()){
            mainActivity.loadingDialog.dismiss();}

        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "removeGroup onSuccess -");
                    startSearch();
                    isConnected = false;
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "removeGroup onFailure -");
                }
            });/*
            manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "cancelConnect onSuccess -");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "cancelConnect onSuccess -");
                }
            });*/
        }
    }

    public void startSearch(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                Log.d(TAG, "startSearch - onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                // Code for when the discovery initiation fails goes here.
                Log.d(TAG, "startSearch - onFailure");
            }
        });
    }

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                //updates the Recyclerview
                mainActivity.mAdapter.update(getPeerNames());

                Log.d(TAG, "added " + refreshedPeers.size() + " peers");
            }
            if (MainActivityController.this.peers.size() == 0) {
                Log.d(TAG, "No devices found");
            }
        }
    };

    public void connectToPeer(int itemPosition, WifiP2pManager manager, WifiP2pManager.Channel channel){
        WifiP2pDevice peer = peers.get(itemPosition);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        isConnected = true;
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                    Log.d(TAG, "connected succesfully");
                    Toast.makeText(context, "connected succesfully",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(context, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }


    // #TODO Arthur4testing
    public String[] getPeerNames(){
        Log.d(TAG, "getting peer list..");
        String[] peerNames = new String[peers.size()];

        for (int i = 0; i < peers.size(); i++) {
            String tempName = peers.get(i).deviceName;

            if(tempName.contains("[Phone]")) {
                peerNames[i] = tempName.split("Phone]")[1];
            }
        }
        return peerNames;
    }

    public void serverConnected(boolean serverConnected, Socket socket){
        this.socket = socket;
        if(serverConnected) {
            isConnected = true;
            mainActivity.startChatView();
        }
        else{
            isConnected = false;
            disconnect();
        }
    }
}
