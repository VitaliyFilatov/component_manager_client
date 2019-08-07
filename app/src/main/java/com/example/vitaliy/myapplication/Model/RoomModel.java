package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.Room;

import java.util.ArrayList;

public class RoomModel extends CommonInfoModel {
    public RoomModel(Context context)
    {
        super(context);
        tableName = "rooms";
        isSyncronizable = true;
    }

    public ArrayList<CommonInfo> getCabinetsByAddressId(int id)
    {
        ArrayList<CommonInfo> array_list = new ArrayList<CommonInfo>();

        Cursor res =  db.getRows( "SELECT id, name FROM rooms" +
                " WHERE address_id="+id+";");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new CommonInfo(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name"))));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Room> getAllRooms()
    {
        ArrayList<Room> array_list = new ArrayList<Room>();

        Cursor res =  db.getRows( "SELECT id, name, address_id FROM rooms;");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new Room(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name")),
                    res.getInt(res.getColumnIndex("address_id"))));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Room> getAllRoomsForServer()
    {
        ArrayList<Room> array_list = new ArrayList<Room>();

        Cursor res =  db.getRows( "SELECT id, name, address_id FROM rooms " +
                "WHERE sync=0 OR sync IS NULL;");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new Room(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("name")),
                    res.getInt(res.getColumnIndex("address_id"))));
            res.moveToNext();
        }
        return array_list;
    }

    public void addNewCabToAddress(int address_id, String cabName)
    {
        String sql = "INSERT INTO rooms (name, address_id)" +
                " VALUES('"+cabName+"', "+address_id+");";
        db.execute(sql);
    }

    public void deleteCabinet(int id)
    {
        ComplexModel complexModel = new ComplexModel(getContext());
        complexModel.unsetRoom(id);
        deleteItem(id);
    }

    public void deleteCabinetsByAddress(int addressId)
    {
        ArrayList<CommonInfo> rooms = getCabinetsByAddressId(addressId);
        for(int i=0;i<rooms.size();i++)
        {
            deleteCabinet(rooms.get(i).id);
        }
    }

}
