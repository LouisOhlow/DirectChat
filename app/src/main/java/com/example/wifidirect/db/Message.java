package com.example.wifidirect.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "message")
        //primaryKeys = { "message_id", "conversation_id" },
        //indices = { @Index("conversation_id") },
        //foreignKeys = @ForeignKey(entity = Macaddress.class,
        //        parentColumns = "id",
        //        childColumns = "conversation_id",
        //        onDelete = ForeignKey.CASCADE))
public class Message {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    private int messageId;

    @NonNull
    @ColumnInfo(name = "conversation_id")
    private Integer conversationId;

    //@ColumnInfo(name = "mac_addess")
    //private String macAddress;

    @NonNull
    @ColumnInfo(name = "text")
    private String text;

    @NonNull
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @NonNull
    @ColumnInfo(name = "role")
    private Boolean role;


    public Message(Integer conversationId, String text, String timestamp, Boolean role) {
        this.conversationId = conversationId;
        // this.macAddress = macAddress;
        this.text = text;
        this.timestamp = timestamp;
        this.role = role;
    }

    public int getMessageId(){ return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public Integer getConversationId() { return conversationId; }
    public void setConversationId(Integer conversationId) { this.conversationId = conversationId; }

    //public String getMacAddress() { return macAddress; }
    //public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Boolean getRole() { return role; }
    public void setRole(Boolean role) { this.role = role; }
}
