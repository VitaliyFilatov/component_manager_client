package com.example.vitaliy.myapplication.Entity;

public class LastSelection {

    public int complexTypeId, objectId, addressId, roomId;

    public LastSelection(int complexTypeId, int objectId, int addressId, int roomId)
    {
        this.complexTypeId = complexTypeId;
        this.objectId = objectId;
        this.addressId = addressId;
        this.roomId = roomId;
    }
}
