package com.example.androidexample;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class RateUsDialog extends Dialog {

    private float userRate;
    FirebaseAuth auth;
    FirebaseUser user;

    String name;


    public RateUsDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us_dialog_layout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        fetchUserInfo(user.getEmail());

        final AppCompatButton rateNowBtn = findViewById(R.id.rateNowBtn);
        final AppCompatButton laterBtn = findViewById(R.id.laterBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final ImageView ratingImage = findViewById(R.id.ratingImage);

        userRate = ratingBar.getRating();




        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postUserRating(user.getEmail(), name, userRate);

            }
        });

        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(rating <= 1){
                    ratingImage.setImageResource(R.drawable.one_star);
                } else if (rating <= 2) {
                    ratingImage.setImageResource(R.drawable.two_star);
                }else if(rating <= 3){
                    ratingImage.setImageResource(R.drawable.three_star);
                }else if(rating <= 4){
                    ratingImage.setImageResource(R.drawable.four_star);
                }else if(rating <=5){
                    ratingImage.setImageResource(R.drawable.five_star);
                }

                animateImage(ratingImage);

                userRate = rating;

            }
        });

    }

    private void animateImage(ImageView ratingImage){
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }



    private void fetchUserInfo(String email){
        String userInfoUrl = "http://coms-309-019.class.las.iastate.edu:8080/account/" + email;

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl,
                response -> {
                    // Parse user information from the response
                    Log.d("RateUsDiolog", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        name = jsonObject.getString("name");


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Log.e("MainActivity", errorMessage);
                });

        Volley.newRequestQueue(getContext()).add(userInfoRequest);



    }

    private void postUserRating(String email, String name, float rating) {
        String postUrl = "http://coms-309-019.class.las.iastate.edu:8080/ratings";

        // Create JSON object with user data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emailId", email);
            jsonObject.put("name", name);
            jsonObject.put("stars", rating);
            Log.d("RateUsDialog", email);
            Log.d("RateUsDialog", name);
            Log.d("RateUsDialog", String.valueOf(rating));
            Log.d("RateUsDiolog", "Post Data: " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Create request to post user rating
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Make the POST request using JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        Toast.makeText(getContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                        Log.d("Volley", "Response: " + response.toString());
                        dismiss();

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
