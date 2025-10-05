package com.example.androidexample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String userName;
    private String joiningDate;
    private boolean isActive;
    private String address;
    private String phone;
    private String postCode;
    private String aptNum;
    private List<Package> packages = new ArrayList<>();
    private String emailId;
    private boolean isManager;
    private String passwordId;
    private boolean isAdmin;

    public User(int id, String name, String joiningDate, boolean isActive, String address, String phone, String postCode, String aptNum, List<Package> packages, String emailId, boolean isManager, String passwordId, boolean isAdmin) {
        this.id = id;
        this.userName = name;
        this.joiningDate = joiningDate;
        this.isActive = isActive;
        this.address = address;
        this.phone = phone;
        this.postCode = postCode;
        this.aptNum = aptNum;
        this.packages.clear();
        this.packages.addAll(packages);
        this.emailId = emailId;
        this.isManager = isManager;
        this.passwordId = passwordId;
        this.isAdmin = isAdmin;
    }
    // Getter and Setter methods
    public int getId() {
        return id;
    }

    //Getter and Setter for userName
    public String getUserName(){
        return userName;
    }

    public void setUserName(String name){
        this.userName = name;
    }

    // Getter and Setter for joiningDate
    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    // Getter and Setter for isActive
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Getter and Setter for address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter and Setter for phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter and Setter for postCode
    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    // Getter and Setter for aptNum
    public String getAptNum() {
        return aptNum;
    }

    public void setAptNum(String aptNum) {
        this.aptNum = aptNum;
    }

    // Getter and Setter for packages
    public List<Package> getPackages() {
        return new ArrayList<>(packages);
    }

    public void setPackages(List<Package> packages) {
        this.packages.clear();
        if (packages != null) {
            this.packages.addAll(packages);
        }
    }

    // Getter and Setter for emailId
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    // Getter and Setter for isManager
    public boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    // Getter and Setter for passwordId
    public String getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(String passwordId) {
        this.passwordId = passwordId;
    }

    // Getter and Setter for isAdmin
    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
