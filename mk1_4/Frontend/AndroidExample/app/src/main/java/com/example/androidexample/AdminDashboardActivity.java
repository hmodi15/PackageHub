package com.example.androidexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Lucas Schakel
 * Admin screen codes
 */
public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PackageInteractionListenerManager, RoomInteractionListener, UserInteractionListener, BuildingInteractionListener {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DrawerLayout drawerLayout;
    private String adminUserName = "Lucas Schakel";
    private Toolbar headerToolbar;
    private String activeView;
    private PackageAdapterManager packageAdapter;
    private UserAdapter userAdapter;
    private BuildingAdapter buildingAdapter;
    private RoomAdapter roomAdapter;
    private RecyclerView packageRecyclerView;
    private RecyclerView userRecyclerView;
    private RecyclerView buildingRecyclerView;
    private RecyclerView roomRecyclerView;
    private List<Package> packageList = new ArrayList<>();
    private List<Package> packageListCopy = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<User> userListCopy = new ArrayList<>();
    private List<Building> buildingList = new ArrayList<>();
    private List<Building> buildingListCopy = new ArrayList<>();
    private List<Room> roomList = new ArrayList<>();
    private List<Room> roomListCopy = new ArrayList<>();
    private BottomNavigationView bottomNav;
    private String addressString = "Big Business";
    private SearchView unifiedSearchBar;

    /**
     * Initializes the admin screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Intent startupIntent = getIntent();
        activeView = "users";

        //firebase user get
        try {
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            String userInfoUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/" + user.getEmail();
            StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl,
                    response -> {
                        // Parse user information from the response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //these are the only one I really need for the admin credentials
                            adminUserName = jsonObject.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AdminDashboardActivity.this, "Error parsing user information", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                        Toast.makeText(AdminDashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("MainActivity", errorMessage);
                    });

            Volley.newRequestQueue(this).add(userInfoRequest);
        }
        catch(Exception e){
            Toast.makeText(AdminDashboardActivity.this, "Firebase Auth failed", Toast.LENGTH_SHORT);
            adminUserName = "Default Administrator";
        }

        //dark theme (not working currently)
        /*
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("darkMode", false);


        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

         */

        //update recycler view with all packages
        packageRecyclerView = findViewById(R.id.packageRecyclerView);
        packageAdapter = new PackageAdapterManager(packageList,this);
        packageRecyclerView.setAdapter(packageAdapter);
        packageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //update recycler view with all rooms
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roomAdapter = new RoomAdapter(roomList,this);
        roomRecyclerView.setAdapter(roomAdapter);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //update recycler view with all users
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userAdapter = new UserAdapter(userList,this);
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //update recycler view with all buildings
        buildingRecyclerView = findViewById(R.id.buildingRecyclerView);
        buildingAdapter = new BuildingAdapter(buildingList,this);
        buildingRecyclerView.setAdapter(buildingAdapter);
        buildingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getPackagesFromServer(addressString);
        getRoomsFromServer(addressString);
        getUsersFromServer(addressString);
        getBuildingsFromServer(addressString);

        headerToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(headerToolbar);
        headerToolbar.setSubtitle(adminUserName);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, headerToolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //bottom nav stuffs
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.adminNav_home) {
                // Handle switching to HomeActivity if needed
                intent = new Intent(AdminDashboardActivity.this, HomeActivity.class);
            }
            else if (item.getItemId() == R.id.adminNav_scan) {
                // Switch to ProfileActivity
                intent = new Intent(AdminDashboardActivity.this, UserProfileActivity.class);
                intent.putExtra("scan_type", "scan");
            }
            else if(item.getItemId() == R.id.adminNav_settings){
                intent = new Intent(AdminDashboardActivity.this, ManagerSettingsActivity.class);
                intent.putExtra("startActiveView", activeView);
            }

            if (intent != null) {
                startActivity(intent);
                // Return true to indicate the item selection is handled
                return true;
            } else {
                // Return false if the item selection is not handled
                return false;
            }
        });

        //Search bar
        unifiedSearchBar = findViewById(R.id.unifiedSearchBar);
        unifiedSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do nothing (using live time updates)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                Log.i("search", newText);
                if(!newText.isEmpty()){
                    Log.i("search", "searching...");
                    if(activeView.equals("packages")){
                        Log.i("search", "packages...");
                        List<Package> newList = new ArrayList<>();
                        for(Package pack : packageListCopy){
                            if(pack.getOccupantName().toLowerCase().contains(userInput) || pack.getSecurityCode().toLowerCase().contains(userInput)){
                                Log.i("search", "found: " + pack.getOccupantName());
                                newList.add(pack);
                            }
                        }
                        packageList.clear();
                        packageList.addAll(newList);
                        packageAdapter.notifyDataSetChanged();
                    }
                    else if(activeView.equals("rooms")){
                        Log.i("search", "rooms...");
                        List<Room> newList = new ArrayList<>();
                        for(Room room : roomListCopy){
                            if(room.getApartmentNumber().toLowerCase().contains(userInput)){
                                Log.i("search", "found: " + room.getApartmentNumber());
                                newList.add(room);
                            }
                        }
                        roomList.clear();
                        roomList.addAll(newList);
                        roomAdapter.notifyDataSetChanged();
                    }
                    else if(activeView.equals("users")){
                        Log.i("search", "users...");
                        List<User> newList = new ArrayList<>();
                        for(User user : userListCopy){
                            if(user.getUserName().toLowerCase().contains(userInput)){
                                Log.i("search", "found: " + user.getUserName());
                                newList.add(user);
                            }
                        }
                        userList.clear();
                        userList.addAll(newList);
                        userAdapter.notifyDataSetChanged();
                    }
                    else if(activeView.equals("buildings")){
                        Log.i("search", "buildings...");
                        List<Building> newList = new ArrayList<>();
                        for(Building building : buildingListCopy){
                            if(building.getName().toLowerCase().contains(userInput)){
                                Log.i("search", "found: " + building.getName());
                                newList.add(building);
                            }
                        }
                        buildingList.clear();
                        buildingList.addAll(newList);
                        buildingAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.i("search", "refreshing search data...");
                    roomList.clear();
                    packageList.clear();
                    userList.clear();
                    roomList.addAll(roomListCopy);
                    packageList.addAll(packageListCopy);
                    userList.addAll(userListCopy);
                    roomAdapter.notifyDataSetChanged();
                    packageAdapter.notifyDataSetChanged();
                    userAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //startup stuff (prepares data)
        if(startupIntent.hasExtra("startActiveView")){
            activeView = startupIntent.getStringExtra("startActiveView");
            if(activeView.equals("packages")){
                Toast.makeText(this, "Loading building packages...", Toast.LENGTH_SHORT).show();
                packageList.clear();
                packageAdapter.notifyDataSetChanged();
                roomRecyclerView.setVisibility(View.GONE);
                userRecyclerView.setVisibility(View.GONE);
                packageRecyclerView.setVisibility(View.VISIBLE);
                buildingRecyclerView.setVisibility(View.GONE);
                getPackagesFromServer(addressString);
                activeView = "packages";
            }
            else if(activeView.equals("rooms")){
                Toast.makeText(this, "Loading rooms...", Toast.LENGTH_SHORT).show();
                roomList.clear();
                roomAdapter.notifyDataSetChanged();
                roomRecyclerView.setVisibility(View.VISIBLE);
                packageRecyclerView.setVisibility(View.GONE);
                userRecyclerView.setVisibility(View.GONE);
                buildingRecyclerView.setVisibility(View.GONE);
                getRoomsFromServer(addressString);
                activeView = "rooms";
            }
            else if(activeView.equals("users")){
                Toast.makeText(this, "Loading users...", Toast.LENGTH_SHORT).show();
                userList.clear();
                userAdapter.notifyDataSetChanged();
                userRecyclerView.setVisibility(View.VISIBLE);
                packageRecyclerView.setVisibility(View.GONE);
                roomRecyclerView.setVisibility(View.GONE);
                buildingRecyclerView.setVisibility(View.GONE);
                getUsersFromServer(addressString);
                activeView = "users";
            }
            else if(activeView.equals("buildings")){
                Toast.makeText(this, "Loading buildings...", Toast.LENGTH_SHORT).show();
                buildingList.clear();
                buildingAdapter.notifyDataSetChanged();
                buildingRecyclerView.setVisibility(View.VISIBLE);
                packageRecyclerView.setVisibility(View.GONE);
                roomRecyclerView.setVisibility(View.GONE);
                userRecyclerView.setVisibility(View.GONE);
                getBuildingsFromServer(addressString);
                activeView = "buildings";
            }
        }
        else {
            Toast.makeText(AdminDashboardActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sidebar navigation feedback handler
     * @param item The selected item
     * @return true if successful, false if unsuccessful
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.nav_users){
            Toast.makeText(this, "Loading user data...", Toast.LENGTH_SHORT).show();
            roomRecyclerView.setVisibility(View.GONE);
            packageRecyclerView.setVisibility(View.GONE);
            buildingRecyclerView.setVisibility(View.GONE);
            userRecyclerView.setVisibility(View.VISIBLE);
            getUsersFromServer(addressString);
            activeView = "users";
        }
        else if(item.getItemId() == R.id.nav_buildings){
            Toast.makeText(this, "Getting buildings...", Toast.LENGTH_SHORT).show();
            buildingList.clear();
            buildingAdapter.notifyDataSetChanged();
            buildingRecyclerView.setVisibility(View.VISIBLE);
            packageRecyclerView.setVisibility(View.GONE);
            roomRecyclerView.setVisibility(View.GONE);
            userRecyclerView.setVisibility(View.GONE);
            getBuildingsFromServer(addressString);
            activeView = "buildings";
        }
        else if(item.getItemId() == R.id.nav_rooms){
            Toast.makeText(this, "Getting rooms...", Toast.LENGTH_SHORT).show();
            //getRequest("http://coms-309-019.class.las.iastate.edu:8080/rooms/" + lastRoomsEntryID.toString(), msgResponse3);
            roomRecyclerView.setVisibility(View.VISIBLE);
            packageRecyclerView.setVisibility(View.GONE);
            userRecyclerView.setVisibility(View.GONE);
            buildingRecyclerView.setVisibility(View.GONE);
            getRoomsFromServer(addressString);
            activeView = "rooms";
        }
        else if(item.getItemId() == R.id.add_room){
            Room newRoom = new Room(0, "", "", addressString, 0);
            onCreateRoom(newRoom);
        }
        else if(item.getItemId() == R.id.add_building){
            Building newBuilding = new Building(0, "New Building", true);
            onCreateBuilding(newBuilding);
        }
        else if(item.getItemId() == R.id.nav_addUser){
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.nav_logout){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "Gotcha (debug)!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * Closes the side navigation when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //---BELOW HERE IS ALL SHARED FROM MANAGER DASHBOARD---
    /**
     * This method is used to delete a package from the packageList.
     * @param position The position of the package in the packageList.
     */
    public void deletePackage(int position){
        if(position >= 0 && position < packageList.size()){
            packageList.remove(position);
            packageAdapter.notifyItemRemoved(position);
            //remove it from the copy as well
            packageListCopy.remove(position);
        }
    }

    /**
     * This method is used to delete a package from the server.
     * It sends a DELETE request to the server and removes the package from the packageList.
     * @param packageId The id of the package to be deleted.
     * @param position The position of the package in the packageList.
     */
    public void deletePackageFromServer(int packageId, final int position) {
        String deleteUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + packageId;
        Log.d("DeletePackageRequest", "Request URL: " + deleteUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    // Handle successful delete
                    // Remove the item from your list and notify the adapter
                    packageList.remove(position);
                    packageAdapter.notifyItemRemoved(position);
                    Toast.makeText(AdminDashboardActivity.this, "Package deleted successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error deleting package. " + (error != null ? error.getMessage() : "Unknown error");
                    //Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("DeletePackageError", errorMessage, error);
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onDeletePackage(int packageId, int position) {
        deletePackageFromServer(packageId, position);
        deletePackage(position);
    }

    @Override
    public void onMarkPackageDelivered(int position) {
        Package updatedPackage = packageList.get(position);
        if(updatedPackage.getPickUpStatus() == true){
            updatedPackage.setPickUpStatus(false);
        }
        else {
            updatedPackage.setPickUpStatus(true);
        }
        updatePackageOnServer(updatedPackage);
        // Update the local packageList data
        packageAdapter.notifyItemChanged(position);
        //update it on the copy as well
        Package updatedPackageCopy = packageListCopy.get(position);
        if(updatedPackageCopy.getPickUpStatus() == true){
            updatedPackageCopy.setPickUpStatus(false);
        }
        else {
            updatedPackageCopy.setPickUpStatus(true);
        }
    }

    /**
     * This method is used to update the occupant name of a package on the server.
     * It sends a PUT request to the server with the updated name.
     * @param updatedPackage the package to update
     */
    private void updatePackageOnServer(Package updatedPackage) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + updatedPackage.getId();
        //Toast.makeText(AdminDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", updatedPackage.getOccupantName());
            requestBody.put("id", updatedPackage.getId());
            requestBody.put("pickUpStatus", updatedPackage.getPickUpStatus());
            requestBody.put("pickUpCode", updatedPackage.getSecurityCode());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AdminDashboardActivity.this, "Failed to create package", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    //Toast.makeText(AdminDashboardActivity.this, "Package updated successfully", Toast.LENGTH_SHORT).show();
                    int position = findPackagePositionById(updatedPackage.getId());

                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating package" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(AdminDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateOccupantNameError", errorMessage, error);
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onUpdateOccupantName(int position, String updatedName) {
        Package updatedPackage = packageList.get(position);
        updatedPackage.setOccupantName(updatedName);
        updatePackageOnServer(updatedPackage);
        // Update the local packageList data
        packageList.get(position).setOccupantName(updatedName);
        packageAdapter.notifyItemChanged(position);
        packageListCopy.get(position).setOccupantName(updatedName);
    }

    /**
     * This method is used to find the position of a package in the packageList by its id.
     * @param packageId The id of the package.
     * @return The position of the package in the packageList, or -1 if the package is not found.
     */
    private int findPackagePositionById(int packageId) {
        for (int i = 0; i < packageList.size(); i++) {
            if (packageList.get(i).getId() == packageId) {
                return i;
            }
        }
        return -1; // Return -1 if the package with the given ID is not found in the list
    }

    /**
     * This method is used to get data from the server.
     * It sends a GET request to the server and updates the packageList with the response.
     */
    private void getPackagesFromServer(String filter) {
        packageList.clear();
        packageAdapter = new PackageAdapterManager(packageList,this);
        packageRecyclerView.setAdapter(packageAdapter);
        String url = "http://coms-309-019.class.las.iastate.edu:8080/packages";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String occupantName = jsonObject.isNull("name") ? "Unknown" : jsonObject.getString("name");
                                String pickUpCode = jsonObject.getString("pickUpCode");
                                String deliveryDate = jsonObject.getInt("scanYear") + "-" +
                                        jsonObject.getInt("scanMonth") + "-" +
                                        jsonObject.getInt("scanDate");
                                int id = jsonObject.getInt("id");

                                packageList.add(new Package(id,occupantName, deliveryDate, pickUpCode, false));
                            }
                            packageAdapter.notifyDataSetChanged();
                            packageListCopy.clear();
                            packageListCopy.addAll(packageList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * This method is used to get USER data from the server.
     * It sends a GET request to the server and updates the userList with the response.
     */
    private void getUsersFromServer(String filter) {
        userList.clear();
        userAdapter = new UserAdapter(userList,this);
        userRecyclerView.setAdapter(userAdapter);
        String url = "http://coms-309-019.class.las.iastate.edu:8080/users";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.isNull("name") ? "Unknown" : jsonObject.getString("name");
                                String joiningDate = jsonObject.isNull("joiningDate") ? "Unknown" : jsonObject.getString("joiningDate");
                                boolean isActive = jsonObject.getBoolean("isActive");
                                String address = jsonObject.isNull("address") ? "Unknown" : jsonObject.getString("address");
                                String phone = jsonObject.isNull("phone") ? "Unknown" : jsonObject.getString("phone");
                                String postCode = jsonObject.isNull("postCode") ? "Unknown" : jsonObject.getString("postCode");
                                String aptNum = jsonObject.isNull("aptNum") ? "Unknown" : jsonObject.getString("aptNum");

                                //Ties a user to their packages
                                List<Package> packages = new ArrayList<Package>();
                                JSONArray packageJsonArray = jsonObject.getJSONArray("packages");
                                for(int j = packageJsonArray.length() - 1; j >= 0; j--){
                                    JSONObject packageJsonObject = packageJsonArray.getJSONObject(j);
                                    String packageOccupantName = packageJsonObject.isNull("name") ? "Unknown" : packageJsonObject.getString("name");
                                    String packagePickUpCode = packageJsonObject.getString("pickUpCode");
                                    String packageDeliveryDate = packageJsonObject.getInt("scanYear") + "-" +
                                            packageJsonObject.getInt("scanMonth") + "-" +
                                            packageJsonObject.getInt("scanDate");
                                    boolean packagePickUpStatus = packageJsonObject.getBoolean("pickUpStatus");
                                    int packageId = packageJsonObject.getInt("id");

                                    packages.add(new Package(packageId,packageOccupantName, packageDeliveryDate, packagePickUpCode, packagePickUpStatus));
                                }

                                String emailId = jsonObject.isNull("emailId") ? "Unknown" : jsonObject.getString("emailId");
                                boolean isManager = jsonObject.getBoolean("isManager");
                                String passwordId = jsonObject.isNull("passwordId") ? "Unknown" : jsonObject.getString("passwordId");
                                boolean isAdmin = jsonObject.getBoolean("isAdmin");

                                userList.add(new User(id, name, joiningDate, isActive, address, phone, postCode, aptNum, packages, emailId, isManager, passwordId, isAdmin));
                            }
                            userAdapter.notifyDataSetChanged();
                            userListCopy.clear();
                            userListCopy.addAll(userList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("volleyError", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getRoomsFromServer(String filter) {
        roomList.clear();
        roomAdapter = new RoomAdapter(roomList,AdminDashboardActivity.this);
        roomRecyclerView.setAdapter(roomAdapter);
        String url = "http://coms-309-019.class.las.iastate.edu:8080/rooms";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String apartmentNumber = jsonObject.getString("aptNum");
                                String address = jsonObject.getString("address");
                                String buildingName = jsonObject.getString("buildingName");
                                int maxTenants = jsonObject.getInt("maxTenants");
                                int id = jsonObject.getInt("id");
                                Log.i("roomGet", "" + id);
                                roomList.add(new Room(id, apartmentNumber, address, buildingName, maxTenants));
                            }
                            roomAdapter.notifyDataSetChanged();
                            roomListCopy.clear();
                            roomListCopy.addAll(roomList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    //Gets buildings from server
    private void getBuildingsFromServer(String filter) {
        buildingList.clear();
        buildingAdapter = new BuildingAdapter(buildingList,AdminDashboardActivity.this);
        buildingRecyclerView.setAdapter(buildingAdapter);
        String url = "http://coms-309-019.class.las.iastate.edu:8080/buildings";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String buildingName = jsonObject.getString("name");
                                int id = jsonObject.getInt("id");
                                boolean isActive = jsonObject.getBoolean("isActive");
                                Log.i("buildingGet", "" + id);
                                buildingList.add(new Building(id, buildingName, isActive));
                            }
                            buildingAdapter.notifyDataSetChanged();
                            buildingListCopy.clear();
                            buildingListCopy.addAll(buildingList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onDeleteRoom(Room roomToDelete) {
        deleteRoomFromServer(roomToDelete);
        deleteRoom(roomToDelete);
    }

    public void deleteRoom(Room roomToDelete){
        if(roomToDelete != null){
            int index = roomList.indexOf(roomToDelete);
            roomList.remove(roomToDelete);
            roomAdapter.notifyItemRemoved(index);
            //remove it from the copy as well
            roomListCopy.remove(roomToDelete);
        }
    }

    public void deleteRoomFromServer(Room roomToDelete) {
        String deleteUrl = "http://coms-309-019.class.las.iastate.edu:8080/rooms/" + roomToDelete.getId();
        Log.d("DeleteRoomRequest", "Request URL: " + deleteUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    // Handle successful delete
                    Toast.makeText(AdminDashboardActivity.this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
                    int index = roomList.indexOf(roomToDelete);
                    roomList.remove(roomToDelete);
                    roomAdapter.notifyItemRemoved(index);
                    //remove it from the copy as well
                    roomListCopy.remove(roomToDelete);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error deleting room. " + (error != null ? error.getMessage() : "Unknown error");
                    //Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("DeleteRoomError", errorMessage, error);
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onUpdateRoom(Room updatedRoom) {
        updateRoom(updatedRoom);
        updateRoomOnServer(updatedRoom);
    }

    public void updateRoom(Room updatedRoom){
        if(updatedRoom != null){
            int index = roomList.indexOf(updatedRoom);
            roomList.set(index, updatedRoom);
            roomAdapter.notifyItemChanged(index);
            roomListCopy.set(index, updatedRoom);
        }
    }

    private void updateRoomOnServer(Room updatedRoom) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/rooms/" + updatedRoom.getId();
        //Toast.makeText(AdminDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("aptNum", updatedRoom.getApartmentNumber());
            requestBody.put("address", updatedRoom.getAddress());
            requestBody.put("buildingName", updatedRoom.getBuildingName());
            requestBody.put("maxTenants", updatedRoom.getMaxTenants());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AdminDashboardActivity.this, "Failed to create room", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    //Toast.makeText(AdminDashboardActivity.this, "Package updated successfully", Toast.LENGTH_SHORT).show();
                    //int position = findPackagePositionById(updatedRoom.getId());
                    int index = roomList.indexOf(updatedRoom);
                    roomList.set(index, updatedRoom);
                    roomAdapter.notifyItemChanged(index);
                    roomListCopy.set(index, updatedRoom);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating room" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(AdminDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateRoomError", errorMessage, error);
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onCreateRoom(Room newRoom) {
        addRoomOnServer(newRoom);
        if(newRoom != null){
            roomList.add(newRoom);
            int index = roomList.indexOf(newRoom);
            roomAdapter.notifyItemInserted(index);
            roomListCopy.add(newRoom);
        }
    }
    private void addRoomOnServer(Room newRoom) {
        // Construct the URL with parameters
        String postUrl = "http://coms-309-019.class.las.iastate.edu:8080/rooms";

        // Send a POST request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response, e.g., show a success message
                        Toast.makeText(AdminDashboardActivity.this, "Successfully created a new room!", Toast.LENGTH_SHORT).show();
                        getRoomsFromServer(addressString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error, e.g., show an error message
                        Toast.makeText(AdminDashboardActivity.this, "Failed to create a new room", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onDeleteUser(User userToDelete) {
        deleteUserFromServer(userToDelete);
        if(userToDelete != null){
            int index = userList.indexOf(userToDelete);
            userList.remove(userToDelete);
            userAdapter.notifyItemRemoved(index);
            //remove it from the copy as well
            userListCopy.remove(userToDelete);
        }
    }

    /**
     * Deletes user from the server
     * @param userToDelete
     */
    public void deleteUserFromServer(User userToDelete) {
        String deleteUrl = "http://coms-309-019.class.las.iastate.edu:8080/users/" + userToDelete.getId();
        Log.d("DeleteUserRequest", "Request URL: " + deleteUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    // Handle successful delete
                    Toast.makeText(AdminDashboardActivity.this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
                    int index = userList.indexOf(userToDelete);
                    userList.remove(userToDelete);
                    userAdapter.notifyItemRemoved(index);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error deleting room. " + (error != null ? error.getMessage() : "Unknown error");
                    //Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("DeleteRoomError", errorMessage, error);
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onUpdateUser(User updatedUser) {
        //not handled here anymore, all in the Admin Profile Editor!
    }

    @Override
    public void onCreateUser(User newUser) {
        //not handled here anymore, all in the Signup!
    }

    @Override
    public void onDeleteBuilding(Building buildingToDelete) {

    }

    @Override
    public void onUpdateBuilding(Building updatedBuilding) {
        if(updatedBuilding != null){
            int index = buildingList.indexOf(updatedBuilding);
            buildingList.set(index, updatedBuilding);
            buildingAdapter.notifyItemChanged(index);
            buildingListCopy.set(index, updatedBuilding);
        }
    }

    private void updateBuildingOnServer(Building updatedBuilding) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/buildings/" + updatedBuilding.getId();
        //Toast.makeText(AdminDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", updatedBuilding.getName());
            requestBody.put("isActive", updatedBuilding.getIsActive());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AdminDashboardActivity.this, "Failed to create building", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    //Toast.makeText(AdminDashboardActivity.this, "Package updated successfully", Toast.LENGTH_SHORT).show();
                    //int position = findPackagePositionById(updatedBuilding.getId());
                    //int index = buildingList.indexOf(updatedBuilding);
                    //buildingList.set(index, updatedBuilding);
                    //buildingAdapter.notifyItemChanged(index);
                    //buildingListCopy.set(index, updatedBuilding);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating building" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(AdminDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateBuildingError", errorMessage, error);
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onCreateBuilding(Building newBuilding) {
        addBuildingOnServer(newBuilding);
        if(newBuilding != null){
            buildingList.add(newBuilding);
            int index = buildingList.indexOf(newBuilding);
            buildingAdapter.notifyItemInserted(index);
            buildingListCopy.add(newBuilding);
        }
    }
    private void addBuildingOnServer(Building newBuilding) {
        // Construct the URL with parameters
        String postUrl = "http://coms-309-019.class.las.iastate.edu:8080/buildings";

        // Send a POST request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response, e.g., show a success message
                        Toast.makeText(AdminDashboardActivity.this, "Successfully created a new room!", Toast.LENGTH_SHORT).show();
                        getRoomsFromServer(addressString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error, e.g., show an error message
                        Toast.makeText(AdminDashboardActivity.this, "Failed to create a new room", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}