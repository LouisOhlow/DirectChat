package com.example.wifidirect.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "macaddress")
public class Macaddress {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "partnermac")
    private String partnermacaddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartnermacaddress() {
        return partnermacaddress;
    }

    public void setPartnermacaddress(String partnermacaddress) {
        this.partnermacaddress = partnermacaddress;
    }
}
