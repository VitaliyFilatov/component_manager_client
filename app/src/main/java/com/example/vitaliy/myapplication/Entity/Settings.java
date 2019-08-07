package com.example.vitaliy.myapplication.Entity;

import android.content.Context;

import com.example.vitaliy.myapplication.Model.SettingsModel;

public class Settings {
    public boolean type, object, address,
    room, additionalInfo, ipAddress, idByAddress, lightUnset;
    public String serverIp;
    public int pageSize;

    private Context context;

    public Settings()
    {
        type = false;
        object = false;
        address = false;
        room = false;
        additionalInfo = false;
        ipAddress = false;
        idByAddress = false;
        lightUnset = false;
        this.context = context;
        serverIp = "";
        pageSize = 10;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void getFromDB()
    {
        SettingsModel settingsModel = new SettingsModel(context);
        Settings settings = settingsModel.getSettings();
        this.type = settings.type;
        this.object = settings.object;
        this.address = settings.address;
        this.room = settings.room;
        this.additionalInfo = settings.additionalInfo;
        this.ipAddress = settings.ipAddress;
        this.idByAddress = settings.idByAddress;
        this.lightUnset = settings.lightUnset;
        this.serverIp = settings.serverIp;
        this.pageSize = settings.pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public void setType(boolean type)
    {
        this.type = type;
    }

    public void setServerIp(String serverIp)
    {
        this.serverIp = serverIp;
    }

    public void setLightUnset(boolean lightUnset)
    {
        this.lightUnset = lightUnset;
    }

    public void setObject(boolean object)
    {
        this.object = object;
    }

    public void setAddress(boolean address)
    {
        this.address = address;
    }

    public void setRoom(boolean room)
    {
        this.room = room;
    }

    public void setAdditionalInfo(boolean additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }

    public void setIpAddress(boolean ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setIdByAddress(boolean idByAddress)
    {
        this.idByAddress = idByAddress;
    }

}
