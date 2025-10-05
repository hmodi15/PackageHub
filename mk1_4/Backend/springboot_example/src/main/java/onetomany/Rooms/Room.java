package onetomany.Rooms;

import jakarta.persistence.*;
import onetomany.Buildings.BuildingRepository;
import onetomany.Buildings.Buildings;
import onetomany.Packages.Package;
import onetomany.Users.User;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Benjamin Bartels
 */
@Entity
public class Room {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String aptNum;
    private String address;
    private String buildingName;
    private int maxTenants;


    //the list of users of a room
    @OneToMany(mappedBy = "room")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "building_id", updatable = true)
    @JsonIgnore
    private Buildings building;


    // =============================== Constructors ================================== //

    public Room(){this.users = new ArrayList<>();
    }

    public Room(String aptNum, String address, String buildingName, int maxTenants){
        this.aptNum = aptNum;
        this.address = address;
        this.buildingName = buildingName;
        this.maxTenants = maxTenants;
        users = new ArrayList<>();
        //building = BuildingRepository.findByName(buildingName);
    }

    public Room( String aptNum, String address, Buildings building, int maxTenants){
        this.aptNum = aptNum;
        this.address = address;
        this.building = building;
        this.buildingName = building.getName();
        this.maxTenants = maxTenants;
        users = new ArrayList<User>();
        //building = BuildingRepository.findByName(buildingName);
    }



    // =============================== Getters and Setters for each field ================================== //


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAptNum() {
        return aptNum;
    }

    public void setAptNum(String aptNum) {
        this.aptNum = aptNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Buildings getBuilding() {
        return building;
    }

    public void setBuilding(Buildings building) {
        this.building = building;
        this.buildingName = building.getName();
    }

    public int getMaxTenants() {
        return maxTenants;
    }

    public void setMaxTenants(int maxTenants) {
        this.maxTenants = maxTenants;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user){
        this.users.add(user);
    }
}
