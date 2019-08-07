package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.CommonInfo;

import java.util.ArrayList;

public class ObjectModel extends CommonInfoModel{
    public ObjectModel(Context context)
    {
        super(context);
        tableName = "objects";
        isSyncronizable = true;
    }

    public ArrayList<CommonInfo> getAllObjectsForServer() {
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

    public ArrayList<CommonInfo> getAllObjects() {
        return getAllInstances();
    }

    public void deleteObject(int id)
    {
        AddressModel addressModel = new AddressModel(getContext());
        addressModel.deleteAddressesByObjectId(id);
        deleteItem(id);
    }

    public void addNewObject(String name)
    {
        String sql = "INSERT INTO objects (name)" +
                " VALUES('"+name+"');";
        db.execute(sql);
    }

}
