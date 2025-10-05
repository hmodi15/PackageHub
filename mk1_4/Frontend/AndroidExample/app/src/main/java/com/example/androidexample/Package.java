package com.example.androidexample;

import java.io.Serializable;

/**
 * Represents a package with an occupant name, delivery date, security code, and ID.
 */
public class Package implements Serializable {
    private String occupantName;
    private String deliveryDate;
    private String securityCode;
    private int id;
    private boolean pickUpStatus;

    /**
     * Constructs a new Package with the given ID, occupant name, delivery date, and security code.
     *
     * @param id The ID of the package.
     * @param occupantName The name of the occupant.
     * @param deliveryDate The delivery date of the package.
     * @param securityCode The security code of the package.
     */
    public Package(int id, String occupantName, String deliveryDate, String securityCode, boolean pickUpStatus) {
        this.occupantName = occupantName;
        this.deliveryDate = deliveryDate;
        this.securityCode = securityCode;
        this.id = id;
        this.pickUpStatus = pickUpStatus;
    }

    /**
     * Returns the occupant's name.
     *
     * @return A string representing the occupant's name.
     */
    public String getOccupantName() {
        return occupantName;
    }

    /**
     * Sets the occupant's name.
     *
     * @param occupantName A string containing the occupant's name.
     */
    public void setOccupantName(String occupantName) {
        this.occupantName = occupantName;
    }

    /**
     * Returns the delivery date.
     *
     * @return A string representing the delivery date.
     */
    public String getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Sets the delivery date.
     *
     * @param deliveryDate A string containing the delivery date.
     */
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Returns the security code.
     *
     * @return A string representing the security code.
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * Sets the security code.
     *
     * @param securityCode A string containing the security code.
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    /**
     * Returns the ID of the package.
     *
     * @return An integer representing the ID of the package.
     */
    public int getId(){return id; }

    public boolean getPickUpStatus(){
        return this.pickUpStatus;
    }

    public void setPickUpStatus(boolean status){
        this.pickUpStatus = status;
    }
}
