package com.example.vitaliy.myapplication.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.provider.Settings;

import com.example.vitaliy.myapplication.Entity.Address;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.ComplexComponent;
import com.example.vitaliy.myapplication.Entity.ComplexInfo;
import com.example.vitaliy.myapplication.Entity.ComplexTypeComponent;
import com.example.vitaliy.myapplication.Entity.ComponentInfo;
import com.example.vitaliy.myapplication.Entity.EquipmentInfo;
import com.example.vitaliy.myapplication.Entity.LastSelection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "EquipmentManagerV3.db";
    private static final int version =13;

    private static final TreeMap<Integer, ArrayList<String>> dbUpgrade;
    static
    {
        dbUpgrade = new TreeMap<Integer, ArrayList<String>>();
        ArrayList<String> upgrades;
        upgrades = new ArrayList<String>();
        upgrades.add("ALTER TABLE addresses ADD COLUMN sync BOOLEAN;");
        upgrades.add("ALTER TABLE complex_types ADD COLUMN sync BOOLEAN;");
        upgrades.add("ALTER TABLE components ADD COLUMN sync BOOLEAN;");
        upgrades.add("ALTER TABLE objects ADD COLUMN sync BOOLEAN;");
        upgrades.add("ALTER TABLE rooms ADD COLUMN sync BOOLEAN;");
        upgrades.add("CREATE TRIGGER update_addresses UPDATE OF city,\n" +
                "street, building, object_id\n" +
                "ON addresses\n" +
                "BEGIN\n" +
                "UPDATE addresses SET sync = 0 WHERE id = old.id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_complex_types UPDATE OF name\n" +
                "ON complex_types\n" +
                "BEGIN\n" +
                "UPDATE complex_types SET sync = 0 WHERE id = old.id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_components UPDATE OF name\n" +
                "ON components\n" +
                "BEGIN\n" +
                "UPDATE components SET sync = 0 WHERE id = old.id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_objects UPDATE OF name\n" +
                "ON objects\n" +
                "BEGIN\n" +
                "UPDATE objects SET sync = 0 WHERE id = old.id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_rooms UPDATE OF name, address_id\n" +
                "ON rooms\n" +
                "BEGIN\n" +
                "UPDATE rooms SET sync = 0 WHERE id = old.id;\n" +
                "END;");
        dbUpgrade.put(9,upgrades);
        upgrades = new ArrayList<String>();

        upgrades.add("CREATE TRIGGER update_complex_types_by_component UPDATE OF component_id\n" +
                "ON complex_type_has_components\n" +
                "BEGIN\n" +
                "UPDATE complex_types SET sync = 0 WHERE id = old.complex_type_id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_complexes_by_component UPDATE OF component_id,\n" +
                "code\n" +
                "ON complex_has_components\n" +
                "BEGIN\n" +
                "UPDATE complexes SET sync = 0 WHERE id = old.complex_id;\n" +
                "END;");
        dbUpgrade.put(10,upgrades);
        upgrades = new ArrayList<String>();

        upgrades.add("CREATE TRIGGER update_complexes_by_component_insert INSERT\n" +
                "ON complex_has_components\n" +
                "BEGIN\n" +
                "UPDATE complexes SET sync = 0 WHERE id = new.complex_id;\n" +
                "END;");
        upgrades.add("CREATE TRIGGER update_complex_types_by_component_insert INSERT\n" +
                "ON complex_type_has_components\n" +
                "BEGIN\n" +
                "UPDATE complex_types SET sync = 0 WHERE id = new.complex_type_id;\n" +
                "END;");
        dbUpgrade.put(11,upgrades);

        upgrades = new ArrayList<String>();

        upgrades.add("CREATE TABLE filters (\n" +
                "complex_type_id INTEGER,\n" +
                "object_id INTEGER,\n" +
                "address_id INTEGER,\n" +
                "room_id INTEGER,\n" +
                "additional_info TEXT,\n" +
                "ip_address TEXT,\n" +
                "id_by_address TEXT,\n" +
                "with_unset INTEGER)");
        upgrades.add("INSERT INTO filters (complex_type_id, object_id, address_id,\n" +
                "room_id, ip_address, id_by_address, additional_info, with_unset)\n" +
                "VALUES (-1,-1,-1,-1,'','','',-1);");
        dbUpgrade.put(12,upgrades);

        upgrades = new ArrayList<String>();

        upgrades.add("ALTER TABLE settings ADD COLUMN page_size INTEGER;");
        upgrades.add("UPDATE settings SET page_size=10;");
        dbUpgrade.put(13,upgrades);

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("onCreate DB version: "+db.getVersion());
        String sql = "\n" +
                "CREATE TABLE IF NOT EXISTS `objects` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`name`\tTEXT,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `addresses` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`city`\tTEXT,\n" +
                "\t`street`\tTEXT,\n" +
                "\t`building`\tTEXT,\n" +
                "\t`object_id`\tINTEGER,\n" +
                "\tFOREIGN KEY(`object_id`) REFERENCES `objects`(`id`),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `rooms` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`name`\tTEXT,\n" +
                "\t`address_id`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`),\n" +
                "\tFOREIGN KEY(`address_id`) REFERENCES `addresses`(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `components` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`name`\tTEXT,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `complex_types` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`name`\tTEXT,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `complex_type_has_components` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`complex_type_id`\tINTEGER,\n" +
                "\t`component_id`\tINTEGER,\n" +
                "\tFOREIGN KEY(`complex_type_id`) REFERENCES `complex_types`(`id`),\n" +
                "\tFOREIGN KEY(`component_id`) REFERENCES `components`(`id`),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `last_selection` (\n" +
                "\t`complex_type_id`\tINTEGER,\n" +
                "\t`object_id`\tINTEGER,\n" +
                "\t`address_id`\tINTEGER,\n" +
                "\t`room_id`\tINTEGER,\n" +
                "\tFOREIGN KEY(`object_id`) REFERENCES `objects`(`id`),\n" +
                "\tFOREIGN KEY(`address_id`) REFERENCES `addresses`(`id`),\n" +
                "\tFOREIGN KEY(`room_id`) REFERENCES `rooms`(`id`),\n" +
                "\tFOREIGN KEY(`complex_type_id`) REFERENCES `complex_types`(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `complexes` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`address_id`\tINTEGER,\n" +
                "\t`room_id`\tINTEGER,\n" +
                "\t`complex_type_id`\tINTEGER,\n" +
                "\t`id_by_address`\tTEXT,\n" +
                "\t`appendix`\tTEXT,\n" +
                "\t`IPADDRESS`\tTEXT,\n" +
                "\t`photo_path`\tTEXT,\n" +
                "\t`sync`\tBOOLEAN,\n" +
                "\tFOREIGN KEY(`room_id`) REFERENCES `rooms`(`id`),\n" +
                "\tFOREIGN KEY(`complex_type_id`) REFERENCES `complex_types`(`id`),\n" +
                "\tPRIMARY KEY(`id`),\n" +
                "\tFOREIGN KEY(`address_id`) REFERENCES `addresses`(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `complex_has_components` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`complex_id`\tINTEGER,\n" +
                "\t`component_id`\tINTEGER,\n" +
                "\t`code`\tTEXT,\n" +
                "\tFOREIGN KEY(`complex_id`) REFERENCES `complexes`(`id`),\n" +
                "\tFOREIGN KEY(`component_id`) REFERENCES `components`(`id`),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS `settings` (\n" +
                "\t`type`\tBOOLEAN,\n" +
                "\t`object`\tBOOLEAN,\n" +
                "\t`address`\tBOOLEAN,\n" +
                "\t`room`\tBOOLEAN,\n" +
                "\t`additional_info`\tBOOLEAN,\n" +
                "\t`ip_address`\tBOOLEAN,\n" +
                "\t`id_by_address`\tBOOLEAN,\n" +
                "\t`light_unset`\tBOOLEAN,\n" +
                "\t`server_ip`\tTEXT\n" +
                ");";

        db.execSQL(sql);

        sql = "INSERT INTO `settings` VALUES (1,1,1,1,1,1,1,1,'');";

        db.execSQL(sql);

        sql = "INSERT INTO `last_selection` VALUES (0,0,0,0);";

        db.execSQL(sql);

        sql = "CREATE TRIGGER update_complexes UPDATE OF address_id,\n" +
                "room_id, complex_type_id, id_by_address,\n" +
                "appendix, IPADDRESS, photo_path\n" +
                "ON complexes\n" +
                "BEGIN\n" +
                "UPDATE complexes SET sync = 0 WHERE id = old.id;\n" +
                "END;";

        db.execSQL(sql);

        for(TreeMap.Entry<Integer, ArrayList<String>> cursor : dbUpgrade.entrySet())
        {
            ArrayList<String> requests = cursor.getValue();
            for(String item : requests)
            {
                db.execSQL(item);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("DB version is " + db.getVersion());
        for(TreeMap.Entry<Integer, ArrayList<String>> cursor : dbUpgrade.entrySet())
        {
            if(cursor.getKey() <= db.getVersion())
                continue;
            System.out.println("upgrade to "+cursor.getKey()+" version");
            ArrayList<String> requests = cursor.getValue();
            for(String item : requests)
            {
                db.execSQL(item);
            }
        }
//        String sql = "ALTER TABLE complexes ADD COLUMN sync BOOLEAN;";
//        db.execSQL(sql);
//
//        sql = "CREATE TRIGGER update_complexes UPDATE OF address_id,\n" +
//                "room_id, complex_type_id, id_by_address,\n" +
//                "appendix, IPADDRESS, photo_path\n" +
//                "ON complexes\n" +
//                "BEGIN\n" +
//                "UPDATE complexes SET sync = 0 WHERE id = old.id;\n" +
//                "END;";
//        db.execSQL(sql);
    }

    public void execute(String sql)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public Cursor getRows(String sql)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( sql, null );
    }
}
