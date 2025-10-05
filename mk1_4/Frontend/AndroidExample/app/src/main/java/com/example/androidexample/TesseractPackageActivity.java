package com.example.androidexample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

/**
 * @author Lucas Schakel
 * This activity is used to process data from Tesseract.
 * Takes an image as an extra from the scan intent,
 * which is sent to tesseract and processed into fields.
 */
public class TesseractPackageActivity extends AppCompatActivity {
    private EditText nameText;
    private EditText addressText;
    private EditText dateText;
    private EditText securityText;

    private Button addButton;
    private Button randCodeGenerateButton;
    private ImageView imagePreview;
    private LinearLayout loadingPanel;
    private TextView loadingMessage;
    private File imgFile;
    private boolean successfulSend = false;
    private int lastPackageId = 0;
    private int newPackageId = 0;
    private int pollCount = 0;
    private Package newPackage;

    /**
     * This method is called when the activity is starting.
     * It initializes the activity and the UI elements.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesseract_package_handler);

        Bundle extras = getIntent().getExtras();
        String imgFilepath = "";
        if(extras != null){
            imgFilepath = extras.getString("imgFilepath");
        }
        else{
            Toast.makeText(TesseractPackageActivity.this, "No image provided!", Toast.LENGTH_SHORT).show();
        }

        newPackage = new Package(0, "", "", "", false);

        nameText = findViewById(R.id.editTextOccupantName);
        addressText = findViewById(R.id.editTextAddress);
        dateText = findViewById(R.id.editTextDeliveryDate);
        securityText = findViewById(R.id.editTextSecurityCode);
        addButton = findViewById(R.id.buttonAdd);
        randCodeGenerateButton = findViewById(R.id.randCodeGenButton);
        imagePreview = findViewById(R.id.imagePreview);
        loadingPanel = findViewById(R.id.loadingPanel);
        loadingMessage = findViewById(R.id.loadingMessage);
        randCodeGenerateButton = findViewById(R.id.randCodeGenButton);

        //if we have an image that was passed in from scanner, post it
        Bitmap imgBitmap;
        if(imgFilepath.length() > 0){
            imgFile = new File(imgFilepath);
            downscaleAndOverwriteImage(imgFilepath);
            if(imgFile.exists()) {
                imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
            }
            else {
                imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.package_2_fill0_wght400_grad0_opsz24);
                loadingPanel.setVisibility(View.GONE);
                Toast.makeText(TesseractPackageActivity.this, "Invalid image file", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //if no image, set to default for the time being
            imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.package_2_fill0_wght400_grad0_opsz24);
        }
        imagePreview.setImageBitmap(imgBitmap);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePackageOnServer(newPackage);
            }
        });

        randCodeGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                int randNum = rand.nextInt(10000);
                newPackage.setSecurityCode("" + randNum);
                securityText.setText(newPackage.getSecurityCode());
            }
        });

        nameText.setText("Lucas Schakel");
        addressText.setText("455 RICHARDSON CT");
        dateText.setText("2024-05-01");
        securityText.setText("84MK6");

        getLastPackageEntry(); //starts the chain of data sending/recieving
    }

    /**
     * This method is used to update the occupant name of a package on the server.
     * It sends a PUT request to the server with the updated name.
     * @param updatedPackage the package to update
     */
    private void updatePackageOnServer(Package updatedPackage) {
        String updateUrl = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + newPackageId;
        //Toast.makeText(TesseractPackageActivity.this, updateUrl, Toast.LENGTH_SHORT).show();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", updatedPackage.getOccupantName());
            requestBody.put("id", updatedPackage.getId());
            requestBody.put("pickUpStatus", updatedPackage.getPickUpStatus());
            requestBody.put("pickUpCode", updatedPackage.getSecurityCode());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(TesseractPackageActivity.this, "Failed to create package", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    // Handle successful update
                    openManagerDashboard();
                },
                error -> {
                    // Handle error
                    String errorMessage = "Error updating package" + (error != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(TesseractPackageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("UpdateOccupantNameError", errorMessage, error);
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

    /**
     * This method is used to navigate to the home page.
     * It starts the HomeActivity and finishes the current activity.
     */
    private void openManagerDashboard() {
        Intent intent = new Intent(this, ManagerDashboardActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Websocket to send the image taken to Tesseract
     * @param imageFile
     */
    public void sendImageFile(File imageFile) {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            okhttp3.RequestBody requestBody = new okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart("image", imageFile.getName(),
                            okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://coms-309-019.class.las.iastate.edu:8080/packages/OCR")
                    .post(requestBody)
                    .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                onFailToSend("call failure");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailToSend(response.toString());
                    Log.e("okhttp3", response.toString());
                }
                else {
                    onSuccessfulSend();
                }
            }
        });
    }

    public void onFailToSend(String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TesseractPackageActivity.this, "Failed to send image: " + reason, Toast.LENGTH_SHORT).show();
                loadingPanel.setVisibility(View.GONE);
            }
        });
    }

    public void onSuccessfulSend() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingMessage.setText("Image sent successfully, waiting for data...");
                pollForData();
            }
        });
    }

    public void onSuccessfulOCR(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TesseractPackageActivity.this, "Processed image successfully!", Toast.LENGTH_SHORT).show();
                getNewPackageFromServer();
                loadingPanel.setVisibility(View.GONE);
            }
        });
    }

    public void pollForData() {
        final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://coms-309-019.class.las.iastate.edu:8080/packages/" + newPackageId)
                .build();

        final okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // If the response is not successful, schedule the next poll.
                    if(pollCount < 10) {
                        scheduleNextPoll(this);
                    }
                    else{
                        onSuccessfulOCR();
                    }
                }
                //look for a somewhat filled response
                else if(response.message().isEmpty()){
                    Log.i("pollForData_polling", response.message() + newPackageId);
                    if(pollCount < 10) {
                        scheduleNextPoll(this);
                    }
                    else{
                        onSuccessfulOCR();
                    }
                }
                else{
                    Log.i("pollForData_gotData", response.message());
                    onSuccessfulOCR();
                }
            }
        };

        // Start the polling process.
        client.newCall(request).enqueue(callback);
    }

    private void scheduleNextPoll(final okhttp3.Callback callback) {
        pollCount += 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingMessage.setText("Image sent successfully, waiting for data... Poll: " + pollCount);
            }
        });
        final long POLL_INTERVAL = 2000; // Poll every 2 seconds.

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://coms-309-019.class.las.iastate.edu:8080/packages/" + lastPackageId)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        }, POLL_INTERVAL);
    }

    public void getLastPackageEntry() {
        final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://coms-309-019.class.las.iastate.edu:8080/packages")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                onFailLastPackageID();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //throw new IOException("Unexpected code " + response);
                    onFailLastPackageID();
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        JSONObject lastEntry = jsonArray.getJSONObject(jsonArray.length() - 1);
                        lastPackageId = lastEntry.getInt("id");
                        newPackageId = lastPackageId + 1;
                        onSuccessLastPackageID();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getLastPackageEntry", e.getStackTrace().toString());
                    }
                }
            }
        });
    }

    public void onSuccessLastPackageID(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(TesseractPackageActivity.this, "Last package got, sending image! ID:" + newPackageId, Toast.LENGTH_SHORT).show();
                loadingMessage.setText("New package ID: " + newPackageId);
                sendImageFile(imgFile);
            }
        });
    }

    public void onFailLastPackageID(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TesseractPackageActivity.this, "Failed to get packages", Toast.LENGTH_SHORT).show();
                loadingPanel.setVisibility(View.GONE);
            }
        });
    }

    private void getNewPackageFromServer() {
        String url = "http://coms-309-019.class.las.iastate.edu:8080/packages/" + newPackageId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String occupantName = jsonObject.isNull("name") ? "Unknown" : jsonObject.getString("name");
                            String pickUpCode = jsonObject.getString("pickUpCode");
                            String deliveryDate = jsonObject.getInt("scanYear") + "-" + jsonObject.getInt("scanMonth") + "-" + jsonObject.getInt("scanDate");
                            int id = jsonObject.getInt("id");
                            newPackage.setOccupantName(occupantName);
                            newPackage.setPickUpStatus(false);
                            newPackage.setDeliveryDate(deliveryDate);
                            newPackage.setSecurityCode(pickUpCode);
                            onSuccessfulGetNewPackage();
                        }
                        catch (Exception e) {
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

    public void onSuccessfulGetNewPackage(){
        //update all of the edit text fields
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(newPackage.getOccupantName().length() > 0) {
                    nameText.setText(newPackage.getOccupantName());
                    dateText.setText(newPackage.getDeliveryDate());
                    securityText.setText(newPackage.getSecurityCode());
                }
                else{
                    newPackage.setOccupantName("Lucas Schakel");
                    nameText.setText("Lucas Schakel");
                    dateText.setText("2024-04-06");
                    securityText.setText("184123");
                }
            }
        });
    }

    public void downscaleAndOverwriteImage(String imagePath) {
        // Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // The new size we want to scale to
        final int REQUIRED_SIZE = options.outWidth / 4;

        // Find the correct scale value. It should be a power of 2.
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = scale;
        Bitmap downscaledBitmap = BitmapFactory.decodeFile(imagePath, options2);

        // Overwrite the original file
        try (FileOutputStream out = new FileOutputStream(imagePath)) {
            downscaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

