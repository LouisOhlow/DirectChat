package com.example.wifidirect.activities;

import android.os.Bundle;

import com.example.wifidirect.BroadcastReceiver;
import com.example.wifidirect.controller.ChatActivityController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wifidirect.R;
import com.example.wifidirect.ui.MyAdapter;

public class ChatActivity extends AppCompatActivity {

    TextView messageText;
    TextView sendButton;

    public MyAdapter mAdapter;
    private RecyclerView recyclerView;

    private BroadcastReceiver receiver;

    private String TAG = "wifidirect: ChatActivity";

    ChatActivityController mChatActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //#TODO open dialog window

        mChatActivityController = ChatActivityController.getSC();
        mChatActivityController.init(this, handler);

        setupRecyclerView();

        messageText = findViewById(R.id.message);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageText.getText().toString().trim().length() > 0) {
                    mChatActivityController.sendMessage(messageText.getText().toString());
                    messageText.setText("");
                }
            }
        });

        //#TODO load db with MAC Address and close dialog window after loading
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatActivityController.sendMacAddress();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "message incoming...");
            byte[] readByte = (byte[]) msg.obj;
            String tempMessage = new String(readByte, 0, msg.arg1);
            mChatActivityController.receiveMessage(tempMessage);
        return true;
        }
    });

    public void loadChat() {
        if(true){
            mAdapter.update(mChatActivityController.getChatList());
            if((mChatActivityController.getChatList().length-1) > 0){
                recyclerView.smoothScrollToPosition(mChatActivityController.getChatList().length-1);
            }
            Log.d(TAG, "updated chat history");
        }
        else{
        }
    }

    private void setupRecyclerView(){
        //init the recycler view to display listItems
        recyclerView = findViewById(R.id.messageRec);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if((mChatActivityController.getChatList().length-1) > 0){
            recyclerView.smoothScrollToPosition(mChatActivityController.getChatList().length-1);
        }

        //specifies an Adapter for the RecyclerView
        mAdapter = new MyAdapter(mChatActivityController.getChatList(), R.layout.chatitem);
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "setup Recycler View");
    }
}
