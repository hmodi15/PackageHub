package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Activity for displaying a user's profile.
 */
public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextPostCode;
    private EditText editTextAptNum;
    private EditText editTextPhone;
    private Button cancelButton;
    private Button updateButton;
    private CheckBox managerCheckBox;
    private CheckBox adminCheckBox;
    private User currentUser;
    private List<User> userList;
    private Intent intent;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        intent = getIntent();
        try {
            currentUser = (User) intent.getSerializableExtra("user");
        }
        catch(Exception e){
            Log.e("Profile loader error", e.toString());
            Intent intentOut = new Intent(ProfileActivity.this, intent.getClass());
            //intent.putExtra("user", currentUser);
            startActivity(intent);
        }

        //Toolbar stuffs
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

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

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPostCode = findViewById(R.id.editTextPostCode);
        editTextAptNum = findViewById(R.id.editTextAptNum);
        editTextPhone = findViewById(R.id.editTextPhone);
        cancelButton = findViewById(R.id.cancelButton);
        updateButton = findViewById(R.id.updateButton);
        managerCheckBox = findViewById(R.id.managerCheckBox);
        adminCheckBox = findViewById(R.id.adminCheckBox);

        editTextUserName.setText(currentUser.getUserName());
        editTextEmail.setText(currentUser.getEmailId());
        editTextAddress.setText(currentUser.getAddress());
        editTextPostCode.setText(currentUser.getPostCode());
        editTextAptNum.setText(currentUser.getAptNum());
        editTextPhone.setText(currentUser.getPhone());
        managerCheckBox.setChecked(currentUser.getIsManager());
        adminCheckBox.setChecked(currentUser.getIsAdmin());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                intent.putExtra("startActiveView", "users");
                startActivity(intent);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.nav_home){
            Toast.makeText(this, "Refreshing Data...", Toast.LENGTH_SHORT).show();}
        else {
            Toast.makeText(this, "Gotcha (debug)!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * Called when the activity has detected the user's press of the back key. Closes sidebar.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Updates the user and calls update on server
    public void updateUser(){
        if(currentUser != null){
            currentUser.setUserName(editTextUserName.getText().toString());
            currentUser.setEmailId(editTextEmail.getText().toString());
            currentUser.setAddress(editTextAddress.getText().toString());
            currentUser.setPostCode(editTextPostCode.getText().toString());
            currentUser.setAptNum(editTextAptNum.getText().toString());
            currentUser.setPhone(editTextPhone.getText().toString());
            currentUser.setIsManager(managerCheckBox.isChecked());
            currentUser.setIsAdmin(adminCheckBox.isChecked());
            updateUserOnServer(currentUser);
        }
    }

    private void updateUserOnServer(User updatedUser) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/users/" + updatedUser.getId();
        //Toast.makeText(AdminDashboardActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", updatedUser.getId());
            requestBody.put("name", updatedUser.getUserName());
            requestBody.put("joiningDate", updatedUser.getJoiningDate());
            requestBody.put("isActive", updatedUser.getIsActive());
            requestBody.put("address", updatedUser.getAddress());
            requestBody.put("phone", updatedUser.getPhone());
            requestBody.put("postCode", updatedUser.getPostCode());
            requestBody.put("aptNum", updatedUser.getAptNum());
            requestBody.put("emailId", updatedUser.getEmailId());
            requestBody.put("isManager", updatedUser.getIsManager());
            requestBody.put("passwordId", updatedUser.getPasswordId());
            requestBody.put("isAdmin", updatedUser.getIsAdmin());

            // For the packages, you need to create a JSONArray and add each package as a JSONObject
            JSONArray packagesJsonArray = new JSONArray();
            for (Package pkg : updatedUser.getPackages()) {
                JSONObject packageJsonObject = new JSONObject();
                packageJsonObject.put("id", pkg.getId());
                packageJsonObject.put("name", pkg.getOccupantName());
                packageJsonObject.put("deliveryDate", pkg.getDeliveryDate());
                packageJsonObject.put("pickUpCode", pkg.getSecurityCode());
                packageJsonObject.put("pickUpStatus", pkg.getPickUpStatus());
                packagesJsonArray.put(packageJsonObject);
            }
            requestBody.put("packages", packagesJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ProfileActivity.this, "Failed to update user!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    Intent intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                    intent.putExtra("startActiveView", "users");
                    startActivity(intent);
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating user" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
}