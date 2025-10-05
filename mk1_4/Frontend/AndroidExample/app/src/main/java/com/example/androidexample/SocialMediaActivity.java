package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialMediaActivity extends AppCompatActivity implements WebSocketListener, MediaPostInteractionListener{
    private Button sendBtn;
    private Button cameraBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private ImageView imageTest;
    private MediaPostAdapter adapter;
    private List<MediaPost> postList = new ArrayList<>();
    private int postID = 0; //TEMPORARY!!!
    private BottomNavigationView bottomNav;

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
        setContentView(R.layout.activity_social_media);

        Bundle extras = getIntent().getExtras();
        String imgFilepath = "";
        if(extras != null){
            try {
                imgFilepath = extras.getString("imgFilepath");
            }
            catch(Exception e){
                imgFilepath = "";
            }
        }

        RecyclerView recyclerView = findViewById(R.id.postRecycler);
        adapter = new MediaPostAdapter(postList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        msgTv = findViewById(R.id.tx1);

        WebSocketManager.getInstance().connectWebSocket("ws://coms-309-019.class.las.iastate.edu:8080/chat/user1");

        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        cameraBtn = (Button) findViewById(R.id.camBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        imageTest = (ImageView) findViewById(R.id.imageView);

        //bottom nav stuffs
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.occupents);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.home) {
                // Handle switching to HomeActivity if needed
                intent = new Intent(SocialMediaActivity.this, HomeActivity.class);

            }
            else if (item.getItemId() == R.id.occupents) {
                // Switch to ProfileActivity
                intent = new Intent(SocialMediaActivity.this, SocialMediaActivity.class);
                intent.putExtra("scan_type", "scan");
            }
            else if(item.getItemId() == R.id.profile){
                intent = new Intent(SocialMediaActivity.this, UserProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                // Return true to indicate the item selection is handled
                return true;
            } else {
                // Return false if the item selection is not handled
                return false;
            }
        });

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(SocialMediaActivity.this);

        //if we have an image that was passed in from scanner, post it
        Bitmap imgBitmap;
        if(imgFilepath.length() > 0){
            File imgFile = new File(imgFilepath);
            if(imgFile.exists()) {
                imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
            }
            else {
                imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mitra);
            }
        }
        else {
            //if no image, set to default for the time being
            imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mitra);
        }
        imageTest.setImageBitmap(imgBitmap);
        /* send button listener */
        Bitmap finalImgBitmap = imgBitmap;
        sendBtn.setOnClickListener(v -> {
            try {
                // send message
                WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                postList.add(new MediaPost(postID, msgEtx.getText().toString(), "", "user1", 1, finalImgBitmap));
                postID += 1;
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebSocketManager.getInstance().disconnectWebSocket();
                Intent intent = new Intent(SocialMediaActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            if(message.contains("http")) {
                String containedUrls = "";
                String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
                Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
                Matcher urlMatcher = pattern.matcher(message);

                //gets the last url in the message
                while (urlMatcher.find()) {
                    containedUrls = (message.substring(urlMatcher.start(0), urlMatcher.end(0)));
                }
                try{
                    Picasso.get().load(containedUrls).into(imageTest);//this fails if not image
                }
                catch(Exception e){
                    String s = msgTv.getText().toString();
                    msgTv.setText(s + "\n"+message);
                }
            }
            else{
                //String s = msgTv.getText().toString();
                //msgTv.setText(s + "\n"+message);
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}

    @Override
    public void onDeletePost(int postId, int position) {
        if(position >= 0 && position < postList.size()){
            postList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void onUpdatePost(int position, String updatedText) {
        if(position >= 0 && position < postList.size()) {
            MediaPost post = postList.get(position);
            post.setPostText(updatedText); //update the text
            postList.remove(position); //remove old
            postList.add(position, post); //add the new where old was
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onLikePost(int postId, int position) {
        if (position >= 0 && position < postList.size()){
            MediaPost post = postList.get(position);
            post.addLike(); //add a like
            postList.remove(position); //remove old
            postList.add(position, post); //add the new where old was
            adapter.notifyItemChanged(position);
        }
    }
}