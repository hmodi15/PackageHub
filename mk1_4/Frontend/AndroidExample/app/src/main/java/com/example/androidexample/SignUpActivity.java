package com.example.androidexample;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;


/**
 * Activity for handling user sign up.
 */
public class SignUpActivity extends AppCompatActivity {

    AppCompatButton regstrbtn;
    FirebaseAuth mAuth;

    EditText editTextEmail, editTextPassword;
    ImageView backArrow;


    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView text = (TextView) findViewById(R.id.termsAndConditions);
        String words = "By Signing Up, You Agree To Our Terms Of Use";
        SpannableString spannableString = new SpannableString(words);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        text.setText(spannableString);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "TermsOfUse.html";
                copyFileFromAssets(filename);
                File file = new File(getFilesDir(), filename);
                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "text/html");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });



        backArrow = findViewById(R.id.backFromSignUpBtn);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);


        mAuth = FirebaseAuth.getInstance();

        regstrbtn = findViewById(R.id.signupbtn);
        regstrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordsMatch() && isValidEmail() && isValidPhone() && isValidAddress() && isValidPostalCode()) {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    makePostRequest();


                } else {
                    // Show a message or handle the case where passwords don't match

                    if(!passwordsMatch()){
                        Toast.makeText(SignUpActivity.this, "Passwords do not match or invalid email", Toast.LENGTH_SHORT).show();
                    }
                    if(!isValidEmail()){
                        Toast.makeText(SignUpActivity.this, "email not valid", Toast.LENGTH_SHORT).show();
                    }
                    if(!isValidPhone()){
                        Toast.makeText(SignUpActivity.this, "phone is not valid", Toast.LENGTH_SHORT).show();
                    }
                    if(!isValidAddress()){
                        Toast.makeText(SignUpActivity.this, "not valid address", Toast.LENGTH_SHORT).show();
                    }
                    if(!isValidPostalCode()){
                        Toast.makeText(SignUpActivity.this, "not valid postal code", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    /**
     * Opens the login page.
     */
    private void openLoginPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Checks if the passwords entered by the user match.
     *
     * @return true if the passwords match, false otherwise.
     */
    private boolean passwordsMatch(){
        EditText passwordEditText = findViewById(R.id.password);
        EditText rePasswordEditText = findViewById(R.id.repassword);

        String password = passwordEditText.getText().toString();
        String rePassword = rePasswordEditText.getText().toString();

        return password.equals(rePassword);
    }

    /**
     * Checks if the email entered by the user is valid.
     *
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail() {
        EditText emailEditText = findViewById(R.id.email);
        String email = emailEditText.getText().toString().trim();

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * checks if is a valid phone number
     */
    private boolean isValidPhone() {
        EditText phoneEditText = findViewById(R.id.phone);
        String phone = phoneEditText.getText().toString().trim();

        // Phone number validation logic goes here
        // For simplicity, let's assume a valid phone number is numeric and has a length of 10
        return phone.matches("\\d{10}");
    }

    /**
     * checks if is a valid address
     */
    private boolean isValidAddress() {
        EditText addressEditText = findViewById(R.id.Address);
        String address = addressEditText.getText().toString().trim();

        // Address validation logic goes here
        // For simplicity, let's assume any non-empty string is considered a valid address
        return !address.isEmpty();
    }

    /**
     * checks if is a valid postal code
     */
    private boolean isValidPostalCode() {
        EditText postalCodeEditText = findViewById(R.id.postalCode);
        String postalCode = postalCodeEditText.getText().toString().trim();

        // Postal code validation logic goes here
        // For simplicity, let's assume a valid postal code is alphanumeric and has a specific format
        return postalCode.matches("\\d{5}");
    }

    private void copyFileFromAssets(String filename) {
        // Get the asset manager
        AssetManager assetManager = getAssets();

        try {
            // Open the asset file
            InputStream inputStream = assetManager.open(filename);

            // Get the destination file in the app's private files directory
            File outFile = new File(getFilesDir(), filename);

            // Create an output stream to write the file
            OutputStream outputStream = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                outputStream = Files.newOutputStream(outFile.toPath());
            }

            // Copy the file byte by byte
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a POST request to create a new user.
     */
    private void makePostRequest() {
        // Get user input
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText addressEditText = findViewById(R.id.Address);
        EditText phoneEdittext = findViewById(R.id.phone);
        EditText postalCodeEditText = findViewById(R.id.postalCode);
        EditText nameEditText = findViewById(R.id.name);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEdittext.getText().toString().trim();
        String postCode = postalCodeEditText.getText().toString().trim();

        // Set up the JSON object to be sent in the POST request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emailId", email);
            jsonObject.put("passwordId", password);
            jsonObject.put("name", name);
            jsonObject.put("address", address);
            jsonObject.put("phone", phone);
            jsonObject.put("postCode", postCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = "http://coms-309-019.class.las.iastate.edu:8080/users";

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Make the POST request using JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        Log.d("Volley", "Response: " + response.toString());
                        openLoginPage();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors that occurred during the request
                        Log.e("Volley", "Error: " + error.toString());
                        // You can show an error message or handle errors as needed
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }



}