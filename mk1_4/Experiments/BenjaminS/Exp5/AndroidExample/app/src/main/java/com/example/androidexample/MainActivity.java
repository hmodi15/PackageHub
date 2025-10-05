package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button strBtn, jsonObjBtn, jsonArrBtn, imgBtn, testBtn;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.testView);

        strBtn = findViewById(R.id.btnStringRequest);
        jsonObjBtn = findViewById(R.id.btnJsonObjRequest);
        jsonArrBtn = findViewById(R.id.btnJsonArrRequest);
        imgBtn = findViewById(R.id.btnImageRequest);
        testBtn = findViewById(R.id.btnTestRequest);

        /* button click listeners */
        strBtn.setOnClickListener(this);
        jsonObjBtn.setOnClickListener(this);
        jsonArrBtn.setOnClickListener(this);
        imgBtn.setOnClickListener(this);
        testBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int id = v.getId();
        String url = "https://api.json-generator.com/templates/s3xaLRwHpIbC/data";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("resident");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        int age = jsonObject.getInt("age");

                        textView.append(name + " " + email + " " + String.valueOf(age) + "\n" );


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
        if (id == R.id.btnStringRequest) {
            startActivity(new Intent(MainActivity.this, StringReqActivity.class));
        } else if (id == R.id.btnJsonObjRequest) {
            startActivity(new Intent(MainActivity.this, JsonObjReqActivity.class));
        } else if (id == R.id.btnJsonArrRequest) {
            startActivity(new Intent(MainActivity.this, JsonArrReqActivity.class));
        } else if (id == R.id.btnImageRequest) {
            startActivity(new Intent(MainActivity.this, ImageReqActivity.class));
        }
    }
}