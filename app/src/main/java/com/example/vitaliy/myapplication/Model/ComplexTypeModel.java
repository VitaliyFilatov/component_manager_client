package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.ComplexTypeComponent;

import java.util.ArrayList;

public class ComplexTypeModel extends CommonInfoModel {

    public ComplexTypeModel(Context context)
    {
        super(context);
        tableName = "complex_types";
        isSyncronizable = true;
    }

    public ArrayList<CommonInfo> getAllComplexTypes() {
        return getAllInstances();
    }

    public ArrayList<CommonInfo> getAllComplexTypesForServer()
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

    public void deleteComplexType(int id)
    {
        deleteItem(id);
    }

    public ArrayList<ComplexTypeComponent> getComponentOfComplexType(int complexTypeId)
    {
        ArrayList<ComplexTypeComponent> array_list = new ArrayList<ComplexTypeComponent>();

        Cursor res =  db.getRows( "SELECT complex_type_has_components.id as id," +
                " components.id as component_id," +
                " components.name as name" +
                " FROM complex_type_has_components JOIN components" +
                " ON complex_type_has_components.component_id=components.id" +
                " WHERE complex_type_has_components.complex_type_id="+complexTypeId+";");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new ComplexTypeComponent(res.getInt(res.getColumnIndex("id")),
                    res.getInt(res.getColumnIndex("component_id")),
                    res.getString(res.getColumnIndex("name"))));
            res.moveToNext();
        }
        return array_list;
    }

    public int addNewComplexType(String name)
    {
        String sql = "INSERT INTO complex_types (name)" +
                " VALUES('"+name+"');";
        db.execute(sql);
        sql = "SELECT id FROM complex_types" +
                " WHERE id=(SELECT MAX(id) FROM complex_types);";
        Cursor res =  db.getRows( "SELECT id FROM complex_types" +
                " WHERE id=(SELECT MAX(id) FROM complex_types);");
        res.moveToFirst();
        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("id"));
        }
        return -1;
    }

    public void updateComplexTypeById(int id, String name)
    {
        String sql = "UPDATE complex_types" +
                " SET name='" + name + "' WHERE id=" + id + ";";
        db.execute(sql);
    }

}
