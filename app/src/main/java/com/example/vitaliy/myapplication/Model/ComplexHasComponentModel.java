package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.ComplexComponentForServer;

import java.util.ArrayList;

public class ComplexHasComponentModel extends BaseModel {

    public ComplexHasComponentModel(Context context)
    {
        super(context);
        tableName = "complex_has_components";
    }

    public void deleteComponentFromComplex(int complex_has_component_id)
    {
        deleteItem(complex_has_component_id);
    }

    public void addComponentToComplex(int complex_id, int component_id)
    {
        String sql = "INSERT INTO complex_has_components" +
                " (complex_id, component_id)" +
                " VALUES ("+complex_id+","+component_id+");";
        db.execute(sql);
    }

    public void deleteComplexComponents(int complexId)
    {
        String sql = "DELETE FROM complex_has_components WHERE complex_id="+complexId;
        db.execute(sql);
    }

    public ArrayList<ComplexComponentForServer> getAll()
    {
        String sql = "SELECT * FROM complex_has_components";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        ArrayList<ComplexComponentForServer> array_list = new ArrayList<ComplexComponentForServer>();

        while(res.isAfterLast() == false){
            ComplexComponentForServer obj = new ComplexComponentForServer();
            obj.id = res.getInt(res.getColumnIndex("id"));
            obj.complexId = res.getInt(res.getColumnIndex("complex_id"));
            obj.componentId = res.getInt(res.getColumnIndex("component_id"));
            obj.code = res.getString(res.getColumnIndex("code"));

            array_list.add(obj);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<ComplexComponentForServer> getAllForServer()
    {
        String sql = "SELECT chc.* FROM complex_has_components AS chc\n" +
                "JOIN complexes AS c ON c.id=chc.complex_id\n" +
                "WHERE c.sync=0 OR c.sync IS NULL;";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();

        ArrayList<ComplexComponentForServer> array_list = new ArrayList<ComplexComponentForServer>();

        while(res.isAfterLast() == false){
            ComplexComponentForServer obj = new ComplexComponentForServer();
            obj.id = res.getInt(res.getColumnIndex("id"));
            obj.complexId = res.getInt(res.getColumnIndex("complex_id"));
            obj.componentId = res.getInt(res.getColumnIndex("component_id"));
            obj.code = res.getString(res.getColumnIndex("code"));

            array_list.add(obj);
            res.moveToNext();
        }
        return array_list;
    }
}
