package onetomany.Rooms;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import onetomany.Buildings.BuildingRepository;
import onetomany.Buildings.Buildings;
import onetomany.Rooms.Room;
import onetomany.Rooms.RoomRepository;
import onetomany.Users.User;
import onetomany.Users.UserRepository;


@RestController
public class RoomController {

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @Operation(summary = "Get a list of all the rooms")
    @GetMapping(path = "/rooms")
    List<Room> getAllRooms(){
        return roomRepository.findAll();
    }

    @Operation(summary = "Get a specific room")
    @GetMapping(path = "/rooms/{id}")
    Room getRoomById( @PathVariable int id){
        return roomRepository.findById(id);
    }

    @Operation(summary = "Save a pre-made room object")
    @PostMapping(path = "/rooms")
    String createRoom(@RequestBody Room room){
        if (room == null)
            return failure;
        Buildings building = buildingRepository.findByName(room.getBuildingName());
        room.setBuilding(building);
        roomRepository.save(room);
        return success;
    }

    @Operation(summary = "Create a room from variables in a room object")
    @PostMapping(path = "/rooms/body")
    String createRoomWithBody(@RequestBody Room request){
        if(request == null)
            return null;
        Buildings building = buildingRepository.findByName(request.getBuildingName());
        request.setBuilding(building);
        roomRepository.save(request);
        return success;
    }

    @Operation(summary = "Update a specific room's details")
    @PutMapping("/rooms/{id}")
    Room updateRoom(@PathVariable int id, @RequestBody Room request){
        Room room = roomRepository.findById(id);
        if(room == null)
            return null;
        room.setAddress(request.getAddress());
        room.setAptNum(request.getAptNum());
        room.setBuildingName(request.getBuildingName());
        room.setUsers(request.getUsers());
        room.setMaxTenants(request.getMaxTenants());
        roomRepository.save(room);
        return roomRepository.findById(id);
    }

    @Operation(summary = "Delete a specific room")
    @DeleteMapping(path = "/rooms/{room_id}")
    @Transactional
    String deleteRoom(@PathVariable int room_id){
        roomRepository.deleteByid(room_id);
        return success;
    }
    /*@DeleteMapping(path = "/rooms/{room_id}")
    String evictFromRoom(@PathVariable int room_id, @PathVariable int user_id){
        Room room = roomRepository.findById(room_id);
        User user = userRepository.findById(user_id);
        List<User> users = room.getUsers();
        if(room == null)
            return null;
        if(users.contains(user)){
            users.remove(user);
            roomRepository.save(room);
            return success;
        }
        return failure;
    }*/
}
