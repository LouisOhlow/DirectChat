package com.example.wifidirect.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createMessage(Message message);

    @Query("SELECT * FROM message WHERE conversation_id = :conversationId ORDER BY timestamp")
    List<Message> loadChatHistory(int conversationId);

}
