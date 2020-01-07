package com.example.wifidirect.activities;

import android.os.Bundle;

import com.example.wifidirect.controller.ChatActivityController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifidirect.R;
import com.example.wifidirect.ui.ChatRecycleAdapter;
import com.example.wifidirect.ui.LoadingDBDialog;

public class ChatActivity extends AppCompatActivity {

    TextView messageText;
    TextView sendButton;
    public TextView chatName;
    ImageButton backButton;


    public ChatRecycleAdapter mAdapter;
    private RecyclerView recyclerView;

    private String TAG = "Wifidirect: ChatActivity";

    ChatActivityController mChatActivityController;

    public LoadingDBDialog loadingDBDialog = new LoadingDBDialog();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingDBDialog.setCancelable(false);
        loadingDBDialog.show(getSupportFragmentManager(), "load Dialog");

        mChatActivityController = ChatActivityController.getSC();
        mChatActivityController.init(this, handler);

        setupRecyclerView();

        messageText = findViewById(R.id.message);
        sendButton = findViewById(R.id.sendButton);
        chatName = findViewById(R.id.chatname);
        backButton = (ImageButton) findViewById(R.id.backbutton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageText.getText().toString().trim().length() > 0) {
                    mChatActivityController.sendMessage(messageText.getText().toString());
                    messageText.setText("");
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatActivityController.sendMacAddress();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               loadChat();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void closeActivity(){
        Toast.makeText(getApplicationContext(), "lost connection", Toast.LENGTH_LONG).show();
        finish();
    }

    public void loadChat() {
        try{
        Log.d(TAG, "updated recyclerview by "+ mChatActivityController.messages.size() + " items");
        mAdapter.update(mChatActivityController.messages);
        if((mChatActivityController.messages.size()-1) > 0){
            recyclerView.smoothScrollToPosition(mChatActivityController.messages.size()-1);
        }
        Log.d(TAG, "updated chat history");
        loadingDBDialog.dismiss();}
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(){
        //init the recycler view to display listItems
        recyclerView = findViewById(R.id.messageRec);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //specifies an Adapter for the RecyclerView
        mAdapter = new ChatRecycleAdapter(new String[0], R.layout.chatitem, R.layout.partner_message);
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "setup Recycler View");
    }
}
