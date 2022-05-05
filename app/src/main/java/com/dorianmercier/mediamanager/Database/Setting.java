package com.dorianmercier.mediamanager.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Setting {
    @PrimaryKey
    @NonNull
    String name;

    String value;

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
