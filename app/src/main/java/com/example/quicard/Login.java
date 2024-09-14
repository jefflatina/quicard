package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;

    String email, password;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseAuth authProfile;
    public static final String SHARED_PREFS = "sharedPrefs";
    //
    private ArrayList<Deck> allDecks = new ArrayList<>();

    private String passwordFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkBox();


        //
        allDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("allDecks");

        //Show hide password using Eye icon
        ImageView imageViewShowHidePwd = binding.imgviewshowhidepassword;
        imageViewShowHidePwd.setImageResource(R.drawable.eyecross);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if password visible then hide
                    binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.eyecross);
                }
                else {
                    //para mashow palitan pic
                    binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.eyeopen );
                }
            }
        });



        //redirect sa forgot password page
        binding.txtforgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPass.class));
            }
        });

        //redirect sa signup page
        binding.btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });


        //login function
        binding.btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = binding.email.getText().toString();
                password = binding.password.getText().toString();

                //condition para makalogin
                if (email.isEmpty()){ //username.isEmpty() || password.isEmpty()
                    Toast.makeText(Login.this, "Please fill in the username", Toast.LENGTH_SHORT).show();
                    binding.email.setError("Username required");
                    binding.email.requestFocus();
                }
                else if (password.isEmpty()){
                    Toast.makeText(Login.this, "Please fill in the password", Toast.LENGTH_SHORT).show();
                    binding.password.setError("Password required");
                    binding.password.requestFocus();
                }
                //check kung nasa database na ba yung email or username
                else {
                    //checkUser();
                    login(email, password);
                }
            }
        });
    }

    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check= sharedPreferences.getString("name", "");
        if (check.equals("true"))
        {
            //Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Login.this, Home.class);
            i.putExtra("allDecks", allDecks); //////////////////////delete
            startActivity(i);
            finish();
        }
    }

    private void login(String email, String password) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", "true");
                    editor.apply();

                    authProfile.getCurrentUser();
                    //Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                    // startActivity(new Intent(Login.this, MainActivity.class));
                    Intent i = new Intent(Login.this, Home.class);
                    i.putExtra("allDecks", allDecks);
                    startActivity(i);
                    // finish();
                } else {
                    authProfile.signOut();
                    Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*

    //function para macheck yung user
    private void checkUser() {
        String userUsername = binding.username.getText().toString();
        String userPassword = binding.password.getText().toString();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(username);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (Objects.equals(passwordFromDB, userPassword)){
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                    else {
                        Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Login.this, "The login details you entered are incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } */
}