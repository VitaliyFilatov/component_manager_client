package com.example.vitaliy.myapplication.Entity;

public class CommonInfo implements TableModel{
    public int id;
    public String name;

    public CommonInfo(int id, String name)
    {
        this.id = id;
        this.name =name;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.id == ((CommonInfo)obj).id);
    }


    @Override
    public int visibleColumnCount() {
        return 1;
    }

    @Override
    public String getStringValueOfVisibleColumn(int column) {
        if(column == 0)
            return name;
        return "";
    }

    @Override
    public int columnCount() {
        return 2;
    }

    @Override
    public String getStringValueOfColumn(int column) {
        switch(column)
        {
            case 0:
            {
                return ""+id;
            }
            case 1:
            {
                return name;
            }
            default:
            {
                return "";
            }
        }
    }

    @Override
    public String getNameOfVisibleColumn(int column) {
        if(column == 0)
            return "Имя";
        return "";
    }

    @Override
    public String getNameOfColumn(int column) {
        switch(column)
        {
            case 0:
            {
                return "id";
            }
            case 1:
            {
                return "name";
            }
            default:
            {
                return "";
            }
        }
    }
}
