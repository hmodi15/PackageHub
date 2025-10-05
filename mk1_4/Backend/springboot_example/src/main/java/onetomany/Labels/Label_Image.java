package onetomany.Labels;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import javax.sql.rowset.serial.SerialBlob;

@Entity
public class Label_Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // GenerationType.IDENTITY
    private int id;
    private String label_path;
    private String labelName;
    private SerialBlob imageBlob;

    // =============================== Constructors ================================== //

    public Label_Image(){}

    public Label_Image(String labelName, SerialBlob imageBlob){
        this.labelName = labelName;
        this.imageBlob = imageBlob;
    }



    // =============================== Getters and Setters for each field ================================== //


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getFileName() {
        return labelName;
    }

    public void setFileName(String fileName) {
        labelName = fileName;
    }

    public String getFilePath(){
        return label_path;
    }

    public void setFilePath(String label_path) {
        this.label_path = label_path;
    }

    public SerialBlob getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(SerialBlob imageBlob) {
        this.imageBlob = imageBlob;
    }
}
