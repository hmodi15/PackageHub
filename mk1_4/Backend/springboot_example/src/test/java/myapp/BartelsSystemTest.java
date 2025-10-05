package myapp;

import io.restassured.mapper.ObjectMapperType;
import onetomany.Main;
import onetomany.Users.User;
import onetomany.Packages.Package;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
public class BartelsSystemTest {
/*
        @LocalServerPort
        int port;

        @BeforeEach
        public void setUp() {
            RestAssured.port = port;
            RestAssured.baseURI = "http://localhost";
        }

        @Test
        public void testGetPackageById() {
            given()
                    .pathParam("id", 427) // Specify the path variable 'id' with value 264
                    .when()
                    .get("/packages/{id}") // Use the path variable in the endpoint URL
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("joe"));
        }

        @Test
        public void testGetNewestPackageByEmail() {
            // Prepare the request body with user details
            ArrayList<Package> packages = new ArrayList<>();

            given()
                    .pathParam("email", "benstee22@gmail.com") // Specify the email as a path parameter
                    .when()
                    .get("/users/package/{email}") // Use the path variable in the endpoint URL
                    .then()
                    .statusCode(200)
                    .body("name", containsString("Ben Steenhoek"));
        }
/*
        @Test
        public void testCreatePackageWithScanner() {
            // Prepare the request body with user details
            //File file = new File("C:\\Workspace\\mk1_4_51-zxing-for-tracking-number\\Backend\\springboot_example\\src\\main\\resources\\IMG_5341.jpg");
            // Send a POST request to create the user
            given()
                    //.contentType(ContentType.MULTIPART)
                    .multiPart("image", getFile("src/main/resources/IMG_5341.jpg"))
                    .pathParam("id", "450")
                    .when()
                    .post("/packages/OCR/{id}")
                    .then()
                    .statusCode(200) // Assuming success status code
                    .body("name", containsString("Ben Steenhoek"));

            // After creating the user, send a GET request to retrieve the user by email
            given()
                    .pathParam("id", "450")
                    .when()
                    .get("/packages/{id}")
                    .then()
                    .statusCode(200) // Assuming success status code
                    .body("name", equalTo("Ben Steenhoek")); // Verify if the retrieved user's name matches the expected value
        }

        @Test
        public void testDeletePackage() {
            given()
                    .pathParam("id", "450")
                    .when()
                    .delete("/packages/{id}")
                    .then()
                    .statusCode(200);
        }
*/
}
