package com.example.androidexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

public class SupportChatActivity2 extends AppCompatActivity implements WebSocketListener{



    private TextView sendBtn, backMainBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private RelativeLayout messageLayout;

    @SuppressLint("ClickableViewAccessibility")
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_support_chat2);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /* initialize UI elements */
        messageLayout = findViewById(R.id.messageLayout);
        sendBtn = findViewById(R.id.sendBtn);
        backMainBtn = findViewById(R.id.backMainBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);
        String userName = getIntent().getStringExtra("username");
        String phone = getIntent().getStringExtra("phone");
        String postCode = getIntent().getStringExtra("postCode");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");

        /* connect this activity to the websocket instance */
        WebSocketManager1.getInstance().setWebSocketListener(SupportChatActivity2.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                // send message
                WebSocketManager1.getInstance().sendMessage(msgEtx.getText().toString());
                msgEtx.getText().clear();
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }
        });

        /* back button listener */
        backMainBtn.setOnClickListener(view -> {
            // got to chat activity
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("username", userName);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("address", address);
            intent.putExtra("postCode", postCode);
            intent.putExtra("selectedItemId", R.id.profile);
            startActivity(intent);
        });




        float originalY = messageLayout.getY();

        // Listen for layout changes to adjust the position of the views
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 200) {
                    // Keyboard is shown
                    adjustLayoutPosition(true, heightDiff);
                } else {
                    // Keyboard is hidden
                    adjustLayoutPosition(false, 0);
                }
            }
        });






    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\n"+message);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });


    }


    private void adjustLayoutPosition(boolean keyboardVisible, int keyboardHeight) {
        if (keyboardVisible) {
            // Move the layout up when the keyboard is shown
            messageLayout.setTranslationY(-keyboardHeight);
        } else {
            // Move the layout back to its original position when the keyboard is hidden
            messageLayout.setTranslationY(0);
        }
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}