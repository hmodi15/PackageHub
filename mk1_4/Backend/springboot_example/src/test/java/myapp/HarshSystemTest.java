package myapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import onetomany.Main;
import onetomany.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import onetomany.Packages.Package;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
public class HarshSystemTest {
/*
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	public void testGetUserById() {
		given()
<<<<<<< HEAD

=======
				.pathParam("id", 351) // Specify the path variable 'id' with value 264
>>>>>>> main
				.when()
				.get("/users/{id}") // Use the path variable in the endpoint URL
				.then()
				.statusCode(200)
<<<<<<< HEAD

=======
				.body("name", equalTo("Bob Bill"));
>>>>>>> main
	}

	@Test
	public void testGetUserByEmail() {
		given()
				.pathParam("email", "bobbill@gmail.com") // Specify the email as a path parameter
				.when()
				.get("/account/{email}") // Use the path variable in the endpoint URL
				.then()
				.statusCode(200)
				.body("name", equalTo("Bob Bill"));
	}

	@Test
	public void testCreateUser() {
		// Prepare the request body with user details
		User user = new User("Harsh Modi", "modi.harsh15@gmail.com", "password", "2056 Hawthorn Court Dr.", "1-800-236-5378", "50010");

		// Send a POST request to create the user
		given()
				.contentType(ContentType.JSON)
				.body(user)
				.when()
				.post("/users")
				.then()
				.statusCode(200); // Assuming success status code

		// After creating the user, send a GET request to retrieve the user by email
		given()
				.pathParam("email", "modi.harsh15@gmail.com")
				.when()
				.get("/account/{email}")
				.then()
				.statusCode(200) // Assuming success status code
				.body("name", equalTo("Harsh Modi")); // Verify if the retrieved user's name matches the expected value
	}

	@Test
	public void testCreateUser_Failure_InvalidEmail() {
		// Test with an invalid email format
		given()
				.contentType(ContentType.JSON)
				.body("{\"emailId\": \"invalid-email\", \"passwordId\": \"password\"}")
				.when()
				.post("/users")
				.then()
				.statusCode(200)
				.body(equalTo("failure"));
	}

	@Test
	public void testCreateUser_Failure_UserAlreadyExists() {
		// Test with an email that already exists in the database
		given()
				.contentType(ContentType.JSON)
				.body("{\"emailId\": \"bobbill@gmail.com\", \"passwordId\": \"password\"}")
				.when()
				.post("/users")
				.then()
				.statusCode(200)
				.body(equalTo("failure"));
	}

	@Test
	public void testLogin() {
		given()
				.contentType(ContentType.JSON)
				.body("{\"emailId\": \"bobbill@gmail.com\", \"passwordId\": \"password\"}")
				.when()
				.post("/login")
				.then()
				.statusCode(200)
				.body(equalTo("success"));
	}

	@Test
	public void testDeleteUser() {
		// Prepare the request body with user details
		User user = new User("Harsh Modi", "modi.harsh15@gmail.com", "password", "2056 Hawthorn Court Dr.", "1-800-236-5378", "50010");

		// Send a POST request to create the user
		given()
				.contentType(ContentType.JSON)
				.body(user)
				.when()
				.post("/users")
				.then()
				.statusCode(200); // Assuming success status code
		given()
				.pathParam("email", "modi.harsh15@gmail.com")
				.when()
				.delete("/users/delete/{email}")
				.then()
				.statusCode(200);
	}

	@Test
	public void testCreatePackage() {
		// Prepare the request body with user details
		User user = new User("Harsh Modi", "modi.harsh15@gmail.com", "password", "2056 Hawthorn Court Dr.", "1-800-236-5378", "50010");

		// Send a POST request to create the user
		given()
				.contentType(ContentType.JSON)
				.body(user)
				.when()
				.post("/users")
				.then()
				.statusCode(200); // Assuming success status code
		given()
				.pathParam("name", "Harsh Modi")
				.contentType(ContentType.JSON)
				.when()
				.post("/packages/create/{name}")
				.then()
				.body(equalTo("{\"message\":\"success\"}"));

	}

	@Test
	public void testDeletePackage() {
		// Prepare the request body with user details
		User user = new User("Harsh Modi", "modi.harsh15@gmail.com", "password", "2056 Hawthorn Court Dr.", "1-800-236-5378", "50010");

		// Send a POST request to create the user
		given()
				.contentType(ContentType.JSON)
				.body(user)
				.when()
				.post("/users")
				.then()
				.statusCode(200); // Assuming success status code
		given()
				.pathParam("name", "Harsh Modi")
				.contentType(ContentType.JSON)
				.when()
				.post("/packages/create/{name}")
				.then()
				.body(equalTo("{\"message\":\"success\"}"));

		// Retrieve the package IDs by sending a GET request
		Response response = given()
				.pathParam("email", "modi.harsh15@gmail.com")
				.when()
				.get("/users/packages/{email}");

		// Extract the package IDs from the response
		List<Integer> packageIds = response.jsonPath().getList("id");

		// Assuming you want to delete the first package in the list
		int packageId = packageIds.get(0);

		// Send a DELETE request to delete the package using the retrieved ID
		given()
				.pathParam("id", packageId)
				.when()
				.delete("/packages/{id}")
				.then()
				.statusCode(200);
<<<<<<< HEAD
	}*/
}
