package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.ComplexTypeComponent;
import com.example.vitaliy.myapplication.Entity.ComplexTypeComponentForServer;

import java.util.ArrayList;

public class ComplexTypeHasComponentModel extends BaseModel{

    public ComplexTypeHasComponentModel(Context context)
    {
        super(context);
        tableName = "complex_has_components";
    }

    public void deleteComponentFromComplexType(int complex_type_has_component_id)
    {
        deleteItem(complex_type_has_component_id);
    }

    public void addComponentToComplexType(int complexTypeId, int componentId)
    {
        String sql = "INSERT INTO complex_type_has_components (complex_type_id, component_id)" +
                " VALUES ("+complexTypeId+", "+componentId+");";
        db.execute(sql);
    }

    public ArrayList<ComplexTypeComponentForServer> getAll()
    {
        String sql = "SELECT * FROM complex_type_has_components";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        ArrayList<ComplexTypeComponentForServer> array_list = new ArrayList<ComplexTypeComponentForServer>();

        while(res.isAfterLast() == false){
            ComplexTypeComponentForServer obj = new ComplexTypeComponentForServer();
            obj.id = res.getInt(res.getColumnIndex("id"));
            obj.complexTypeId = res.getInt(res.getColumnIndex("complex_type_id"));
            obj.componentId = res.getInt(res.getColumnIndex("component_id"));

            array_list.add(obj);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<ComplexTypeComponentForServer> getAllForServer()
    {
        String sql = "SELECT cthc.* FROM complex_type_has_components AS cthc\n" +
                "JOIN complex_types AS ct ON ct.id=cthc.complex_type_id\n" +
                "WHERE ct.sync=0 OR ct.sync IS NULL;";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        ArrayList<ComplexTypeComponentForServer> array_list = new ArrayList<ComplexTypeComponentForServer>();

        while(res.isAfterLast() == false){
            ComplexTypeComponentForServer obj = new ComplexTypeComponentForServer();
            obj.id = res.getInt(res.getColumnIndex("id"));
            obj.complexTypeId = res.getInt(res.getColumnIndex("complex_type_id"));
            obj.componentId = res.getInt(res.getColumnIndex("component_id"));

            array_list.add(obj);
            res.moveToNext();
        }
        return array_list;
    }
}
