package com.example.vitaliy.myapplication.Service;

import android.content.Context;

import com.example.vitaliy.myapplication.Model.DBHelper;

public class SingletonDB {
    private static SingletonDB mInstance;
    private DBHelper db;
    private static Context mCtx;

    private SingletonDB(Context context) {
        mCtx = context;
        db = getDBHelper();
    }

    public static synchronized SingletonDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonDB(context);
        }
        return mInstance;
    }

    public DBHelper getDBHelper() {
        if (db == null) {
            db = new DBHelper(mCtx);
        }
        return db;
    }
}
