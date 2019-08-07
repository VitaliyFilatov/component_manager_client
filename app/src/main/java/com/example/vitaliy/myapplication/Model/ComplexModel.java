package com.example.vitaliy.myapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.vitaliy.myapplication.Entity.ComplexComponent;
import com.example.vitaliy.myapplication.Entity.ComplexInfo;
import com.example.vitaliy.myapplication.Entity.ComplexInfoServer;

import java.util.ArrayList;

public class ComplexModel extends BaseModel {

    public int pageSize;

    private int currentPage;

    private static String requestForComplexInfo = "SELECT DISTINCT c.id,\n" +
            "c.appendix, c.IPADDRESS, c.id_by_address, c.photo_path,\n" +
            "ct.name AS complex_type_name,\n" +
            "ct.id AS complex_type_id,\n" +
            "o.name AS object_name, o.id AS object_id,\n" +
            "a.id AS address_id, a.city, a.street,\n" +
            "a.building, r.name AS room_name, r.id AS room_id,\n" +
            "sub.have_unset\n" +
            "FROM complexes AS c LEFT JOIN\n" +
            "addresses AS a ON c.address_id=a.id\n" +
            "LEFT JOIN objects AS o ON o.id=a.object_id\n" +
            "LEFT JOIN complex_types AS ct ON c.complex_type_id=ct.id\n" +
            "LEFT JOIN rooms AS r ON r.id=c.room_id\n" +
            "LEFT JOIN (SELECT DISTINCT subc.id, 'true' as have_unset FROM complexes AS subc\n" +
            "LEFT JOIN complex_has_components AS subchc\n" +
            "ON subc.id=subchc.complex_id\n" +
            "WHERE subchc.id is not NULL\n" +
            "AND (code is NULL OR code = '')) AS sub\n" +
            "ON sub.id=c.id";


    public ComplexModel(Context context, int pageSize) {
        super(context);
        tableName = "complexes";
        this.pageSize = pageSize;
        currentPage = 0;
        isSyncronizable = true;
    }

    public ComplexModel(Context context) {
        this(context, 10);
    }


    public void resetModel()
    {
        currentPage = 0;
    }

    public int bindComplexTypeToComplex(int complex_type_id, int complex_id) {
        String sql;
        Integer complexTypeId = null;
        if (complex_type_id > 0) {
            complexTypeId = complex_type_id;
        }
        if (complex_id > -1) {
            sql = "UPDATE complexes SET" +
                    " complex_type_id=" + complexTypeId +
                    " WHERE id=" + complex_id + ";";
        } else {
            sql = "INSERT INTO complexes (complex_type_id)" +
                    " VALUES (" + complexTypeId + ");";
        }
        db.execute(sql);
        if (complex_id > -1)
            return complex_id;
        sql = "SELECT id FROM complexes" +
                " WHERE id=(SELECT MAX(id) FROM complexes);";
        Cursor res = db.getRows(sql);
        res.moveToFirst();
        if (res.isAfterLast() == false) {
            return res.getInt(res.getColumnIndex("id"));
        }
        return -1;
    }

    private ComplexInfo getComplexInfoFromCursor(Cursor res)
    {
        ComplexInfo complexInfo = new ComplexInfo();
        complexInfo.setId(res.getInt(res.getColumnIndex("id")));
        complexInfo.setIdByAddress(res.getString(res.getColumnIndex("id_by_address")));
        complexInfo.setComplexType(res.getInt(res.getColumnIndex("complex_type_id")),
                res.getString(res.getColumnIndex("complex_type_name")));
        complexInfo.setObject(res.getInt(res.getColumnIndex("object_id")),
                res.getString(res.getColumnIndex("object_name")));
        complexInfo.setAddress(res.getInt(res.getColumnIndex("address_id")),
                res.getString(res.getColumnIndex("city")),
                res.getString(res.getColumnIndex("street")),
                res.getString(res.getColumnIndex("building")));
        complexInfo.setRoom(res.getInt(res.getColumnIndex("room_id")),
                res.getString(res.getColumnIndex("room_name")));
        complexInfo.setAppendix(res.getString(res.getColumnIndex("appendix")));
        complexInfo.setIpAddress(res.getString(res.getColumnIndex("IPADDRESS")));
        complexInfo.setPhotoPath(res.getString(res.getColumnIndex("photo_path")));
        String haveUnset = res.getString(res.getColumnIndex("have_unset"));
        complexInfo.setHaveUnsetComponents(false);
        if(haveUnset != null)
        {
            if(!haveUnset.equals(""))
            {
                complexInfo.setHaveUnsetComponents(true);
            }
        }
        return complexInfo;
    }

