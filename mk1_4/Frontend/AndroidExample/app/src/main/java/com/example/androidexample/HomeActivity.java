package com.example.androidexample;

import static com.example.androidexample.R.color.purple_200;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class extends AppCompatActivity and implements PackageInteractionListener.
 * It is used to display the home page of the application.
 */
public class HomeActivity extends AppCompatActivity implements PackageInteractionListener {

    private PackageAdapter adapter;
    private List<Package> packageList = new ArrayList<>();
    TextView managerTxt;

    FirebaseAuth auth;
    FirebaseUser user;
    private TabLayout tabLayout;
    int selectedTabPosition;
    private TextView placeholderTextView;


    /**
     * Called when the activity is starting.
     * It initializes the activity and the UI elements.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("darkMode", false);

        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        selectedTabPosition = 0;


        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.home);
        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");


        // Top tabs
        tabLayout = findViewById(R.id.tablLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Packages"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        Objects.requireNonNull(tabLayout.getTabAt(selectedTabPosition)).view.setBackgroundColor(getResources().getColor(purple_200));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedPosition = tab.getPosition();
                if(selectedPosition == 1){
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                    Intent intenet = new Intent(HomeActivity.this, PackageHistoryAcitvity.class);
                    startActivity(intenet);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackages);
        adapter = new PackageAdapter(packageList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getDataFromServer(user.getEmail());

        /*addPackagebtn = findViewById(R.id.addPackageButton);
        addPackagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPackagePage();
            }
        });*/

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.home) {

            } else if (item.getItemId() == R.id.profile) {
                // Switch to ProfileActivity
                intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("selectedItemId", R.id.profile);
                intent.putExtra("username", userName);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("postCode", postCode);
                intent.putExtra("address", address);
                startActivity(intent);
                // Return true to indicate the item selection is handled
                return true;
            }else if(item.getItemId() == R.id.occupents){
                intent = new Intent(this, SocialMediaActivity.class);
                //intent.putExtra("selectedItemId", R.id.occupents);
                startActivity(intent);
            }

            return true;
        });


    }

    /**
     * This method is used to get data from the server.
     * It sends a GET request to the server and updates the packageList with the response.
     */
    private void getDataFromServer(String name) {
        String url = "http://coms-309-019.class.las.iastate.edu:8080/users/packages/" + name;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String occupantName = jsonObject.isNull("name") ? "Unknown" : jsonObject.getString("name");
                                String pickUpCode = jsonObject.getString("pickUpCode");
                                String deliveryDate = jsonObject.getInt("scanYear") + "-" +
                                        jsonObject.getInt("scanMonth") + "-" +
                                        jsonObject.getInt("scanDate");
                                int id = jsonObject.getInt("id");
                                boolean pickUpStatus = jsonObject.getBoolean("pickUpStatus");

                                // only display packages that are not picked up
                                if(!pickUpStatus) {
                                    packageList.add(new Package(id, occupantName, deliveryDate, pickUpCode, false));
                                }


                            }
                            adapter.notifyDataSetChanged();

                            if(packageList.isEmpty()){
                                createPlaceholderTextView();
                            }else{
                                removePlaceholderTextView();
                            }
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
     * This method is used to delete a package from the packageList.
     * @param position The position of the package in the packageList.
     */
    public void deletePackage(int position){
        if(position >= 0 && position < packageList.size()){
            packageList.remove(position);
            adapter.notifyItemRemoved(position);
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
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(HomeActivity.this, "Package deleted successfully", Toast.LENGTH_SHORT).show();
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

    /**
     * This method is called when a package is deleted.
     * It deletes the package from the server and from the packageList.
     * @param packageId The id of the package to be deleted.
     * @param position The position of the package in the packageList.
     */
    @Override
    public void onDeletePackage(int packageId, int position) {
        deletePackageFromServer(packageId, position);
        deletePackage(position);
    }

    /**
     * This method is called when the occupant name of a package is updated.
     * It updates the occupant name on the server and in the packageList.
     * @param position The position of the package in the packageList.
     * @param updatedName The new name of the occupant.
     */

    /*
    @Override
    public void onUpdateOccupantName(int position, String updatedName) {
        updateOccupantNameOnServer(packageList.get(position).getId(), updatedName);
        // Update the local packageList data
        packageList.get(position).setOccupantName(updatedName);
        adapter.notifyItemChanged(position);
    }*/

    /**
     * This method is used to update the occupant name of a package on the server.
     * It sends a PUT request to the server with the updated name.
     * @param packageId The id of the package to be updated.
     */
    /*private void updateOccupantNameOnServer(int packageId, String updatedName) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + packageId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("passwordId", updatedName);
            requestBody.put("id", packageId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    Toast.makeText(HomeActivity.this, "Occupant name updated successfully", Toast.LENGTH_SHORT).show();

                    int position = findPackagePositionById(packageId);

                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating occupant name. " + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

        getDataFromServer();

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }/*

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
     * This method is used to navigate to the AddPackageActivity.
     * It starts the AddPackageActivity and finishes the current activity.
     */
    private void openAddPackagePage() {
        Intent intent = new Intent(this, AddPackageActivity.class);
        startActivity(intent);
        finish();
    }


    private void createPlaceholderTextView() {

        placeholderTextView = new TextView(this);
        placeholderTextView.setText("No New Packages To Display");
        placeholderTextView.setTextSize(20);
        placeholderTextView.setTextColor(getResources().getColor(android.R.color.black));

        placeholderTextView.setTypeface(null, Typeface.BOLD);


        ViewGroup rootLayout = findViewById(android.R.id.content);


        placeholderTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        placeholderTextView.setGravity(Gravity.CENTER);


        rootLayout.addView(placeholderTextView);


        int topMargin = rootLayout.getHeight() / 8;
        ((ViewGroup.MarginLayoutParams) placeholderTextView.getLayoutParams()).topMargin = topMargin;
    }

    private void removePlaceholderTextView() {
        if (placeholderTextView != null && placeholderTextView.getParent() != null) {
            ((ViewGroup) placeholderTextView.getParent()).removeView(placeholderTextView);
            placeholderTextView = null;
        }
    }

}
