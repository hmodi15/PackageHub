package onetomany.Users;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.NonUniqueResultException;
import onetomany.Credentials.Credentials;
import onetomany.Credentials.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import onetomany.Rooms.Room;
import onetomany.Rooms.RoomRepository;
import onetomany.Packages.Package;
import onetomany.Packages.PackageRepository;

import static onetomany.Credentials.Credentials.hashPassword;


/**
 *
 * @author Benjimin Bartels and Harsh Modi
 *
 */

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    CredentialsRepository credentialsRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private static String chatUser;

    public UserController(UserRepository userRepository, CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }


    // =============================== Mappings ================================== //


    @Operation(summary = "Get a list of all the users")
    @GetMapping(path = "/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @Operation(summary = "Get a specific user")
    @GetMapping(path = "/users/{id}")
    public User getUserById( @PathVariable int id){
        return userRepository.findById(id);
    }

    @Operation(summary = "Get a specific user's details given their email")
    @GetMapping(path = "/account/{email}")
    public User getUserByEmail(@PathVariable String email) {
        Credentials userCred = credentialsRepository.findByemailId(email);
        return userRepository.findBycredentials(userCred);
    }

    @Operation(summary = "Get the list of packages of a specific user")
    @GetMapping(path = "/users/{id}/packages")
    public List<Package> getPackagesByUserId(@PathVariable int id, User user){
        user = userRepository.findById(id);
        return user.getPackages();
    }

    @Operation(summary = "Get the list of packages that are awaiting pickup of a specific user")
    @GetMapping(path = "/users/{id}/packagestopickup")
    List<Package> getAwaitingPackagesByUserId(@PathVariable int id){
        User user = userRepository.findById(id);
        return user.getAwaitingPackages();
    }

    @Operation(summary = "Make a new user that isn't already in the database")
    @PostMapping(path = "/users")
    public String createUser(@RequestBody User user){
        if(user == null || !user.getEmailId().contains("@") || !user.getEmailId().contains(".")) {
            return "failure";
        }
        try {
            if(credentialsRepository.findByemailId(user.getEmailId()).getEmailId().equals(user.getEmailId())) {
                return "failure";
            }
            user.setPasswordId(hashPassword(user.getPasswordId()));
            Credentials userCred = new Credentials(user.getEmailId(), user.getPasswordId());
            user.setCredentials(userCred);
            userCred.setUser(user);
            credentialsRepository.save(userCred);
            userRepository.save(user);
            return success;
        } catch (NonUniqueResultException e) {
            // Handle non-unique result exception
            return "failure"; // Return a specific message indicating a non-unique result
        } catch (Exception e) {
            // Log the exception or handle it appropriately
            user.setPasswordId(hashPassword(user.getPasswordId()));
            Credentials userCred = new Credentials(user.getEmailId(), user.getPasswordId());
            user.setCredentials(userCred);
            userCred.setUser(user);
            userRepository.save(user);
            credentialsRepository.save(userCred);
            return success;
        }
    }


    @Operation(summary = "Edit a specific user's details given their id")
    @PutMapping("/users/{id}")
    User updateUserViaId(@PathVariable int id, @RequestBody User request) throws ExecutionException, InterruptedException {

        User user = userRepository.findById(id);
        Credentials userCred;
        if(user.getCredentials() != null) {
            userCred = credentialsRepository.findByemailId(user.getEmailId());
        } else {
            userCred = new Credentials(request.getEmailId(), request.getPasswordId());
        }
        
        // Update user's name, emailId, and passwordId with the new data
        user.setName(request.getName());
        if(user.getCredentials() == null) {
            user.setCredentials(userCred);
        }
        user.setEmailId(request.getEmailId());
        user.setPasswordId(request.getPasswordId());
        userCred.setUser(user);
        credentialsRepository.save(userCred);

        user.setJoiningDate(new Date());
        user.setEmailId(request.getEmailId());
        user.setPasswordId(hashPassword(request.getPasswordId()));
        user.setJoiningDate(request.getJoiningDate());
        user.setActive(request.getIsActive());
        user.setPhone(request.getPhone());
        user.setPostCode(request.getPostCode());
        user.setAddress(request.getAddress());
        user.setAptNum(request.getAptNum());
        user.setIsManager(request.getIsManager());
        user.setIsAdmin(request.getIsAdmin());

        // Save the updated user object
        userRepository.save(user);

        // Return the updated user
        return user;
    }

    @Operation(summary = "Edit a specific user's details given their email")
    @PutMapping("/users/account/{email}")
    String updateUserViaEmail(@PathVariable String email, @RequestBody User request){
        Credentials userCred = credentialsRepository.findByemailId(email);
        User user = userRepository.findBycredentials(userCred);

        // Check if the user exists
        if (user == null) {
            return null; // User not found, return null
        }

        try {
            // Update user's name with the new data
            user.setName(request.getName());
            user.setPhone(request.getPhone());
            user.setPostCode(request.getPostCode());
            user.setAddress(request.getAddress());

            // Save the updated user object
            userRepository.save(user);
        } catch (Exception e) {
            return "failure";
        }
        // Return the updated user
        return "success";
    }

    @Operation(summary = "Get the room of a specific user")
    @GetMapping(path = "/users/{id}/room")
    public Room getRoomByUserId(@PathVariable int id, @RequestBody User user) {
        user = userRepository.findById(id);
        return user.getRoom();
    }

    @Operation(summary = "Assign a room to a user")
    @PutMapping("/users/{userId}/rooms/{roomId}")
    public String assignRoomToUser(@PathVariable int userId, @PathVariable int roomId){
        User user = userRepository.findById(userId);
        Room room = roomRepository.findById(roomId);
        if(user == null || room == null)
            return failure;
        room.addUser(user);
        user.setRoom(room);
        userRepository.save(user);
        return success;
    }


    @Operation(summary = "Assign a package to a user")
    @PutMapping("/users/{userId}/packages/{packageId}")
    public String assignPackageToUser(@PathVariable int userId,@PathVariable int packageId){
        User user = userRepository.findById(userId);
        Package pkg = packageRepository.findById(packageId);
        if(user == null || pkg == null)
            return failure;
        pkg.setUser(user);
        user.addPackages(pkg);
        userRepository.save(user);
        return success;
    }

    @Operation(summary = "(Admin Only) Delete a specific user via id")
    @DeleteMapping(path = "/users/{id}")
    @Transactional
    void deleteUser(@PathVariable int id) throws ExecutionException, InterruptedException {
        User user = userRepository.findById(id);
        Credentials userCred = credentialsRepository.findByemailId(user.getEmailId());
        credentialsRepository.delete(userCred);
        userRepository.deleteById(id);
    }

    @Operation(summary = "Delete a specific user via email")
    @DeleteMapping(path = "/users/delete/{email}")
    @Transactional
    public void deleteUser(@PathVariable String email){

        Credentials userCred = credentialsRepository.findByemailId(email);
        User user = userRepository.findBycredentials(userCred);
        List<Package> userPackages = user.getPackages();
        for (Package userPackage : userPackages) {
            if (userPackage != null) {
                deleteAPackage(userPackage.getid());
            }
        }
        credentialsRepository.deleteByemailId(userCred.getEmailId());
        userRepository.deleteById(user.getId());
    }

    public static String getChatUser() {
        return chatUser;
    }

    public static void setChatUser(String user) {
         chatUser = user;
    }

    public void deleteAPackage(int id){
        Package pkg = packageRepository.findById(id);
        if(pkg != null) {
            packageRepository.delete(pkg);
        }
    }
}
