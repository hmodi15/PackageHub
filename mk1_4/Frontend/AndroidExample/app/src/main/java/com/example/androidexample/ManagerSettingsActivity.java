package com.example.androidexample;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ManagerSettingsActivity extends AppCompatActivity {


    ImageView backView;
    Dialog dialog;
    Button btnDialogCancel, btnDialogDelete;
    TextView deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);

        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String managerScreenStartActiveView = getIntent().getStringExtra("startActiveView");

        deleteAccount = findViewById(R.id.deleteAccountBtn);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        backView = findViewById(R.id.backFromSettingsBtn);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerSettingsActivity.this, ManagerDashboardActivity.class);
                intent.putExtra("startActiveView", managerScreenStartActiveView);
                startActivity(intent);
            }
        });

        dialog = new Dialog(ManagerSettingsActivity.this);
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

        btnDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount(userName);
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
                        Toast.makeText(ManagerSettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        // Redirect to login page or any other appropriate action
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Toast.makeText(ManagerSettingsActivity.this, "Error deleting account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(deleteRequest);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openProfile(String email, String name, String phone, String address, String postalCode){
        Intent intent = new Intent(this, ManagerDashboardActivity.class);
        startActivity(intent);
    }
}