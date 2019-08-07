package com.example.vitaliy.myapplication.Entity;

public class ComplexInfo {
    public int id;
    public CommonInfo complex_type, object, room;
    public Address address;
    public String appendix, ipAddress, photoPath, id_by_address;
    public boolean haveUnsetComponents;

    public ComplexInfo()
    {
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setAppendix(String appendix)
    {
        this.appendix = appendix;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setPhotoPath(String photoPath)
    {
        this.photoPath = photoPath;
    }

    public void setIdByAddress(String id_by_address)
    {
        this.id_by_address = id_by_address;
    }

    public void setComplexType(int id, String name)
    {
        this.complex_type = new CommonInfo(id, name);
    }

    public void setHaveUnsetComponents(boolean haveUnsetComponents)
    {
        this.haveUnsetComponents = haveUnsetComponents;
    }

    public void setObject(int id, String name)
    {
        this.object = new CommonInfo(id, name);
    }

    public void setRoom(int id, String name)
    {
        this.room = new CommonInfo(id, name);
    }

    public void setAddress(int id, String city, String street, String building)
    {
        this.address = new Address(id, city, street, building);
    }
}
