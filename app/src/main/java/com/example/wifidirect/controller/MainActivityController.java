package com.example.wifidirect.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
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
    private ArrayList<WifiP2pDevice> tempPeers;

    Socket socket;

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    String chatPartnerName;

    private String TAG = "Wifidirect: MainActivityController: ";

    private MainActivityController(){

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo p2PInfo){
                final InetAddress groupOwnerAddress = p2PInfo.groupOwnerAddress;

                if(p2PInfo.groupFormed && p2PInfo.isGroupOwner){
                    mainActivity.p2pInfoText.setText("Host");
                    ServerSocketManager serverSocketManager = new ServerSocketManager();
                    serverSocketManager.start();

                }else if(p2PInfo.groupFormed){
                    mainActivity.p2pInfoText.setText("Client");
                    ClientSocketManager client = new ClientSocketManager(groupOwnerAddress);
                    client.start();
                }
            }
        };

        peers = new ArrayList<>();
        tempPeers = new ArrayList<>();
    }

    public static MainActivityController getSC(){
        if(MainActivityController.mMainActivityController == null){
            MainActivityController.mMainActivityController = new MainActivityController();
        }
        return MainActivityController.mMainActivityController;
    }

    public void initialize(WifiP2pManager.Channel channel, WifiP2pManager manager, MainActivity mainActivity) {
        this.channel = channel;
        this.manager = manager;
        this.mainActivity = mainActivity;
        context = mainActivity.getApplicationContext();
    }

    public void disconnect(){
        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "removeGroup onSuccess -");
                    startSearch();
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

    public void turnOnWifi(){
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) mWifiManager.setWifiEnabled(true);
        else {
            Toast.makeText(context, "please turn on wifi", Toast.LENGTH_LONG).show();
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
            if (!refreshedPeers.equals( MainActivityController.this.tempPeers)) {
                MainActivityController.this.tempPeers.clear();
                MainActivityController.this.tempPeers.addAll(refreshedPeers);
                MainActivityController.this.peers = MainActivityController.this.tempPeers;
                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.
                //TODO Check for mobile names deviceName.contains("Phone:");

                mainActivity.mAdapter.update(getPeerList());
                //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
                for(WifiP2pDevice peer : tempPeers){
                    Log.d(TAG, "name: " + peer.deviceName);
                }
                Log.d(TAG, "added " + refreshedPeers.size() + " peers");
            }
            if (MainActivityController.this.peers.size() == 0) {
                //Log.d(WiFiDirectActivity.TAG, "No devices found");
                Toast.makeText(context, "no devices found", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void connectToPeer(int itemPosition, WifiP2pManager manager, WifiP2pManager.Channel channel){
        WifiP2pDevice peer = peers.get(itemPosition);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        chatPartnerName = peer.deviceName;


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

    public String[] getPeerList(){
        Log.d(TAG, "getting peer list..");
        String[] peerNames = new String[peers.size()];
        for (int i = 0; i < peers.size(); i++) {
            peerNames[i] = peers.get(i).deviceName;
            //Log.d(TAG, "type: " + peers.get(i).deviceName);
        }
        return peerNames;
    }

    public void serverConnected(boolean serverConnected, Socket socket){
        this.socket = socket;
        if(serverConnected) {
            mainActivity.startChatView();
        }
        else{
            disconnect();
        }
        if(!mainActivity.loadingDialog.isHidden()){
            mainActivity.loadingDialog.dismiss();
        }
    }
}
