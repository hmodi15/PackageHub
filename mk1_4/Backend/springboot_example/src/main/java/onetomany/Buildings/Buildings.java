package onetomany.Buildings;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import onetomany.Packages.Package;
import onetomany.Rooms.Room;

@Entity
public class Buildings {

    /*
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    // @Column(name = "is_active")
    private boolean isActive = true;
    // @Column(name = "if_active")
    // private boolean ifActive = true;
    @OneToMany(mappedBy = "building")
    private List<Room> rooms;

    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User), the cascade option tells springboot
     * to create the child entity if not present already (in this case it is laptop)
     * @JoinColumn specifies the ownership of the key i.e. The User table will contain a foreign key from the laptop table and the column name will be laptop_id
     */


    /*
     * @OneToMany tells springboot that one instance of User can map to multiple instances of Phone OR one user row can map to multiple rows of the phone table
     */

    // =============================== Constructors ================================== //

    public Buildings() {
        this.rooms = new ArrayList<>();
    }

    public Buildings(String name, int id) {
        this.name = name;
        this.id = id;
        this.isActive = true;
        this.rooms = new ArrayList<>();
    }



    // =============================== Getters and Setters for each field ================================== //


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
}
