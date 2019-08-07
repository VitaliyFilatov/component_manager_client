package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.Settings;
import com.example.vitaliy.myapplication.Service.SingletonSettings;

public class SettingsModel extends BaseModel {
    public SettingsModel(Context context)
    {
        super(context);
    }

    public Settings getSettings()
    {
        Cursor res =  db.getRows( "SELECT * FROM settings");
        res.moveToFirst();

        Settings settings = new Settings();

        if(res.isAfterLast() == false){

            settings.setType((res.getShort(res.getColumnIndex("type"))) > 0);
            settings.setObject((res.getShort(res.getColumnIndex("object"))) > 0);
            settings.setAddress((res.getShort(res.getColumnIndex("address"))) > 0);
            settings.setRoom((res.getShort(res.getColumnIndex("room"))) > 0);
            settings.setAdditionalInfo((res.getShort(res.getColumnIndex("additional_info"))) > 0);
            settings.setIpAddress((res.getShort(res.getColumnIndex("ip_address"))) > 0);
            settings.setIdByAddress((res.getShort(res.getColumnIndex("id_by_address"))) > 0);
            settings.setLightUnset((res.getShort(res.getColumnIndex("light_unset"))) > 0);
            settings.setServerIp((res.getString(res.getColumnIndex("server_ip"))));
            settings.setPageSize((res.getInt(res.getColumnIndex("page_size"))));
        }
        return settings;
    }

    public void setType(boolean value)
    {
        setField("type", value);
    }

    public void setObject(boolean value)
    {
        setField("object", value);
    }

    public void setAddress(boolean value)
    {
        setField("address", value);
    }

    public void setRoom(boolean value)
    {
        setField("room", value);
    }

    public void setAdditionalInfo(boolean value)
    {
        setField("additional_info", value);
    }

    public void setIpAddress(boolean value)
    {
        setField("ip_address", value);
    }

    public void setIdByAddress(boolean value)
    {
        setField("id_by_address", value);
    }

    public void setLightUnset(boolean value)
    {
        setField("light_unset", value);
    }

    public void setServerIp(String value)
    {
        String sql = "UPDATE settings SET server_ip='"+value+"';";
        db.execute(sql);
    }

    public void setPageSize(int value)
    {
        String sql = "UPDATE settings SET page_size='"+value+"';";
        db.execute(sql);
    }


    private void setField(String field, boolean value)
    {
        short val = 0;
        if(value)
            val = 1;
        String sql = "UPDATE settings SET "+field+"="+val;
        db.execute(sql);
    }
}
