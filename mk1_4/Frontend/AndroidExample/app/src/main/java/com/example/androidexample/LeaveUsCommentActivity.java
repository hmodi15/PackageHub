package com.example.androidexample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LeaveUsCommentActivity extends AppCompatActivity {

    ImageView backBtn;

    AppCompatButton sendButton;

    TextInputEditText text;
    FirebaseAuth auth;
    FirebaseUser user;
    String name;

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
        setContentView(R.layout.activity_leave_us_comment);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        fetchUserInfo(user.getEmail());






        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
                startActivity(intent);
            }
        });


        // send message to backend server then go back to settings
        sendButton = findViewById(R.id.sendBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = findViewById(R.id.messageText);
                if(text != null){
                    String message = Objects.requireNonNull(text.getText()).toString().trim();
                    if(!message.isEmpty()){
                        postUserMessage(user.getEmail(), name, message);
                    }else{
                        Toast.makeText(LeaveUsCommentActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeaveUsCommentActivity.this, "Error Sending Message", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(LeaveUsCommentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("LeaveUsCommentActivity", errorMessage);
                });

        Volley.newRequestQueue(this).add(userInfoRequest);

    }


    private void postUserMessage(String email, String name, String message) {
        String postUrl = "http://coms-309-019.class.las.iastate.edu:8080/feedback";

        // Create JSON object with user data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emailId", email);
            jsonObject.put("name", name);
            jsonObject.put("text", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", "Response: " + response.toString());


                        Toast.makeText(LeaveUsCommentActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LeaveUsCommentActivity.this, UserSettingsActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}