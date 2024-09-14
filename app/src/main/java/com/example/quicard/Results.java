package com.example.quicard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Results extends AppCompatActivity {

    ImageButton btnClose;
    Button btnResults;
    TextView iknowitvalue, stilllearningvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        iknowitvalue = findViewById(R.id.iknowitvalue);
        int counter = getIntent().getIntExtra("knowint", 0);

        //iknowitvalue.setText(String.valueOf(counter));
        iknowitvalue.setText("I KNOW IT                          " + Integer.toString(counter));

        stilllearningvalue = findViewById(R.id.stilllearningvalue);
        int counter1 = getIntent().getIntExtra("learnint", 0);

        stilllearningvalue.setText("STILL LEARNING               " + Integer.toString(counter1));

        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnResults = (Button) findViewById(R.id.btnResults);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Results.this, Home.class));
            }
        });



        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Results.this, MainActivity.class));
            }

        });

    }
}