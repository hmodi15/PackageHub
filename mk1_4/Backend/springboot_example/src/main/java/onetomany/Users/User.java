package onetomany.Users;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import onetomany.Credentials.Credentials;
import onetomany.Feedback.FeedbackEntity;
import onetomany.Packages.Package;
import onetomany.Rooms.Room;

import javax.print.attribute.standard.DateTimeAtCreation;

/***
 * Users class that contains information about the user in each column of its table
 */

/**
 * @author Harsh Modi
 */
@Entity
@JsonIgnoreProperties("credentials") // Ignore the 'credentials' field during JSON serialization
public class User {

    /*
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Date joiningDate;
    private boolean isActive = true;
    private String address;
    private String phone;
    private String postCode;
    private String aptNum;

    @Column
    private Boolean is_manager = false;

    @Column
    private Boolean is_admin = false;

     //@OneToMany tells springboot that one instance of User can map to multiple
     //instances of Package OR one user row can map to multiple rows of the package table
     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     private List<Package> packages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "room_id", updatable = true)
    @JsonIgnore
    private Room room;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn
    private Credentials credentials = new Credentials("0" , "0");

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FeedbackEntity> ratings = new ArrayList<>();


    // =============================== Constructors ================================== //


    public User(String name, String emailId, String passwordId, String address, String phone, String postCode) {
        setId(id);
        setName(name);
        setJoiningDate(new Date());
        setActive(true);
        setAddress(address);
        setPhone(phone);
        setPostCode(postCode);
        packages = new ArrayList<>();
        setEmailId(emailId);
        setPasswordId(passwordId);
        credentials.setUser(this);
        this.is_admin = false;
        this.is_manager = false;
        ratings = new ArrayList<FeedbackEntity>();
    }

    public User() {
        setId(id);
        setName("");
        setAddress("");
        setPhone("");
        setPostCode("");
        setJoiningDate(new Date());
        packages = new ArrayList<Package>();
        credentials = new Credentials("","");
        credentials.setUser(this);
        this.is_admin = false;
        this.is_manager = false;
        ratings = new ArrayList<FeedbackEntity>();
    }


    // =============================== Getters and Setters for each field ================================== //


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        if(name != null) {
            return name;
        } else {
            return "";
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getJoiningDate(){
        return joiningDate;
    }

    public void setJoiningDate(Date joiningDate){
        this.joiningDate = joiningDate;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void setActive(boolean active){
        this.isActive = active;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public List<Package> getAwaitingPackages(){
        List<Package> usersPackages= packages;
        usersPackages.removeIf(pkg -> pkg.getPickUpStatus() == true);
        return usersPackages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public void addPackages(Package pkg){
        this.packages.add(pkg);
    }

    public Room getRoom(){
        return room;
    }

    public void setRoom(Room room){
        this.room = room;
    }

    public String getEmailId() {
        if (this.credentials != null) {
            return this.credentials.getEmailId();
        } else {
            return null; // Or handle the null case accordingly
        }
    }

    public void setEmailId(String emailId) {
        this.credentials.setEmailId(emailId);
    }

    public String getPasswordId() {
        if (this.credentials != null) {
            return this.credentials.getPasswordId();
        } else {
            return null; // Or handle the null case accordingly
        }
    }

    public void setPasswordId(String passwordId) {
        this.credentials.setPasswordId(passwordId);
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public void setCredentials(Credentials cred){
        this.credentials = cred;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPostCode() {
        return this.postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsManager() {
        return is_manager;
    }

    public void setIsManager(Boolean b) {
        this.is_manager = b;
    }

    public Boolean getIsAdmin() {
        return is_admin;
    }

    public void setIsAdmin(Boolean b) {
        this.is_admin = b;
    }

    public String getAptNum() {
        return aptNum;
    }

    public void setAptNum(String aptNum) {
        this.aptNum = aptNum;
    }


    // =============================== Special Functions ================================== //

}
