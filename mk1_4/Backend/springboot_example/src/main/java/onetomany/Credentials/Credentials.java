package onetomany.Credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Entity
public class Credentials {
    @Id
    private String emailId;
    private String passwordId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", updatable = true)
    @JsonIgnore
    private User user;


    // =============================== Constructors ================================== //


    public Credentials(String emailId, String passwordID) {
        this.setEmailId(emailId);
        this.setPasswordId(passwordID);
        this.setUser(user);
    }

    public Credentials() {
    }


    // =============================== Getters and Setters for each field ================================== //


    public String getEmailId(){
        return emailId;
    }

    public void setEmailId(String emailId){
        this.emailId = emailId;
    }

    public String getName() {
        if (user != null && user.getName() != null) {
            return user.getName();
        } else {
            return "";
        }
    }

    public void setName(String name) {
        if (user != null) {
            user.setName(name);
        }
    }

    public String getPasswordId(){
        return passwordId;
    }

    public void setPasswordId(String passwordId){
        this.passwordId = passwordId;
    }

    public void setCredentialsEmail(String email) {
        user.setEmailId(email);
    }

    public void setCredentialsPassword(String password) {
        user.setPasswordId(password);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    // =============================== Special Functions ================================== //

    /**
     * SHA-256 Encryption code to encrypt passwords for security reasons
     * @param password
     * @return hexString
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null; // Handle error appropriately
        }
    }
}
