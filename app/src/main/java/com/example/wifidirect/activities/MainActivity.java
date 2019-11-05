package com.example.wifidirect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.wifidirect.BroadcastReceiver;
import com.example.wifidirect.MainActivityController;
import com.example.wifidirect.MyAdapter;
import com.example.wifidirect.R;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Mainactivity: ";

    private final IntentFilter intentFilter = new IntentFilter();

    private MainActivityController mMainActivityController;

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;

    private BroadcastReceiver receiver;

    public MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainActivityController = MainActivityController.getSC(MainActivity.this);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(this, getMainLooper(), null);

        mMainActivityController.turnOnWifi();
        initButtons();
        setupRecyclerView();
        setupIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver(manager, channel, this, mAdapter, mMainActivityController.peerListListener);
        registerReceiver(receiver, intentFilter);
        Log.d(TAG, "startSearch - onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.d(TAG, "startSearch - onPause");
    }

    private void setupIntents(){
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        /* specify an adapter (see also next example) */
        mAdapter = new MyAdapter(mMainActivityController.getPeerList());
        receiver = new BroadcastReceiver(manager, channel, this, mAdapter, mMainActivityController.peerListListener);
        recyclerView.setAdapter(mAdapter);
    }

    private void initButtons() {
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMainActivityController.startSearch(channel, manager);

                mAdapter.update(mMainActivityController.getPeerList());
            }
        });
    }
}

