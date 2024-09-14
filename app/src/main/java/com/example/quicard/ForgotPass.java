package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityForgotPassBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {
    ActivityForgotPassBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    ImageButton backforgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        backforgotpass = findViewById(R.id.backforgotpass);

        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(ForgotPass.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        /* backforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPass.this, Login.class));
            }
        }); */

        ImageButton back = (ImageButton)findViewById(R.id.backforgotpass);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btndone.setOnClickListener(view -> {
            forgotPassword();
        });
    }

    private Boolean validateEmail(){
        String value = binding.forgotemail.getText().toString();
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (value.isEmpty()){
            binding.forgotemail.setError("Field cannot be empty");
            return false;
        }
        /*else if (!value.matches(emailPattern)){
            binding.forgotemail.setError("Invalid Email Address");
            return false;
        }*/
        else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            binding.forgotemail.setError("Invalid Email Address");
            return false;
        }
        else {
            binding.forgotemail.setError(null);
            return true;
        }
    }

    private void forgotPassword() {

        if (!validateEmail()){
            return;
        }

        dialog.show();

        auth.sendPasswordResetEmail(binding.forgotemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();

                if (task.isSuccessful()){
                    startActivity(new Intent(ForgotPass.this, Login.class));
                    finish();
                    Toast.makeText(ForgotPass.this, "Please check your email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgotPass.this, "Enter correct email id", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}