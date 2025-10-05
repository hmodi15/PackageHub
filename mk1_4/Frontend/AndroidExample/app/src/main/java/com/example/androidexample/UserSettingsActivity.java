package com.example.androidexample;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSettingsActivity extends AppCompatActivity {


    ImageView backView, nightModeIcon, userInfoIcon, commentIcon, termsAndConditionsIcon, privacyIcon, deleteAccIcon;
    ImageView userRightIcon, leaveUsCommentRightIcon, termsAndConditionsRightIcon, privacyPolicyRightIcon;
    Dialog dialog;
    Button btnDialogCancel, btnDialogDelete;
    TextView deleteAccount;


    SwitchCompat darkModeSwitch;
    TextView deleteAccountTextEdit, darkModeTextView, leaveUsCommentTextView, termsAndConditionsTextView, viewPolicyTextView, userInfoTextView, settingsTitle;

    RelativeLayout privacyPolicyBtn, termsOfUseBtn, commentLayout, editUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Set the appropriate night mode
//        if (darkModeEnabled) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        setContentView(R.layout.activity_user_settings);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("darkMode", false);

        settingsTitle = findViewById(R.id.settingsTitleTextView);
        userRightIcon = findViewById(R.id.UserInfoRightIcon);
        leaveUsCommentRightIcon = findViewById(R.id.leaveUsCommentRightIcon);
        termsAndConditionsRightIcon = findViewById(R.id.termsAndConditionsRightIcon);
        privacyPolicyRightIcon = findViewById(R.id.privacyPolicyRightIcon);
        deleteAccountTextEdit = findViewById(R.id.deleteAccountTextView);
        darkModeTextView = findViewById(R.id.darkModeTextView);
        leaveUsCommentTextView = findViewById(R.id.leaveUsCommentTextView);
        termsAndConditionsTextView = findViewById(R.id.termsAndConditionsTextView);
        viewPolicyTextView = findViewById(R.id.viewPolicyTextView);
        userInfoTextView = findViewById(R.id.userInfoTextView);
        backView = findViewById(R.id.backFromSettingsBtn);
        nightModeIcon = findViewById(R.id.NightModeIcon);
        userInfoIcon = findViewById(R.id.userInfoIcon);
        commentIcon = findViewById(R.id.commentIcon);
        termsAndConditionsIcon = findViewById(R.id.termsAndConditionsIcon);
        privacyIcon = findViewById(R.id.privacyIcon);
        deleteAccIcon = findViewById(R.id.deleteAccIcon);


        darkModeSwitch = findViewById(R.id.switchDarkMode);
        darkModeSwitch.setChecked(darkModeEnabled);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("darkMode", isChecked);
                editor.apply();


                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        SharedPreferences preff = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean darkModeEnableds = preff.getBoolean("darkMode", false);
        if (darkModeEnableds) {
            userInfoTextView.setTextColor(getResources().getColor(android.R.color.white));
            deleteAccountTextEdit.setTextColor(getResources().getColor(android.R.color.white));
            darkModeTextView.setTextColor(getResources().getColor(android.R.color.white));
            leaveUsCommentTextView.setTextColor(getResources().getColor(android.R.color.white));
            termsAndConditionsTextView.setTextColor(getResources().getColor(android.R.color.white));
            viewPolicyTextView.setTextColor(getResources().getColor(android.R.color.white));
            settingsTitle.setTextColor(getResources().getColor(android.R.color.white));

            // Set icon tint color
            privacyPolicyRightIcon.setColorFilter(getResources().getColor(android.R.color.white));
            userRightIcon.setColorFilter(getResources().getColor(android.R.color.white));
            leaveUsCommentRightIcon.setColorFilter(getResources().getColor(android.R.color.white));
            termsAndConditionsRightIcon.setColorFilter(getResources().getColor(android.R.color.white));
            backView.setColorFilter(getResources().getColor(android.R.color.white));
            nightModeIcon.setColorFilter(getResources().getColor(android.R.color.white));
            userInfoIcon.setColorFilter(getResources().getColor(android.R.color.white));
            commentIcon.setColorFilter(getResources().getColor(android.R.color.white));
            termsAndConditionsIcon.setColorFilter(getResources().getColor(android.R.color.white));
            privacyIcon.setColorFilter(getResources().getColor(android.R.color.white));
            deleteAccIcon.setColorFilter(getResources().getColor(android.R.color.white));
        } else {
            settingsTitle.setTextColor(getResources().getColor(android.R.color.black));
            userInfoTextView.setTextColor(getResources().getColor(android.R.color.black));
            deleteAccountTextEdit.setTextColor(getResources().getColor(android.R.color.black));
            darkModeTextView.setTextColor(getResources().getColor(android.R.color.black));
            leaveUsCommentTextView.setTextColor(getResources().getColor(android.R.color.black));
            termsAndConditionsTextView.setTextColor(getResources().getColor(android.R.color.black));
            viewPolicyTextView.setTextColor(getResources().getColor(android.R.color.black));

            // Reset icon tint color to default (black)
            privacyPolicyRightIcon.clearColorFilter();
            termsAndConditionsRightIcon.clearColorFilter();
            leaveUsCommentRightIcon.clearColorFilter();
            userRightIcon.clearColorFilter();
            backView.clearColorFilter();
            nightModeIcon.clearColorFilter();
            userInfoIcon.clearColorFilter();
            commentIcon.clearColorFilter();
            termsAndConditionsIcon.clearColorFilter();
            privacyIcon.clearColorFilter();
            deleteAccIcon.clearColorFilter();
        }






        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");




        deleteAccount = findViewById(R.id.deleteAccountBtn);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });



        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(userName, name, phone, address, postCode);
            }
        });

        dialog = new Dialog(UserSettingsActivity.this);
        dialog.setContentView(R.layout.custom_delete_account_dialog);
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

        editUserBtn = findViewById(R.id.EditUserLayout);
        editUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this, UserEditSettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        privacyPolicyBtn = findViewById(R.id.privacyPolicyLayout);
        privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
                finish();

            }
        });

        termsOfUseBtn = findViewById(R.id.termsAndConditionsLayout);
        termsOfUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        commentLayout = findViewById(R.id.commentLayout);
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LeaveUsCommentActivity.class);
                startActivity(intent);
                finish();

            }
        });



        btnDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                assert user != null;
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User account deleted.");
                            deleteUserAccount(userName);
                        }else{
                            Log.d(TAG, "User account failed to delete.");
                        }
                    }
                });


            }
        });



    }

    private void deleteUserAccount(String username) {
        String deleteUrl = "http://coms-309-019.class.las.iastate.edu:8080/users/delete/" + username;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful account deletion
                        Toast.makeText(UserSettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        // Redirect to login page or any other appropriate action
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Toast.makeText(UserSettingsActivity.this, "Error deleting account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(deleteRequest);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openProfile(String email, String name, String phone, String address, String postalCode){
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("username", email);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("address", address);
        intent.putExtra("postCode", postalCode);
        intent.putExtra("selectedItemId", R.id.profile);
        startActivity(intent);
        finish();
    }



}