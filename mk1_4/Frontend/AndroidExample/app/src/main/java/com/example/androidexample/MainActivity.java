package com.example.androidexample;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQ_ONE_TAP = 100;
    FirebaseAuth auth, mAuth;
    String name, mail;
    GoogleSignInClient googleSignInClient;
    EditText username;
    EditText password;

    Button loginButton;
    Button signUpButton;
    TextView forgotPasTxt;
    SignInButton googleSignInBtn;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    String namex, postcodex, phonex, addressx;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                auth = FirebaseAuth.getInstance();
                                name = auth.getCurrentUser().getDisplayName();
                                mail = auth.getCurrentUser().getEmail();
                                Toast.makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                                //get the persons name:


                                // if I want to get profile image
                                //Glide.with(MainActivity.this).load(auth.getCurrentUser()).getPhotoUrl()).into(imageView);
                            }else{
                                Toast.makeText(MainActivity.this, "Failed to sign in:" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (ApiException e){
                    e.printStackTrace();
                }
            }
        }
    });




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        username = findViewById(R.id.username);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            username.setAutofillHints(View.AUTOFILL_HINT_USERNAME);
        }
        password = findViewById(R.id.password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
        }
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signingUpButton);
        forgotPasTxt = findViewById(R.id.forgotPasswordTxt);
        googleSignInBtn = findViewById(R.id.googleSignupBtn);

        signUpButton.setPaintFlags(signUpButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Firebase stuff
        FirebaseApp.initializeApp(this);





        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, options);

        auth = FirebaseAuth.getInstance();

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
               buttonGoogleSignIn(view);
            }
        });







        /*
        Google sign in
         */

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("873853085970-t3rj1t2n4vpuv8b1teeqj50dgstoc4i3.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();




        forgotPasTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPass();
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                mAuth = FirebaseAuth.getInstance();

                // Make a network request to validate the credentials

                mAuth.signInWithEmailAndPassword(enteredUsername, enteredPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                                    // update the password on backend
                                    changePassword(enteredUsername, enteredPassword);

                                    validateCredentials(enteredUsername, enteredPassword);
                                } else {

                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }


        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpPage();
            }
        });

    }

    private void validateCredentials(String username, String password) {
        String loginUrl = "http://coms-309-019.class.las.iastate.edu:8080/login";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    if (response.trim().equalsIgnoreCase("success")) {
                        // Logic for success
                        EditText emailEditText = findViewById(R.id.username);
                        String email = emailEditText.getText().toString();
                        fetchUserInfo(email);
                        //openHomePage2(email);
                    } else {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", errorMessage);
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

    private void fetchUserInfo(String email){
        String userInfoUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/" + email;

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl,
                response -> {
                    // Parse user information from the response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String name = jsonObject.getString("name");
                        String phoneNumber = jsonObject.getString("phone");
                        String address = jsonObject.getString("address");
                        String postalCode = jsonObject.getString("postCode");



                        // Pass user information to home page
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("username", email);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phoneNumber);
                        intent.putExtra("address", address);
                        intent.putExtra("postCode", postalCode);
                        intent.putExtra("isBobby", email.equals("bobby@gmail.com"));
                        startActivity(intent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error parsing user information", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", errorMessage);
                });

        Volley.newRequestQueue(this).add(userInfoRequest);



    }

    public void buttonGoogleSignIn(View view){
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
            @Override
            public void onSuccess(BeginSignInResult result) {
                try {
                    startIntentSenderForResult(
                            result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                            null, 0, 0, 0);
                    Log.d(TAG, "beginSignIn on Success Triggered");
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                }
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, "failed to connect " + e.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    openHomePage(username);
                    if (idToken !=  null) {
                        Log.d(TAG, "Got ID token.");
                    } else if (password != null) {

                        Log.d(TAG, "Got password.");
                    }
                } catch (ApiException e) {
                    //
                }
                break;
        }
    }


    /*private void updatePassword(String updatePassword) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/edit/" ;
        Log.d("Update Passsword", "Request URL: " + updatePassword);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    Toast.makeText(MainActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating password. " + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdatePasswordError", errorMessage, error);
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }*/


    private void changePassword(String email, String passwordx) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/edit/" + email;

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
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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


    private void openHomePage(String userName) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("username", userName);
        startActivity(intent);
        finish();
    }

    private void openSignUpPage(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void openForgotPass(){
        Intent intent = new Intent(this, ForgotPasswordLoginActivity.class);
        startActivity(intent);
    }

}