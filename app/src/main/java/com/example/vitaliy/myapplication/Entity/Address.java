package com.example.vitaliy.myapplication.Entity;

public class Address implements TableModel{
    public String city, street, building;
    public int id;
    public int objectId;

    public Address(int id, String city, String street, String building)
    {
        this.id = id;
        this.city = city;
        this.street = street;
        this.building = building;
    }

    public Address(int id, String city, String street, String building, int objectId)
    {
        this(id, city, street, building);
        this.objectId = objectId;
    }

    public String getStringFullAddress()
    {
        return getStringValueOfVisibleColumn(0);
    }

    @Override
    public boolean equals(Object obj) {
        return (this.id == ((Address)obj).id);
    }

    @Override
    public int visibleColumnCount() {
        return 1;
    }

    @Override
    public String getStringValueOfVisibleColumn(int column) {
        if(column == 0)
        {
            String result = "";
            if(city != null)
                result+=city + ", ";
            if(street != null)
                result+=street + ", ";
            if(building != null)
                result+=building;
            return result;
        }
        return "";
    }

    @Override
    public int columnCount() {
        return 4;
    }

    @Override
    public String getStringValueOfColumn(int column) {
        switch (column)
        {
            case 0:
                return ""+id;
            case 1:
                return city;
            case 2:
                return street;
            case 3:
                return building;
            default:
                return "";

        }
    }

    @Override
    public String getNameOfVisibleColumn(int column) {
        return "Адрес";
    }

    @Override
    public String getNameOfColumn(int column) {
        switch (column)
        {
            case 0:
                return "id";
            case 1:
                return "city";
            case 2:
                return "street";
            case 3:
                return "building";
            default:
                return "";

        }
    }
}
