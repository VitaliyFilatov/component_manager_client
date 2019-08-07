package com.example.vitaliy.myapplication.Model;


import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.Filter;
import com.example.vitaliy.myapplication.Entity.LastSelection;

public class FilterModel extends BaseModel {

    public FilterModel(Context context)
    {
        super(context);
    }

    public Filter getFilter()
    {
        Cursor res =  db.getRows( "SELECT * FROM filters");
        res.moveToFirst();

        Filter filter = new Filter();
        if(res.isAfterLast() == false){
            filter.additionalInfo = res.getString(res.getColumnIndex("additional_info"));
            filter.addressId = res.getInt(res.getColumnIndex("address_id"));
            filter.complexTypeId = res.getInt(res.getColumnIndex("complex_type_id"));
            filter.idByAddress = res.getString(res.getColumnIndex("id_by_address"));
            filter.ipAddress = res.getString(res.getColumnIndex("ip_address"));
            filter.objectId = res.getInt(res.getColumnIndex("object_id"));
            filter.roomId = res.getInt(res.getColumnIndex("room_id"));
            filter.withUnset = res.getInt(res.getColumnIndex("with_unset"));
            return filter;
        }
        filter.additionalInfo = "";
        filter.addressId = -1;
        filter.complexTypeId = -1;
        filter.idByAddress = "";
        filter.ipAddress = "";
        filter.objectId = -1;
        filter.roomId = -1;
        filter.withUnset = -1;
        return filter;
    }

    public void updateFilter(Filter filter)
    {

        String sql = "UPDATE filters" +
                " SET additional_info='"+filter.additionalInfo+"'," +
                " address_id="+filter.addressId+"," +
                " complex_type_id="+filter.complexTypeId+"," +
                " id_by_address='"+filter.idByAddress+"'," +
                " ip_address='"+filter.ipAddress+"'," +
                " object_id="+filter.objectId+"," +
                " room_id="+filter.roomId+"," +
                " with_unset="+filter.withUnset+";\n";
        db.execute(sql);
    }
}
