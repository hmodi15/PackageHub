package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Ben Steenhoek, Lucas Schakel
 * This activity is used to add a new package.
 */
public class AddPackageActivity extends AppCompatActivity {
    private EditText nameText;

    private EditText dateText;

    private EditText securityText;

    private Button addButton;

    /**
     * This method is called when the activity is starting.
     * It initializes the activity and the UI elements.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        nameText = findViewById(R.id.editTextOccupantName);
        dateText = findViewById(R.id.editTextDeliveryDate);
        securityText = findViewById(R.id.editTextSecurityCode);
        addButton = findViewById(R.id.buttonAdd);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPackage();
            }
        });

    }


    /**
     * This method is used to add a new package.
     * It gets the input from the user, validates it, and sends a POST request to the server.
     */
    private void addPackage() {
        String name = nameText.getText().toString().trim();
        String date = dateText.getText().toString().trim();
        String securityCode = securityText.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty() || securityCode.isEmpty()) {
            return;
        }

        // Encode the parameters for the URL
        try {
            name = URLEncoder.encode(name, "UTF-8");
            date = URLEncoder.encode(date, "UTF-8");
            securityCode = URLEncoder.encode(securityCode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        // Construct the URL with parameters
        String postUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/create" +
                "?name=" + name +
                "&scan_Date_Str=" + date +
                "&pickUpCode=" + securityCode;

        // Send a POST request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response, e.g., show a success message
                        openHomePage();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error, e.g., show an error message
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * This method is used to navigate to the home page.
     * It starts the HomeActivity and finishes the current activity.
     */
    private void openHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


}

