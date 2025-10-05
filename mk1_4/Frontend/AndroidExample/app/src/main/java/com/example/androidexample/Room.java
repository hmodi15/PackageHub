package com.example.androidexample;

public class Room {
    private int id;
    private String aptNum;
    private String address;
    private String buildingName;
    private int maxTenants;

    public Room(int id, String apartmentNumber, String address, String buildingName, int maxTenants) {
        this.id = id;
        this.aptNum = apartmentNumber;
        this.address = address;
        this.buildingName = buildingName;
        this.maxTenants = maxTenants;
    }
    //getters and setters
    public int getId(){
        return id;
    }
    public String getApartmentNumber(){
        return aptNum;
    }
    public void setApartmentNumber(String apartmentNumber){
        this.aptNum = apartmentNumber;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getBuildingName(){
        return buildingName;
    }
    public void setBuildingName(String buildingName){
        this.buildingName = buildingName;
    }
    public int getMaxTenants(){
        return maxTenants;
    }
    public void setMaxTenants(int maxTenants){
        this.maxTenants = maxTenants;
    }
}
