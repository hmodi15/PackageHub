package com.example.androidexample;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    Button btnReset, btnBack;
    TextInputEditText edtEmail, edtPassword, edtPrevPassword;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        progressBar = findViewById(R.id.forgetPasswordProgressbar);
        btnBack = findViewById(R.id.btnForgotPasswordBack);
        btnReset = findViewById(R.id.btnReset);

        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");


        edtEmail = findViewById(R.id.edtForgotPasswordEmail);
        edtPrevPassword = findViewById(R.id.edtPrevPassword);
        edtPassword = findViewById(R.id.edtNewPassword);





        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
                String prevPassword = Objects.requireNonNull(edtPrevPassword.getText()).toString().trim();
                //String newPassword = Objects.requireNonNull(edtPassword.getText()).toString().trim();

                String emails = user.getEmail();
                Log.d(TAG, emails);

                user.updatePassword(Objects.requireNonNull(edtPassword.getText()).toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User password updated");
                            // Validate credentials
                            validateCredentials(email, prevPassword);
                        }
                    }
                });



            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin(userName, name, phone, address, postCode);
            }
        });

    }

    private void openLogin(String userName, String name, String phone, String address, String postCode) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("address", address);
        intent.putExtra("postCode", postCode);
        startActivity(intent);
    }


    private void validateCredentials(String username, String password) {
        String loginUrl = "http://coms-309-019.class.las.iastate.edu:8080/login";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    if (response.trim().equalsIgnoreCase("success")) {
                        // Logic for success
                        Toast.makeText(ChangePasswordActivity.this, response, Toast.LENGTH_LONG).show();
                        EditText passwordEditText = findViewById(R.id.edtNewPassword);
                        String PASSWORD = passwordEditText.getText().toString();
                        changePassword(PASSWORD);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(ChangePasswordActivity.this, "Please Log Out Then Log In Again", Toast.LENGTH_LONG).show();
                    Log.e("ChangePasswordActivity", errorMessage);
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("emailId", username);
                    jsonBody.put("passwordId", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(this).add(loginRequest);
    }

    private void changePassword(String passwordx) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/edit/" + user.getEmail();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("passwordId", passwordx);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update

                },
                error -> {
                    // Handle error
                    String errorMessage = "Error" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateOccupantPasswordError", errorMessage, error);
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