package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.Address;

import java.util.ArrayList;

public class AddressModel extends BaseModel{
    public AddressModel(Context context)
    {
        super(context);
        tableName = "addresses";
        isSyncronizable = true;
    }

    public ArrayList<Address> getAddressesByObjectId(int id)
    {
        ArrayList<Address> array_list = new ArrayList<Address>();
        Cursor res =  db.getRows( "SELECT id, city, street, building" +
                " FROM addresses WHERE object_id="+id+";");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new Address(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("city")),
                    res.getString(res.getColumnIndex("street")),
                    res.getString(res.getColumnIndex("building"))));
            res.moveToNext();
        }
        return array_list;
    }

    public void deleteAddress(int id)
    {
        RoomModel roomModel = new RoomModel(getContext());
        roomModel.deleteCabinetsByAddress(id);
        ComplexModel complexModel = new ComplexModel(getContext());
        complexModel.unsetAddress(id);
        deleteItem(id);
    }

    public void addNewAddressToObject(int object_id, String city, String street, String building)
    {
        String sql = "INSERT INTO addresses (city, street, building, object_id)" +
                " VALUES ('"+city+"', '"+street+"', '"+building+"', "+object_id+");";
        db.execute(sql);
    }

    public void deleteAddressesByObjectId(int objectId) {
        ArrayList<Address> addresses = getAddressesByObjectId(objectId);
        for (int i = 0; i < addresses.size(); i++) {
            deleteAddress(addresses.get(i).id);
        }
    }

    public ArrayList<Address> getAllAddresses()
    {
        ArrayList<Address> array_list = new ArrayList<Address>();
        Cursor res =  db.getRows( "SELECT id, city, street, building, object_id" +
                " FROM addresses;");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new Address(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("city")),
                    res.getString(res.getColumnIndex("street")),
                    res.getString(res.getColumnIndex("building")),
                    res.getInt(res.getColumnIndex("object_id"))));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Address> getAllAddressesForServer()
    {
        ArrayList<Address> array_list = new ArrayList<Address>();
        Cursor res =  db.getRows( "SELECT id, city, street, building, object_id" +
                " FROM addresses WHERE sync=0 OR sync IS NULL;");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new Address(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("city")),
                    res.getString(res.getColumnIndex("street")),
                    res.getString(res.getColumnIndex("building")),
                    res.getInt(res.getColumnIndex("object_id"))));
            res.moveToNext();
        }
        return array_list;
    }

}
