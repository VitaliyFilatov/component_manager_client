package com.example.vitaliy.myapplication.Service;


import android.content.Context;

import com.example.vitaliy.myapplication.Entity.Settings;

public class SingletonSettings {
    private static SingletonSettings mInstance;
    private Settings settings;
    private static Context mCtx;

    private SingletonSettings(Context context) {
        mCtx = context;
        settings = new Settings();
        settings.setContext(context);
        settings.getFromDB();
    }

    public static synchronized SingletonSettings getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonSettings(context);
        }
        return mInstance;
    }

    public Settings getSettings() {
        return settings;
    }
}
