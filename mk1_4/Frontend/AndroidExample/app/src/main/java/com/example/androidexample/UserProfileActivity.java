package com.example.androidexample;

import static android.content.ContentValues.TAG;
import static com.example.androidexample.R.layout.activity_user_profile;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Text;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {


    FirebaseAuth auth;
    Dialog dialog;
    Button btnDialogCancel, btnDialogDelete;

    LinearLayout supportLayout, settingsLayout, rateLayout;
    TextView LogoutBtn;
    TextView changePasswordBtn, managerPortalBtn;
    FirebaseUser user;
    String name, phoneNumber, address, postalCode;
    LinearLayout backgroundColor;
    ScrollView Switcher;
    Boolean isManager;

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

        setContentView(activity_user_profile);
        Switcher = findViewById(R.id.scrollView2);
        backgroundColor = findViewById(R.id.linearLayout);
        if(darkModeEnabled){
            Switcher.setBackgroundColor(getResources().getColor(R.color.black));
            backgroundColor.setBackgroundResource(R.drawable.profile_dark_grey_background);
        }




        TextView emailTextView = findViewById(R.id.emailView);







        LogoutBtn = findViewById(R.id.logoutBtn);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            emailTextView.setText(user.getEmail());
        }




        managerPortalBtn = findViewById(R.id.imageView2);
        managerPortalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMangerPortal();
            }
        });




        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.profile);



        //String userName = getIntent().getStringExtra("username");
        //String phone = getIntent().getStringExtra("phone");
        //String postCode = getIntent().getStringExtra("postCode");
        //String  name = getIntent().getStringExtra("name");
        //String address = getIntent().getStringExtra("address");



        /**
         * get user information
         */

        String userInfoUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/" + user.getEmail();

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl,
                response -> {
                    // Parse user information from the response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        name = jsonObject.getString("name");
                        phoneNumber = jsonObject.getString("phone");
                        address = jsonObject.getString("address");
                        postalCode = jsonObject.getString("postCode");
                        isManager = jsonObject.getBoolean("isManager");
                        TextView nameTextView = findViewById(R.id.nameTxt);
                        TextView phoneTextView = findViewById(R.id.phoneTxt);
                        TextView postCodeTextView = findViewById(R.id.postCodeTxt);
                        TextView addressTextView = findViewById(R.id.addressTxt);

                        nameTextView.setVisibility(View.VISIBLE);
                        emailTextView.setVisibility(View.VISIBLE);
                        nameTextView.setText(name);
                        phoneTextView.setText(formatPhoneNumber(phoneNumber));
                        postCodeTextView.setText(postalCode);
                        addressTextView.setText(address);
                        if(isManager){
                            managerPortalBtn.setVisibility(View.VISIBLE);
                        }



                        // Pass user information to home page




                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "Error parsing user information", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", errorMessage);
                });

        Volley.newRequestQueue(this).add(userInfoRequest);


        changePasswordBtn = findViewById(R.id.changePassTxt);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePassword(user.getEmail(), name, phoneNumber, address, postalCode);
            }
        });



        //TextView emailTextView = findViewById(R.id.emailView);

        //emailTextView.setText(userName);

        settingsLayout = findViewById(R.id.settingsBtn);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserProfileActivity.this, UserSettingsActivity.class);
                intent.putExtra("username", user.getEmail());
                intent.putExtra("name", name);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("address", address);
                intent.putExtra("postCode", postalCode);
                startActivity(intent);
            }
        });

        supportLayout = findViewById(R.id.supportLayout);
        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = "ws://coms-309-019.class.las.iastate.edu:8080/chat/" + user.getEmail();
                WebSocketManager1.getInstance().connectWebSocket(server);
                openChat(user.getEmail(), name, phoneNumber, address, postalCode);
            }
        });

        rateLayout = findViewById(R.id.rateUsLayout);
        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateUsDialog rateUsDialog = new RateUsDialog(UserProfileActivity.this);
                Objects.requireNonNull(rateUsDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                rateUsDialog.setCancelable(false);
                rateUsDialog.show();
            }
        });




        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
            }
        });


        dialog = new Dialog(UserProfileActivity.this);
        dialog.setContentView(R.layout.custom_dialog_logout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        btnDialogDelete = dialog.findViewById(R.id.btnDialogDelete);

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserProfileActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                logout();



            }
        });


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }


        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.home) {
                // Handle switching to HomeActivity if needed
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("username", user.getEmail());
                intent.putExtra("name", name);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("address", address);
                intent.putExtra("postCode", postalCode);
                startActivity(intent);
                // Return true to indicate the item selection is handled
                return true;
            } else if (item.getItemId() == R.id.profile) {

                return true;
            }else if(item.getItemId() == R.id.occupents){
                intent = new Intent(this, SocialMediaActivity.class);
                startActivity(intent);
            }

            return true;
        });



    }

    private void openChangePassword(String userName, String name, String phone, String address, String postCode) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("address", address);
        intent.putExtra("postCode", postCode);
        startActivity(intent);
    }


    /* private void openSettings(String username){
         Intent intent = new Intent(this, UserSettingsActivity.class);
         intent.putExtra("username", username);
         intent.putExtra("name", name);
         intent.putExtra("phoneNumber", phoneNumber);
         intent.putExtra("address", address);
         intent.putExtra("postCode", postalCode);
         intent.putExtra("isBobby", email.equals("bobby@gmail.com"));
         Toast.makeText(UserProfileActivity.this, username, Toast.LENGTH_SHORT).show();
         startActivity(intent);
     }*/
    private void openChat(String userName, String name, String phone, String address, String postCode) {
        Intent intent = new Intent(this, SupportChatActivity2.class);
        intent.putExtra("username", userName);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("address", address);
        intent.putExtra("postCode", postCode);
        startActivity(intent);
    }

    private void openMangerPortal(){
        Intent intent = new Intent(this, ManagerDashboardActivity.class);
        startActivity(intent);
    }

    private void logout(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() == 10) {
            return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
        } else {
            return phoneNumber;
        }
    }




}