package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class UserEditSettingsActivity extends AppCompatActivity {

    TextInputEditText nameTxt, phoneTxt, addressTxt, postCodeTxt;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView backBtn;
    MaterialButton updateBtn;
    String name, phoneNumber, address, postalCode;

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
        setContentView(R.layout.activity_user_edit_settings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        fetchUserInfo(user.getEmail());

        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backBtn);
        nameTxt = findViewById(R.id.nameInputText);
        phoneTxt = findViewById(R.id.phoneInputText);
        addressTxt = findViewById(R.id.addressInputText);
        postCodeTxt = findViewById(R.id.postCodeInputText);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEditSettingsActivity.this, UserSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInformation();
            }
        });


    }


    private void fetchUserInfo(String email){
        String userInfoUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/" + email;

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl,
                response -> {
                    // Parse user information from the response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        name = jsonObject.getString("name");
                        phoneNumber = jsonObject.getString("phone");
                        address = jsonObject.getString("address");
                        postalCode = jsonObject.getString("postCode");

                        nameTxt.setText(name);
                        phoneTxt.setText(phoneNumber);
                        addressTxt.setText(address);
                        postCodeTxt.setText(postalCode);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserEditSettingsActivity.this, "Error parsing user information", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(UserEditSettingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", errorMessage);
                });

        Volley.newRequestQueue(this).add(userInfoRequest);

    }


    private void updateUserInformation() {

        String updatedName = Objects.requireNonNull(nameTxt.getText()).toString().trim();
        String updatedPhoneNumber = Objects.requireNonNull(phoneTxt.getText()).toString().trim();
        String updatedAddress = Objects.requireNonNull(addressTxt.getText()).toString().trim();
        String updatedPostalCode = Objects.requireNonNull(postCodeTxt.getText()).toString().trim();

        if (updatedName.isEmpty() || updatedPhoneNumber.isEmpty() || updatedAddress.isEmpty() || updatedPostalCode.isEmpty()) {
            Toast.makeText(UserEditSettingsActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        if (!isValidPhoneNumber(updatedPhoneNumber)) {
            Toast.makeText(UserEditSettingsActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return; // Exit the method if phone number is invalid
        }

        if (!isValidPostalCode(updatedPostalCode)) {
            Toast.makeText(UserEditSettingsActivity.this, "Invalid postal code", Toast.LENGTH_SHORT).show();
            return; // Exit the method if postal code is invalid
        }
        if (updatedAddress.length() < 5) { // For example, checking if the address is at least 5 characters long
            Toast.makeText(UserEditSettingsActivity.this, "Invalid address", Toast.LENGTH_SHORT).show();
            return; // Exit the method if address is invalid
        }


        JSONObject updatedUserInfo = new JSONObject();
        try {
            updatedUserInfo.put("name", updatedName);
            updatedUserInfo.put("phone", updatedPhoneNumber);
            updatedUserInfo.put("address", updatedAddress);
            updatedUserInfo.put("postCode", updatedPostalCode);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/users/account/" + user.getEmail();
        StringRequest updateRequest = new StringRequest(Request.Method.PUT, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response)) { // Check if response is not empty
                            // Handle successful update
                            Toast.makeText(UserEditSettingsActivity.this, "User information updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserEditSettingsActivity.this, UserSettingsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(UserEditSettingsActivity.this, "Unexpected response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserEditSettingsActivity.this, "Error updating user information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                return updatedUserInfo.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(UserEditSettingsActivity.this).add(updateRequest);
    }



    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberPattern = "\\d{10}";
        return phoneNumber.matches(phoneNumberPattern);
    }

    // Function to validate postal code
    private boolean isValidPostalCode(String postalCode) {
        String postalCodePattern = "\\d{5}";
        return postalCode.matches(postalCodePattern);
    }
}