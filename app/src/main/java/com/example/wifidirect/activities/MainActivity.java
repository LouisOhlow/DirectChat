package com.example.wifidirect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wifidirect.BroadcastReceiver;
import com.example.wifidirect.LoadingDialog;
import com.example.wifidirect.MainActivityController;
import com.example.wifidirect.MyAdapter;
import com.example.wifidirect.R;

import java.net.ServerSocket;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Wifidirect: Mainactivity: ";

    private final IntentFilter intentFilter = new IntentFilter();

    View.OnClickListener listItemOnClick;

    private MainActivityController mMainActivityController;

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;

    private BroadcastReceiver receiver;

    public MyAdapter mAdapter;
    private RecyclerView recyclerView;

    public TextView p2pInfoText;

    public LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // create the Singleton as the MVC Controller
        mMainActivityController = MainActivityController.getSC();
        mMainActivityController.setMainActivity(this);

        //the WifiP2PManager class
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(this, getMainLooper(), null);
        Log.d(TAG, "channel initialized");

        mMainActivityController.initialize(channel, manager);
        mMainActivityController.startSearch();

        mMainActivityController.turnOnWifi(); //TODO turn on location
        p2pInfoText = findViewById(R.id.p2pInfo);

        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);

        initButtons();
        setupRecyclerView();
        setupIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //init the BR to receive Broadcasts for WifiDirect
        receiver = new BroadcastReceiver(manager, channel, this, mAdapter, mMainActivityController.peerListListener);
        registerReceiver(receiver, intentFilter);

        Log.d(TAG, "startSearch - onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        mMainActivityController.disconnect();
        //Disable receiver
        unregisterReceiver(receiver);
        Log.d(TAG, "startSearch - onPause");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainActivityController.disconnect();
        // TODO disconnect on app closing
    }

    private void setupIntents(){
        //set up the Intenfilter to which the BroadcastReceiver should listen to

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Log.d(TAG, "set up all Intent");
    }

    private void setupRecyclerView(){
        //init the recycler view to display listItems
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //specifies an Adapter for the RecyclerView
        mAdapter = new MyAdapter(mMainActivityController.getPeerList(), listItemOnClick);
        receiver = new BroadcastReceiver(manager, channel, this, mAdapter, mMainActivityController.peerListListener);
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "setup Recycler View");

    }

    private void initButtons() {
        //The OnClick method for the listItems in the RecyclerView
        listItemOnClick = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                mMainActivityController.connectToPeer(itemPosition, manager, channel);
                loadingDialog.show(getSupportFragmentManager(), "load Dialog");
            }
        };

    }

    public void startChatView(){
        Intent chatStartIntent = new Intent(this, ChatTestView.class);
        this.startActivity(chatStartIntent);
    }
}

