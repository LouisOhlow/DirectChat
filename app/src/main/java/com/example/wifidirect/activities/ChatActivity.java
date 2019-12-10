package com.example.wifidirect.activities;

import android.os.Bundle;

import com.example.wifidirect.controller.ChatActivityController;
import com.example.wifidirect.serverclient.SendReceive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wifidirect.R;

public class ChatActivity extends AppCompatActivity {


    public TextView tempText;
    TextView messageText;
    TextView sendButton;

    private String TAG = "wifidirect: ChatActivity";

    ChatActivityController mChatActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChatActivityController = ChatActivityController.getSC();
        mChatActivityController.init(this, handler);


        tempText = findViewById(R.id.textView2);
        messageText = findViewById(R.id.message);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatActivityController.sendMessage(messageText.getText().toString());
            }
        });
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
}
