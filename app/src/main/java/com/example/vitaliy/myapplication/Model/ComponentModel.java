package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.CommonInfo;

import java.util.ArrayList;

public class ComponentModel extends CommonInfoModel {
    public ComponentModel(Context context)
    {
        super(context);
        tableName = "components";
        isSyncronizable = true;
    }

    public ArrayList<CommonInfo> getAllComponents()
    {
        return getAllInstances();
    }

    public ArrayList<CommonInfo> getAllComponentsForServer()
    {
        ArrayList<CommonInfo> array_list = new ArrayList<CommonInfo>();

        String sql = "SELECT * FROM " + tableName +" WHERE sync=0 OR sync IS NULL";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new CommonInfo(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name"))));
            res.moveToNext();
        }
        return array_list;
    }


    public void deleteComponent(int id)
    {
        deleteItem(id);
    }

    public void addNewComponent(String name)
    {
        String sql = "INSERT INTO components (name)" +
                " VALUES ('"+name+"');";
        db.execute(sql);
    }

}
