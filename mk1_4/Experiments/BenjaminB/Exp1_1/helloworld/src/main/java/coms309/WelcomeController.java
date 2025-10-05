package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and thank you for letting us NOT do this demo on Saturday at noon.";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello " + name + ", what exactly were we supposed to do for this assignment?";
    }
}
