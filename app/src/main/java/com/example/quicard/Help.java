package com.example.quicard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.quicard.databinding.ActivityAboutBinding;
import com.example.quicard.databinding.ActivityHelpBinding;

public class Help extends AppCompatActivity {

    ActivityHelpBinding binding;
    ImageButton backhelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backhelp = findViewById(R.id.backhelp);
        ImageButton back = (ImageButton)findViewById(R.id.backhelp);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}