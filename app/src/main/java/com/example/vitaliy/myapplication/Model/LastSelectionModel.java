package com.example.vitaliy.myapplication.Model;


import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.LastSelection;

public class LastSelectionModel extends BaseModel {

    public LastSelectionModel(Context context)
    {
        super(context);
    }

    public LastSelection getLastSelection()
    {
        Cursor res =  db.getRows( "select * from last_selection");
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return new LastSelection(res.getInt(res.getColumnIndex("complex_type_id")),
                    res.getInt(res.getColumnIndex("object_id")),
                    res.getInt(res.getColumnIndex("address_id")),
                    res.getInt(res.getColumnIndex("room_id")));
        }
        return new LastSelection(0,0,0,0);
    }

    public void updateLastSelection(LastSelection lastSelection)
    {
        Integer complexTypeId = null;
        if(lastSelection.complexTypeId > 0)
            complexTypeId = lastSelection.complexTypeId;

        Integer objectId = null;
        if(lastSelection.objectId > 0)
            objectId = lastSelection.objectId;

        Integer addressId = null;
        if(lastSelection.addressId > 0)
            addressId = lastSelection.addressId;

        Integer roomId = null;
        if(lastSelection.roomId > 0)
            roomId = lastSelection.roomId;

        String sql = "UPDATE last_selection" +
                " SET complex_type_id="+complexTypeId+"," +
                " object_id="+objectId+"," +
                " address_id="+addressId+"," +
                " room_id="+roomId+";\n";
        db.execute(sql);
    }
}
