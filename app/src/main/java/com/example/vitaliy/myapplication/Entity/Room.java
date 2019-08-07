package com.example.vitaliy.myapplication.Entity;

public class Room extends CommonInfo {

    public int addressId;

    public Room(int id, String name, int addressId)
    {
        super(id, name);
        this.addressId = addressId;
    }
}
