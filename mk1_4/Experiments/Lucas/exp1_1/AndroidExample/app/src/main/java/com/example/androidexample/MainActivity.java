package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private Button mainButton;
    private Button upgradeButton;
    private String buttonPressText = "Clicked:";
    private int clickCount = 0;
    private int clickGain = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        mainButton = findViewById(R.id.main_button);
        upgradeButton = findViewById(R.id.upgrade_button);
        messageText.setText("Howdy!");

        /* click listener on counter button pressed */
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When button clicked, change text to corresponding value
                clickCount += clickGain;
                if(clickCount % 2 == 1) {
                    messageText.setTextColor(0xFFFF0000);
                    messageText.setText(buttonPressText + " " + clickCount);
                }
                else{
                    messageText.setTextColor(0xFF117711);
                    messageText.setText(buttonPressText + " " + clickCount);
                }
            }
        });

        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCount >= 50) {
                    clickGain *= 2;
                    messageText.setTextColor(0xFF00FF00);
                    messageText.setText("Upgraded gain! Gain: " + " " + clickGain);
                    clickCount -= 50;
                }
                else{
                    messageText.setTextColor(0xFFFF0000);
                    messageText.setText("Not enough clicks!");
                }
            }
        });
    }
}