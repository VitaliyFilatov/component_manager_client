package com.example.vitaliy.myapplication.Service;


import java.util.HashMap;

public class SingletonComponentsCodeStorage {
    private static SingletonComponentsCodeStorage mInstance;

    public HashMap<Integer, String> componentCodes;


    private SingletonComponentsCodeStorage() {
        componentCodes = new HashMap<Integer, String>();
    }

    public static synchronized SingletonComponentsCodeStorage getInstance() {
        if (mInstance == null) {
            mInstance = new SingletonComponentsCodeStorage();
        }
        return mInstance;
    }
}
