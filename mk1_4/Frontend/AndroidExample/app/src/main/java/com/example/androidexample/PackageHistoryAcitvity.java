package com.example.androidexample;

import static com.example.androidexample.R.color.purple_200;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;




public class PackageHistoryAcitvity extends AppCompatActivity implements PackageInteractionListener {

    private PackageAdapter adapter;
    private List<Package> packageList = new ArrayList<>();
    private TextView placeholderTextView;
    FirebaseAuth auth;
    FirebaseUser user;
    private TabLayout tabLayout;
    int selectedTabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_history_acitvity);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.home) {

            } else if (item.getItemId() == R.id.profile) {
                // Switch to ProfileActivity
                intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("selectedItemId", R.id.profile);
                intent.putExtra("username", userName);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("postCode", postCode);
                intent.putExtra("address", address);
                startActivity(intent);
                // Return true to indicate the item selection is handled
                return true;
            }else if(item.getItemId() == R.id.occupents){
                intent = new Intent(this, SocialMediaActivity.class);
                startActivity(intent);
            }

            return true;
        });



        selectedTabPosition = 0;
        tabLayout = findViewById(R.id.tablLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Packages"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        Objects.requireNonNull(tabLayout.getTabAt(1)).view.setBackgroundColor(getResources().getColor(purple_200));
        Objects.requireNonNull(tabLayout.getTabAt(1)).view.setBackgroundColor(getResources().getColor(purple_200));
        tabLayout.selectTab(tabLayout.getTabAt(1));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedPosition = tab.getPosition();
                if(selectedPosition == 0){
                    Intent intenet = new Intent(PackageHistoryAcitvity.this, HomeActivity.class);
                    startActivity(intenet);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int selectedPosition = tab.getPosition();
                if(selectedPosition == 0){
                    Intent intenet = new Intent(PackageHistoryAcitvity.this, HomeActivity.class);
                    startActivity(intenet);
                }
            }
        });



        //Recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackages);
        adapter = new PackageAdapter(packageList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getDataFromServer(user.getEmail());


    }


    private void getDataFromServer(String name) {
        String url = "http://coms-309-019.class.las.iastate.edu:8080/users/packages/" + name;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String occupantName = jsonObject.isNull("name") ? "Unknown" : jsonObject.getString("name");
                                String pickUpCode = jsonObject.getString("pickUpCode");
                                String deliveryDate = jsonObject.getInt("scanYear") + "-" +
                                        jsonObject.getInt("scanMonth") + "-" +
                                        jsonObject.getInt("scanDate");
                                int id = jsonObject.getInt("id");
                                boolean pickUpStatus = jsonObject.getBoolean("pickUpStatus");

                                // only display packages that have already been picked up
                                if(pickUpStatus){
                                    packageList.add(new Package(id,occupantName, deliveryDate, pickUpCode, true));
                                }




                            }
                            adapter.notifyDataSetChanged();

                            if(packageList.isEmpty()){
                                createPlaceholderTextView();
                            }else{
                                removePlaceholderTextView();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }




    /**
     * This method is used to delete a package from the server.
     * It sends a DELETE request to the server and removes the package from the packageList.
     * @param packageId The id of the package to be deleted.
     * @param position The position of the package in the packageList.
     */
    public void deletePackageFromServer(int packageId, final int position) {
        String deleteUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + packageId;
        Log.d("DeletePackageRequest", "Request URL: " + deleteUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    // Handle successful delete
                    // Remove the item from your list and notify the adapter
                    packageList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(PackageHistoryAcitvity.this, "Package deleted successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error deleting package. " + (error != null ? error.getMessage() : "Unknown error");
                    //Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("DeletePackageError", errorMessage, error);
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * This method is called when a package is deleted.
     * It deletes the package from the server and from the packageList.
     * @param packageId The id of the package to be deleted.
     * @param position The position of the package in the packageList.
     */
    @Override
    public void onDeletePackage(int packageId, int position) {
        deletePackageFromServer(packageId, position);
        deletePackage(position);
    }

    public void deletePackage(int position){
        if(position >= 0 && position < packageList.size()){
            packageList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    private void createPlaceholderTextView() {

        placeholderTextView = new TextView(this);
        placeholderTextView.setText("Package History Will Show Up Here");
        placeholderTextView.setTextSize(20);
        placeholderTextView.setTextColor(getResources().getColor(android.R.color.black));

        placeholderTextView.setTypeface(null, Typeface.BOLD);


        ViewGroup rootLayout = findViewById(android.R.id.content);


        placeholderTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        placeholderTextView.setGravity(Gravity.CENTER);


        rootLayout.addView(placeholderTextView);


        int topMargin = rootLayout.getHeight() / 8;
        ((ViewGroup.MarginLayoutParams) placeholderTextView.getLayoutParams()).topMargin = topMargin;
    }


    private void removePlaceholderTextView() {
        if (placeholderTextView != null && placeholderTextView.getParent() != null) {
            ((ViewGroup) placeholderTextView.getParent()).removeView(placeholderTextView);
            placeholderTextView = null;
        }
    }
}