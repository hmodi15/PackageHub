package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CounterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button backBtn;     // define back button variable

    Button buttonAdd, buttonSub, buttonMul, buttonDiv;
    EditText editTextN1, editTextN2;
    TextView textView;
    int num1, num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        // initialize the buttons and text boxes
        buttonAdd = findViewById(R.id.btn_add);
        buttonSub = findViewById(R.id.btn_sub);
        buttonMul = findViewById(R.id.btn_mul);
        buttonDiv = findViewById(R.id.btn_div);
        editTextN1 = findViewById(R.id.number1);
        editTextN2 = findViewById(R.id.number2);
        textView = findViewById(R.id.answer);

        //makes buttons call the onClick function
        buttonAdd.setOnClickListener(this);
        buttonSub.setOnClickListener(this);
        buttonMul.setOnClickListener(this);
        buttonDiv.setOnClickListener(this);
        backBtn = findViewById(R.id.counter_back_btn);






/*
        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public int getIntFromEditText(EditText editText){
        if(editText.getText().toString().equals("")){
            Toast.makeText(this, "Enter a number", Toast.LENGTH_SHORT).show();
            return 0;
        }else {
            // parsing the integer value from the string value
            return Integer.parseInt(editText.getText().toString());
        }
    }

    @Override
    public void onClick(View view) {
        num1 = getIntFromEditText(editTextN1);
        num2 = getIntFromEditText(editTextN2);

        // doing the calculation based on user input and sets the text to the correct answer
        if(view.getId() == R.id.btn_add){
            textView.setText("Answer =  " + (num1  + num2));
        } else if (view.getId() == R.id.btn_sub) {
            textView.setText("Answer =  " + (num1  - num2));
        } else if (view.getId() == R.id.btn_mul) {
            textView.setText("Answer =  " + (num1  * num2));
        } else if (view.getId() == R.id.btn_div) {
            textView.setText("Answer =  " + ((float) num1  / (float) num2));
        }
    }
}