    public ComplexInfo getComplex(int id)
    {
        String sql = ComplexModel.requestForComplexInfo + " WHERE c.id="+id+";";

        Cursor res =  db.getRows( sql );
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return getComplexInfoFromCursor(res);
        }

        return null;
    }

    public int getMaxIdByAddress(int address_id)
    {
        Integer addressId = null;
        if (address_id > 0) {
            addressId = address_id;
        }
        String sql = "SELECT MAX(id_by_address) AS last_id FROM complexes WHERE address_id=" +
                addressId+";";
        Cursor res =  db.getRows(sql);
        res.moveToFirst();
        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("last_id"));
        }
        return 0;
    }


    public int isSync(int id)
    {
        String sql = "SELECT sync FROM complexes WHERE id="+id;
        Cursor res =  db.getRows(sql);
        res.moveToFirst();
        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("sync"));
        }
        return 0;
    }


    public void saveComplexInfo(int complex_id,
                                int address_id,
                                int room_id,
                                String appendix,
                                String ipAddress,
                                String idByAddress) {
        Integer addressId = null;
        Integer roomId = null;
        if (address_id > 0) {
            addressId = address_id;
        }
        if (room_id > 0) {
            roomId = room_id;
        }
        String sql = "UPDATE complexes SET" +
                " address_id=" + addressId + ", room_id=" + roomId +
                ", id_by_address='"+idByAddress+"', appendix='"+appendix+"', IPADDRESS='"+ipAddress +
                "' WHERE id=" + complex_id + ";";
        System.out.println("sql: "+sql);
        db.execute(sql);
//        int max_id = getMaxIdByAddress(address_id) + 1;
//        sql = "UPDATE complexes SET" +
//                " id_by_address=" + max_id +
//                " WHERE id=" + complex_id + ";";
//        System.out.println("sql: "+sql);
//        db.execute(sql);
    }

    public void saveComplexPhoto(String photoPath, int complex_id)
    {
        String sql = "UPDATE complexes SET" +
                " photo_path='" + photoPath +
                "' WHERE id=" + complex_id + ";";
        db.execute(sql);
    }

    public void updateComponentsFromComplexType(int complex_id) {
        String sql = "DELETE FROM complex_has_components" +
                " WHERE complex_id=" + complex_id + ";";
        db.execute(sql);

        sql = "INSERT INTO complex_has_components (complex_id, component_id)" +
                " SELECT complexes.id, complex_type_has_components.component_id" +
                " FROM complexes JOIN complex_types" +
                " ON complexes.complex_type_id=complex_types.id" +
                " JOIN complex_type_has_components" +
                " ON complex_type_has_components.complex_type_id=complex_types.id" +
                " WHERE complexes.id=" + complex_id + ";";
        db.execute(sql);
    }

    public int getComplexTypeIdOfComplex(int complex_id)
    {
        Cursor res =  db.getRows( "SELECT complex_type_id" +
                " FROM complexes WHERE id="+complex_id+";");
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("complex_type_id"));
        }
        return -1;
    }

    public int getObjectIdOfComplex(int complex_id)
    {

        Cursor res =  db.getRows( "SELECT objects.id" +
                " FROM complexes JOIN addresses ON complexes.address_id=addresses.id" +
                " JOIN objects ON addresses.object_id=objects.id" +
                " WHERE complexes.id=" + complex_id);
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("id"));
        }
        return -1;
    }

    public ArrayList<ComplexComponent> getComponentOfComplex(int complex_id)
    {
        ArrayList<ComplexComponent> array_list = new ArrayList<ComplexComponent>();

        Cursor res =  db.getRows( "SELECT complex_has_components.id as id," +
                " components.id as component_id, components.name," +
                " complex_has_components.code" +
                " FROM complex_has_components JOIN components" +
                " ON complex_has_components.component_id=components.id" +
                " WHERE complex_has_components.complex_id="+complex_id+";");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String code = res.getString(res.getColumnIndex("code"));
            if(code == null)
                code = "";
            array_list.add(new ComplexComponent(res.getInt(res.getColumnIndex("id")),
                    res.getInt(res.getColumnIndex("component_id")),
                    res.getString(res.getColumnIndex("name")),
                    code));
            res.moveToNext();
        }
        return array_list;
    }

    public void addComponentsToComplex(int complex_id, ArrayList<ComplexComponent> components)
    {
        String sql = "DELETE FROM complex_has_components" +
                " WHERE complex_id="+complex_id+";";
        db.execute(sql);
        if(components.size() == 0)
            return;

        sql = "INSERT INTO complex_has_components" +
                " (complex_id, component_id, code) VALUES";
        for(int i=0;i<components.size();i++)
        {
            sql+=" ("+complex_id+", "+components.get(i).component_id+", '"+components.get(i).code+"')";
            if(i < components.size() - 1)
            {
                sql+=",";
            }
        }
        db.execute(sql);
    }

    public int getAddressIdOfComplex(int complex_id)
    {

        Cursor res =  db.getRows( "SELECT address_id FROM complexes" +
                " WHERE id=" + complex_id+";");
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("address_id"));
        }
        return -1;
    }

    public int getRoomIdOfComplex(int complex_id)
    {

        Cursor res =  db.getRows( "SELECT room_id" +
                " FROM complexes" +
                " WHERE complexes.id=" + complex_id);
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("room_id"));
        }
        return -1;
    }

    public ArrayList<ComplexInfo> getAllComplexesByCondition(int complexTypeId,
                                                             int objectId,
                                                             int addressId,
                                                             int roomId,
                                                             int withUnset,
                                                             String additionalInfo,
                                                             String ipAddress,
                                                             String idByAddress)
    {
        ArrayList<ComplexInfo> array_list = new ArrayList<ComplexInfo>();
        String sql = ComplexModel.requestForComplexInfo;
        String andOrWhere = "WHERE";
        if(complexTypeId >= 0)
        {
            if(complexTypeId == 0)
            {
                sql+=" WHERE ct.id IS NULL";
            }
            else
            {
                sql+=" WHERE ct.id = " + complexTypeId;
            }
            andOrWhere = "AND";
        }
        if(objectId >= 0)
        {
            sql+=" "+andOrWhere;
            if(objectId == 0)
            {
                sql+=" o.id IS NULL";
            }
            else
            {
                sql+=" o.id = " + objectId;
            }
            andOrWhere = "AND";
        }
        if(addressId >= 0)
        {
            sql+=" "+andOrWhere;
            if(addressId == 0)
            {
                sql+=" a.id IS NULL";
            }
            else
            {
                sql+=" a.id = " + addressId;
            }
            andOrWhere = "AND";
        }
        if(roomId > 0)
        {
            sql+=" "+andOrWhere;
            if(roomId == 0)
            {
                sql+=" r.id IS NULL";
            }
            else
            {
                sql+=" r.id = " + roomId;
            }
            andOrWhere = "AND";
        }
        if(additionalInfo != null)
        {
            if(!additionalInfo.equals(""))
            {
                sql+=" "+andOrWhere;
                sql += " c.appendix = '" + additionalInfo + "'";
                andOrWhere = "AND";
            }
        }
        if(ipAddress != null)
        {
            if(!ipAddress.equals(""))
            {
                sql+=" "+andOrWhere;
                sql += " c.IPADDRESS = '" + ipAddress + "'";
                andOrWhere = "AND";
            }
        }
        if(idByAddress != null)
        {
            if(!idByAddress.equals(""))
            {
                sql+=" "+andOrWhere;
                sql += " c.id_by_address='" + idByAddress + "'";
                andOrWhere = "AND";
            }
        }
        if(withUnset >= 0)
        {
            String not = "";
            if(withUnset == 1)
            {
                not = "not ";
            }
            sql+=" "+andOrWhere;
            sql += " sub.have_unset is " + not + "NULL";
        }

        sql += " ORDER BY c.id DESC";
        sql += " LIMIT " + currentPage*pageSize + ", " + pageSize;
        currentPage++;

        Cursor res =  db.getRows( sql );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(getComplexInfoFromCursor(res));
            res.moveToNext();
        }

        return array_list;
    }

    public ArrayList<ComplexInfo> getAllComplexesByComponentCode(String code) {
        ArrayList<ComplexInfo> array_list = new ArrayList<ComplexInfo>();

        String sql = ComplexModel.requestForComplexInfo+
                " LEFT JOIN complex_has_components AS chc ON c.id=chc.complex_id" +
                " WHERE chc.code = '" + code + "';";


        sql += " ORDER BY c.id DESC";
        sql += " LIMIT " + currentPage*pageSize + ", " + pageSize;
        currentPage++;

        Cursor res = db.getRows(sql);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(getComplexInfoFromCursor(res));
            res.moveToNext();
        }

        return array_list;
    }

    public ArrayList<ComplexInfo> getAllComplexes()
    {
        ArrayList<ComplexInfo> array_list = new ArrayList<ComplexInfo>();

        String request = ComplexModel.requestForComplexInfo;
        request += " ORDER BY c.id DESC";
        request += " LIMIT " + currentPage*pageSize + ", " + pageSize;
        currentPage++;
        Cursor res =  db.getRows(request);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(getComplexInfoFromCursor(res));
            res.moveToNext();
        }

        return array_list;
    }

    public ArrayList<ComplexInfoServer> getAllComplexesForServer()
    {
        ArrayList<ComplexInfoServer> array_list = new ArrayList<ComplexInfoServer>();

        Cursor res =  db.getRows("SELECT * FROM complexes WHERE sync=0" +
                " OR sync IS NULL");
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ComplexInfoServer info = new ComplexInfoServer();
            info.complexTypeId = res.getInt(res.getColumnIndex("complex_type_id"));
            info.id = res.getInt(res.getColumnIndex("id"));
            info.addressId = res.getInt(res.getColumnIndex("address_id"));
            info.roomId = res.getInt(res.getColumnIndex("room_id"));
            info.appendix = res.getString(res.getColumnIndex("appendix"));
            info.id_by_address = res.getString(res.getColumnIndex("id_by_address"));
            info.ipAddress = res.getString(res.getColumnIndex("IPADDRESS"));
            info.photoPath = res.getString(res.getColumnIndex("photo_path"));
            System.out.println("add to list");
            array_list.add(info);
            res.moveToNext();
        }

        System.out.println("array size: "+array_list.size());
        return array_list;
    }

    public void deleteComplex(int complexId)
    {
        ComplexHasComponentModel complexHasComponentModel = new ComplexHasComponentModel(getContext());
        complexHasComponentModel.deleteComplexComponents(complexId);
        deleteItem(complexId);
    }

    public void unsetAddress(int addressId)
    {
        String sql = "UPDATE "+tableName+" SET address_id=null" +
                " WHERE address_id="+addressId;
        db.execute(sql);
    }

    public void unsetRoom(int roomId)
    {
        String sql = "UPDATE "+tableName+" SET room_id=null" +
                " WHERE room_id="+roomId;
        db.execute(sql);
    }

    public int getCountWithPhoto()
    {
        Cursor res =  db.getRows( "SELECT count(id) AS cnt FROM complexes WHERE" +
                " photo_path IS NOT NULL AND (sync=0 OR sync IS NULL);");
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex("cnt"));
        }
        return 0;
    }
}
