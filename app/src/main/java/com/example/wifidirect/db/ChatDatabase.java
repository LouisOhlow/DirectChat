package com.example.wifidirect.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Macaddress.class, Message.class}, version = 1)
public abstract class ChatDatabase extends RoomDatabase {


    private static final String DATABASE_NAME = "chat_db";
    private static ChatDatabase INSTANCE = null;

    public static ChatDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized ( ChatDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ChatDatabase.class,
                        DATABASE_NAME)
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract MacaddressDao macaddressDao();

    public abstract MessageDao messageDao();

}