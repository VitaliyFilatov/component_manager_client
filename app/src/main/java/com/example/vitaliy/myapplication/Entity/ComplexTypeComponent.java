package com.example.vitaliy.myapplication.Entity;

public class ComplexTypeComponent implements TableModel{
    public int id;
    public int component_id;
    public String name;

    public ComplexTypeComponent(int id, int component_id, String name)
    {
        this.id = id;
        this.component_id = component_id;
        this.name = name;
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
        return 3;
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
                return ""+component_id;
            }
            case 2:
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
                return "component_id";
            }
            case 2:
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
