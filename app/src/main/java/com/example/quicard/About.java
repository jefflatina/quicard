package com.example.quicard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.quicard.databinding.ActivityAboutBinding;
import com.example.quicard.databinding.ActivityForgotPassBinding;

public class About extends AppCompatActivity {

    ActivityAboutBinding binding;
    ImageButton backabout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backabout = findViewById(R.id.backabout);
        ImageButton back = (ImageButton)findViewById(R.id.backabout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}