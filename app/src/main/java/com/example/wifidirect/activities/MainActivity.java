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
import android.widget.TextView;

import com.example.wifidirect.BroadcastReceiver;
import com.example.wifidirect.MainActivityController;
import com.example.wifidirect.MyAdapter;
import com.example.wifidirect.R;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Mainactivity: ";

    private final IntentFilter intentFilter = new IntentFilter();

    View.OnClickListener listItemOnClick;

    private MainActivityController mMainActivityController;

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;

    private BroadcastReceiver receiver;

    public MyAdapter mAdapter;
    private RecyclerView recyclerView;

    public TextView p2pInfoText;

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

        mMainActivityController.turnOnWifi();
        p2pInfoText = findViewById(R.id.p2pInfo);
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

        //Disable receiver
        unregisterReceiver(receiver);
        Log.d(TAG, "startSearch - onPause");
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

        //The OnClick method for the listItems in the RecyclerView
        listItemOnClick = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                mMainActivityController.connectToPeer(itemPosition, manager, channel);
            }
        };

    }
}

