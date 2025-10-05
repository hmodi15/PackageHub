package onetomany.Credentials;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import onetomany.Users.User;
import onetomany.Users.UserController;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static onetomany.Credentials.Credentials.hashPassword;

@RestController
public class CredentialsController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CredentialsRepository credentialsRepository;

    public CredentialsController(UserRepository userRepository, CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }


    // =============================== Mappings ================================== //


    @Operation(summary = "Find login information by logging in")
    @GetMapping("/account/info")
    public String getCredentials(@RequestBody Map<String, String> requestBody) {
        String emailId = requestBody.get("emailId");
        String password = hashPassword(requestBody.get("passwordId"));
        if(login(requestBody).equals("success")) {
            return (emailId + " " + password);
        } else {
            return "failure";
        }
    }

    @Operation(summary = "(Admin Only)Find login information of everyone")
    @GetMapping("/accounts")
    public List<Credentials> getCredentialsAll() {
        return credentialsRepository.findAll();
    }

    @Operation(summary = "Log in with an existing account")
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> requestBody) {
        String emailId = requestBody.get("emailId");
        String password = hashPassword(requestBody.get("passwordId"));

        try {
            Credentials userCred = credentialsRepository.findByemailId(emailId);
            if (userCred != null && userCred.getPasswordId().equals(password)) {
                UserController.setChatUser(emailId);
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            // Log the exception or handle it appropriately
            return "error";
        }
    }

    @Operation(summary = "Change the password")
    @PutMapping("/account/edit/{email}")
    public void editCredentials(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String password = hashPassword(requestBody.get("passwordId"));
        Credentials userCred = credentialsRepository.findByemailId(email);
        userCred.setPasswordId(password);
        credentialsRepository.save(userCred);
    }

    @Operation(summary = "Create a new adminstrator")
    @PutMapping("/create/admin")
    public String setAdmin(@RequestBody Map<String, String> requestBody) {
        String emailId = requestBody.get("emailId");
        String password = hashPassword(requestBody.get("passwordId"));
        if(login(requestBody).equals("success")) {
            Credentials userCred = credentialsRepository.findByemailId(emailId);
            User user = userRepository.findBycredentials(userCred);
            user.setIsAdmin(true);
            credentialsRepository.save(userCred);
            return "success";
        } else {
            return "failure";
        }
    }

    @Operation(summary = "Create a new manager")
    @PutMapping("/create/manager")
    public String setManager(@RequestBody Map<String, String> requestBody) {
        String emailId = requestBody.get("emailId");
        String emailId2 = requestBody.get("ManagerEmailId");
        if(login(requestBody).equals("success")) {
            Credentials userCred = credentialsRepository.findByemailId(emailId);
            User user = userRepository.findBycredentials(userCred);
            Credentials userCred2 = credentialsRepository.findByemailId(emailId2);
            User user2 = userRepository.findBycredentials(userCred2);
            if(user.getIsAdmin()) {
                user.setIsManager(true);
                credentialsRepository.save(userCred2);
                return "success";
            }
        } else {
            return "failure";
        }
        return "failure";
    }

    @Operation(summary = "(Only for admins) Delete credentials for anyone selected")
    @DeleteMapping("/account/delete")
    @Transactional
    public String deleteCredentials(@RequestBody Map<String, String> requestBody) {
        String emailToDeleteId = requestBody.get("emailToDeleteId");
        String emailId = requestBody.get("emailId");
        if(login(requestBody).equals("success")) {
            Credentials cred = credentialsRepository.findByemailId(emailId);
            Credentials deleteCredId = credentialsRepository.findByemailId(emailToDeleteId);
            try {
                User user = userRepository.findBycredentials(deleteCredId);
                if(/*cred.getIsAdmin() || */cred.equals(deleteCredId)) {
                    userRepository.delete(user);
                    credentialsRepository.delete(deleteCredId);
                    return "success";
                } else {
                    return "failure";
                }
            } catch(Exception e) {
                return "failure";
            }
        } else {
            return "failure";
        }
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
