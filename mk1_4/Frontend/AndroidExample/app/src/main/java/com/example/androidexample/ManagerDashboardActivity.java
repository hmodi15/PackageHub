package com.example.androidexample;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.app.AlertDialog;
import android.content.DialogInterface;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class extends AppCompatActivity and implements NavigationView.OnNavigationItemSelectedListener.
 * It is used to display the manager dashboard of the application.
 */
public class ManagerDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PackageInteractionListenerManager, RoomInteractionListener {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DrawerLayout drawerLayout;
    private PackageAdapterManager packageAdapter;
    private RoomAdapter roomAdapter;
    private RecyclerView packageRecyclerView;
    private RecyclerView roomRecyclerView;
    private List<Package> packageList = new ArrayList<>();
    private List<Package> packageListCopy = new ArrayList<>();
    private List<Room> roomList = new ArrayList<>();
    private List<Room> roomListCopy = new ArrayList<>();
    private BottomNavigationView bottomNav;
    private String addressString = "Big Business";
    private String managerNameString = "Lucas Schakel";
    private SearchView unifiedSearchBar;
    private String activeView = "packages";
    /**
     * Called when the activity is starting.
     * It initializes the activity and the UI elements.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startupIntent = getIntent();
        setContentView(R.layout.activity_manager_dashboard);

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
                            managerNameString = jsonObject.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ManagerDashboardActivity.this, "Error parsing user information", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                        Toast.makeText(ManagerDashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("MainActivity", errorMessage);
                    });

            Volley.newRequestQueue(this).add(userInfoRequest);
        }
        catch(Exception e){
            Toast.makeText(ManagerDashboardActivity.this, "Firebase Auth failed", Toast.LENGTH_SHORT);
            managerNameString = "Default Manager";
        }

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

        getPackagesFromServer(addressString);
        getRoomsFromServer(addressString);

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
                }
                else{
                    Log.i("search", "refreshing search data...");
                    roomList.clear();
                    packageList.clear();
                    roomList.addAll(roomListCopy);
                    packageList.addAll(packageListCopy);
                    roomAdapter.notifyDataSetChanged();
                    packageAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        //Toolbar stuffs
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(managerNameString);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //bottom nav stuffs
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.managerNav_home) {
                // Handle switching to HomeActivity if needed
                intent = new Intent(ManagerDashboardActivity.this, HomeActivity.class);
            }
            else if (item.getItemId() == R.id.managerNav_scan) {
                // Switch to ProfileActivity
                intent = new Intent(ManagerDashboardActivity.this, ScannerActivity.class);
                intent.putExtra("scan_type", "scan");
            }
            else if(item.getItemId() == R.id.managerNav_settings){
                intent = new Intent(ManagerDashboardActivity.this, ManagerSettingsActivity.class);
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

        //startup stuff (prepares data)
        if(startupIntent.hasExtra("startActiveView")){
            activeView = startupIntent.getStringExtra("startActiveView");
            if(activeView.equals("packages")){
                Toast.makeText(this, "Loading building packages...", Toast.LENGTH_SHORT).show();
                packageList.clear();
                packageAdapter.notifyDataSetChanged();
                roomRecyclerView.setVisibility(View.GONE);
                packageRecyclerView.setVisibility(View.VISIBLE);
                getPackagesFromServer(addressString);
                activeView = "packages";
            }
            else if(activeView.equals("rooms")){
                Toast.makeText(this, "Loading rooms...", Toast.LENGTH_SHORT).show();
                roomList.clear();
                roomAdapter.notifyDataSetChanged();
                roomRecyclerView.setVisibility(View.VISIBLE);
                packageRecyclerView.setVisibility(View.GONE);
                getRoomsFromServer(addressString);
                activeView = "rooms";
            }
        }
        else {
            Toast.makeText(ManagerDashboardActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sidebar navigation call handler function
     * @param item The selected item
     * @return true if successful
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.nav_packages){
            Toast.makeText(this, "Loading building packages...", Toast.LENGTH_SHORT).show();
            roomRecyclerView.setVisibility(View.GONE);
            packageRecyclerView.setVisibility(View.VISIBLE);
            getPackagesFromServer(addressString);
            activeView = "packages";
        }
        else if(item.getItemId() == R.id.nav_rooms){
            Toast.makeText(this, "Loading rooms...", Toast.LENGTH_SHORT).show();
            roomRecyclerView.setVisibility(View.VISIBLE);
            packageRecyclerView.setVisibility(View.GONE);
            getRoomsFromServer(addressString);
            activeView = "rooms";
        }
        else if(item.getItemId() == R.id.nav_home){
            Intent intent = new Intent(ManagerDashboardActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.nav_logout){
            Intent intent = new Intent(ManagerDashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.add_room){
            Room newRoom = new Room(0, "", "", addressString, 0);
            onCreateRoom(newRoom);
        }
        else if(item.getItemId() == R.id.nav_stats){
            // Create an AlertDialog.Builder instance
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            int heldPackageCount = 0;

            for(Package pack : packageListCopy){
                if(pack.getPickUpStatus() == false){
                    heldPackageCount += 1;
                }
            }

            // Set the message for the AlertDialog
            builder.setTitle(addressString + " Statistics");
            builder.setMessage("Undelivered Packages: " + heldPackageCount + "\n" + "Lifetime Total Packages: " + packageListCopy.size() + "\n" + "Total Rooms: " + roomList.size());

            // Add an 'OK' button to the AlertDialog
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if(item.getItemId() == R.id.nav_addPackage){
            Intent intent = new Intent(ManagerDashboardActivity.this, AddPackageActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "Gotcha (debug)!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * Back button onClick handler. Closes the side navigation.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
                    Toast.makeText(ManagerDashboardActivity.this, "Package deleted successfully", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(ManagerDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", updatedPackage.getOccupantName());
            requestBody.put("id", updatedPackage.getId());
            requestBody.put("pickUpStatus", updatedPackage.getPickUpStatus());
            requestBody.put("pickUpCode", updatedPackage.getSecurityCode());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ManagerDashboardActivity.this, "Failed to create package", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    //Toast.makeText(ManagerDashboardActivity.this, "Package updated successfully", Toast.LENGTH_SHORT).show();
                    int position = findPackagePositionById(updatedPackage.getId());

                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating package" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(ManagerDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
        //update it on the copy as well
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

    private void getRoomsFromServer(String filter) {
        roomList.clear();
        roomAdapter = new RoomAdapter(roomList,ManagerDashboardActivity.this);
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
                    Toast.makeText(ManagerDashboardActivity.this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(ManagerDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("aptNum", updatedRoom.getApartmentNumber());
            requestBody.put("address", updatedRoom.getAddress());
            requestBody.put("buildingName", updatedRoom.getBuildingName());
            requestBody.put("maxTenants", updatedRoom.getMaxTenants());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ManagerDashboardActivity.this, "Failed to create room", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    //Toast.makeText(ManagerDashboardActivity.this, "Package updated successfully", Toast.LENGTH_SHORT).show();
                    //int position = findPackagePositionById(updatedRoom.getId());
                    int index = roomList.indexOf(updatedRoom);
                    roomList.set(index, updatedRoom);
                    roomAdapter.notifyItemChanged(index);
                    roomListCopy.set(index, updatedRoom);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating room" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(ManagerDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ManagerDashboardActivity.this, "Successfully created a new room!", Toast.LENGTH_SHORT).show();
                        getRoomsFromServer(addressString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error, e.g., show an error message
                        Toast.makeText(ManagerDashboardActivity.this, "Failed to create a new room", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}