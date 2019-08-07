package com.example.vitaliy.myapplication.Entity;

public interface TableModel {

    int visibleColumnCount();
    String getStringValueOfVisibleColumn(int column);
    int columnCount();
    String getStringValueOfColumn(int column);
    String getNameOfVisibleColumn(int column);
    String getNameOfColumn(int column);
}
