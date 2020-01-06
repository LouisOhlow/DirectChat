package com.example.wifidirect.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidirect.db.Message;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChatRecycleAdapter extends RecyclerView.Adapter<ChatRecycleAdapter.MyViewHolder> {
    private String[] mDataset;
    Context context;
    View.OnClickListener onClick;
    String TAG = "Wifidirect: ChatRecycleAdapter";

    public List<Message> messages;

    int chat;
    int partnerChat;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatRecycleAdapter(String[] myDataset, int chat, int partnerChat) {
        mDataset = myDataset;
        this.chat = chat;
        this.partnerChat = partnerChat;
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getRole()){
            return 0;
        }
        else{
            return 1;
        }
    }

    @NonNull
    @Override
    public ChatRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatRecycleAdapter.MyViewHolder vh = null;
        // create a new view
        switch (viewType) {
            case 0:
                TextView v1 = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(chat, parent, false);
                v1.setOnClickListener(onClick);
                vh = new MyViewHolder(v1);
                break;
            case 1:
                TextView v2 = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(partnerChat, parent, false);
                v2.setOnClickListener(onClick);
                vh = new MyViewHolder(v2);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecycleAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }
        else{
            return mDataset.length;
        }
    }

    public void update(List<Message> messages) {
        this.messages = messages;
        String [] tempMessage = new String[messages.size()];
        for(int i = 0; i<messages.size(); i++){
            tempMessage[i] = messages.get(i).getText();
        }
        mDataset = tempMessage;
        notifyDataSetChanged();
    }
}