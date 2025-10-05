package onetomany.Packages;

import jakarta.persistence.*;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;



import com.fasterxml.jackson.annotation.JsonIgnore;

import onetomany.Users.UserRepository;
import onetomany.Users.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.rowset.serial.SerialBlob;

/**
 * @author Benjamin Bartels
 */
@Entity
public class Package {

    /*
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The recipient's name.
     */
    private String name;

    /**
     * the apartment number the package is going to
     */
    private String apt_num;

    /**
     * the apartment address that the package is going to
     */
    private String address;

    /**
     * the year the package was scanned.
     */
    private int scan_Year;

    /**
     * the month the package was scanned.
     */
    private int scan_Month;

    /**
     * the day of the month the package was scanned.
     */
    private int scan_Date;

    /**
     * the time the package was scanned expressed using a 24-hour clock.
     */
    private String scan_Time;

    /**
     * the date the package was scanned expressed MM/DD/YYYY.
     */
    private String scan_Date_Str;

    /**
     * The status of the package: 1 = picked up, 0 = waiting to be picked up.
     */
    private boolean pickedUp;

    /**
     * The randomly generated pickup code for the package.
     */
    private String pickUpCode;

    /**
     * The tracking number of the package.
     */
    private String trackingNumber;

    private String ocr_feedback;

    private SerialBlob label_img;

    /*
     * @ManyToOne tells springboot that multiple instances of Package can map to one instance of OR multiple rows of the package table can map to one user row
     * @JoinColumn specifies the ownership of the key i.e. The Package table will contain a foreign key from the User table and the column name will be user_id
     * @JsonIgnore is to assure that there is no infinite loop while returning either user/package objects (package->user->[packages]->...)
     */
    /**
     * the user class object ties the specific instance of the package class to its recipient
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", updatable = true)
    @JsonIgnore
    private User user;

    // =============================== Constructors ================================== //

    /**
     * The empty constructor for the package class
     */
    public Package() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        name = "johnny";
        apt_num = "42";
        this.pickedUp = false;
        this.user = null;
        this.pickUpCode = securityCodeGen();
    }

    /**
     * The constructor for the package class that takes in the users name
     */
    public Package(String name) {
        this.name = name;
        Date date = new Date();
        this.pickedUp = false;
        this.pickUpCode = securityCodeGen();
    }

    /**
     * The constructor for the package class that takes in the user's name
     * and the predetermined package pick-up code
     */
    public Package(String name, String pickUpCode, String address){
        Date date = new Date();
        this.name = name;
        this.pickedUp = false;
        this.pickUpCode = pickUpCode;
        setAddress(address);
    }

    /**
     * The constructor for the package class that takes in the user's name,
     * Blob for the label image, feed back from the OCR and the
     * predetermined package pick-up code
     */
    public Package(String name, String pickUpCode, SerialBlob label_img, String ocr_feedback){
        Date date = new Date();
        this.name = name;
        this.pickedUp = false;
        this.pickUpCode = pickUpCode;
        this.label_img = label_img;
        this.ocr_feedback = ocr_feedback;
        this.trackingNumber = "not found";
    }


    public Package(User user, String address, String apt_num, String trackingNumber, SerialBlob label_img, String ocr_feedback){
        Date date = new Date();
        this.user = user;
        this.name = user.getName();
        this.apt_num = apt_num;
        this.address = address;
        this.pickedUp = false;
        this.label_img = label_img;
        this.ocr_feedback = ocr_feedback;
        this.trackingNumber = trackingNumber;
        this.pickUpCode = securityCodeGen();
    }


    // =============================== Getters and Setters for each field ================================== //


    public int getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAptNum(String apt_num) {
        this.apt_num = apt_num;
    }

    public String getAptNum() {
        return apt_num;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean getPickUpStatus() {
        return pickedUp;
    }

    public void changePickUpStatus() {
        pickedUp = !pickedUp;
    }

    public void setPickUpCode(String pickUpCode) {
        this.pickUpCode = pickUpCode;
    }

    public String getPickUpCode() {
        return pickUpCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


// =============================== Scan Time functions ================================== //

    public void setScan_Date_Str(Date scanDate){
        this.scan_Date_Str = scanDate.toString();
        StringBuilder dateSB = new StringBuilder(scan_Date_Str);
        if(dateSB.indexOf("/") >0) {
            this.scan_Year = Integer.parseInt(dateSB.substring(0, (dateSB.indexOf("-"))));
            dateSB = dateSB.delete(0, dateSB.indexOf("-"));
            dateSB.deleteCharAt(0);
            this.scan_Month = Integer.parseInt(dateSB.substring(0, (dateSB.indexOf("-"))));
            dateSB = dateSB.delete(0, dateSB.indexOf("-"));
            dateSB.deleteCharAt(0);
            this.scan_Date = Integer.parseInt(dateSB.substring(0, (dateSB.indexOf(" "))));
            dateSB = dateSB.delete(0, dateSB.indexOf(" "));
            dateSB.deleteCharAt(0);
            this.scan_Time = dateSB.toString();
        }
    }

    public String getScan_Date_Str(){
        if(scan_Date_Str == null){
            return scan_Month + "/" + scan_Date + "/" + scan_Year;
        }
        return scan_Date_Str;
    }

    public int getScanYear() {
        return scan_Year;
    }

    public void setScanYear(Integer scanYear) {
        this.scan_Year = scanYear;
    }

    public int getScanMonth() {
        return scan_Month;
    }

    public void setScanMonth(Integer scanMonth) {
        this.scan_Month = scanMonth;
    }

    public int getScanDate() {
        return scan_Date;
    }

    public void setScanDate(Integer scanDate) {
        this.scan_Date = scanDate;
    }

    public String getScanTime() {
        return scan_Time;
    }

    public void setScanTime(String scanTime) {
        this.scan_Time = scanTime;
    }


    // =============================== Special functions ================================== //


    public String securityCodeGen(){
        Random random = new Random();

        // Generate first 2 random digits
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(random.nextInt(10)); // First digit
        codeBuilder.append(random.nextInt(10)); // Second digit

        // Generate 2 random capitalized letters
        for (int i = 0; i < 2; i++) {
            char randomChar = (char) ('A' + random.nextInt(26));
            codeBuilder.append(randomChar);
        }

        // Generate last random digit
        codeBuilder.append(random.nextInt(10));

        return codeBuilder.toString();
    }

}
