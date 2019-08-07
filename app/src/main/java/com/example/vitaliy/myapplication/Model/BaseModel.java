package com.example.vitaliy.myapplication.Model;

import android.content.Context;

import com.example.vitaliy.myapplication.Service.SingletonDB;

public class BaseModel {
    protected DBHelper db;
    protected String tableName;
    private Context context;
    protected boolean isSyncronizable;

    public BaseModel(Context context) {
        this.context = context;
        db = SingletonDB.getInstance(context).getDBHelper();
        tableName = "";
        isSyncronizable = false;
    }

    public Context getContext()
    {
        return context;
    }

    protected void deleteItem(int id)
    {
        String sql = "DELETE FROM "+tableName+" WHERE id="+id+";";
        db.execute(sql);
    }

    public void synchronize()
    {
        if(isSyncronizable)
        {
            String sql = "UPDATE "+tableName+" SET sync=1" +
                    " WHERE sync=0 OR sync IS NULL";
            db.execute(sql);
        }
    }

}
