package com.example.vitaliy.myapplication.Entity;

public class Filter {
    public int complexTypeId, objectId,
            addressId, roomId, withUnset;
    public String additionalInfo,
            ipAddress, idByAddress;

    public Filter()
    {}

    public Filter(int complexTypeId, int objectId, int addressId, int roomId,
                  int withUnset, String ipAddress, String additionalInfo, String idByAddress)
    {
        this.complexTypeId = complexTypeId;
        this.objectId = objectId;
        this.addressId = addressId;
        this.roomId = roomId;
        this.withUnset = withUnset;
        this.additionalInfo = additionalInfo;
        this.ipAddress = ipAddress;
        this.idByAddress = idByAddress;
    }
}
