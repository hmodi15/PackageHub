package onetomany.Buildings;


import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import onetomany.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Harsh Modi
 *
 */

@RestController
public class BuildingController {

    @Autowired
    BuildingRepository buildingRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @Operation(summary = "Get a list of all the buildings")
    @GetMapping(path = "/buildings")
    List<Buildings> getAllUsers(){
        return buildingRepository.findAll();
    }

    @Operation(summary = "Get information about a specific building")
    @GetMapping(path = "/buildings/{id}")
    Buildings getUserById( @PathVariable int id){
        return buildingRepository.findById(id);
    }

    @Operation(summary = "Create a new building")
    @PostMapping("/buildings/")
    String createBuilding(@RequestBody Buildings building){
        if(building == null) {return failure;}

        buildingRepository.save(building);
        return success;
    }

    @Operation(summary = "Edit a current building's information")
    @PutMapping("/buildings/{id}")
    String updateBuilding(@PathVariable int id, @RequestBody Buildings request){
        Buildings user = buildingRepository.findById(id);
        if(user == null) {return failure;}
        buildingRepository.save(request);
        return success;
    }

    @Operation(summary = "Delete an existing building")
    @DeleteMapping(path = "/buildings/{id}")
    String deleteBuilding(@PathVariable int id){
        Buildings building = buildingRepository.findById(id);
        buildingRepository.delete(building);
        return success;
    }

}
