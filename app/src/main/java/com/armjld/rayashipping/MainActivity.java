package com.armjld.rayashipping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button2;
    EditText editTextTextPersonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button2 = findViewById(R.id.button2);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);

        button2.setOnClickListener(v-> {
            Toast.makeText(this, editTextTextPersonName.getText().toString(), Toast.LENGTH_SHORT).show();
        });

    }
}