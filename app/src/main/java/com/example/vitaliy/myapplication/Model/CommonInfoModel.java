package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Service.SingletonDB;

import java.util.ArrayList;

public class CommonInfoModel extends BaseModel {


    public CommonInfoModel(Context context) {
        super(context);
    }

    public ArrayList<CommonInfo> getAllInstances()
    {
        ArrayList<CommonInfo> array_list = new ArrayList<CommonInfo>();

        String sql = "SELECT * FROM " + tableName;
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new CommonInfo(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name"))));
            res.moveToNext();
        }
        return array_list;
    }
}
