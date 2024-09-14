package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quicard.ChangePassword;
import com.example.quicard.Profile;
import com.example.quicard.UpdateEmail;
import com.example.quicard.UpdateProfile;
import com.example.quicard.UpdateProfilePic;
import com.example.quicard.Users;
import com.example.quicard.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Settings extends AppCompatActivity {
    ActivitySettingsBinding binding;

    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    public static final String SHARED_PREFS = "sharedPrefs";

    private String userID;

    ImageButton btn_back, updateprofilebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn_back = findViewById(R.id.btn_back);
        updateprofilebtn = findViewById(R.id.updateprofilebtn);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser =authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(Settings.this, "Something went wrong. Please log in again", Toast.LENGTH_SHORT).show();
        }
        else {
            showUserProfile(firebaseUser);
        }



        //back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //updateprofile button
        updateprofilebtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Settings.this, UpdateProfilePic.class);
               startActivity(intent);
           }
       });

        //Pagpalit ng profile pic
        binding.imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, UpdateProfilePic.class);
                startActivity(intent);
            }
        });

        //Pagedit ng profile
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, UpdateProfile.class));
            }
        });

        //Pagupdate ng email
        binding.btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, UpdateEmail.class));
            }
        });

        //Pagchange ng password
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, ChangePassword.class));
            }
        });

        //About
        binding.aboutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, About.class));
            }
        });

        //Terms
        binding.termsofservicebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Terms.class));
            }
        });

        //help
        binding.helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Help.class));
            }
        });

        //Paglog out
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();

                authProfile.signOut();
                Toast.makeText(Settings.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, Login.class) ;

                //clear stack to prevent user from coming back after pressing back or logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        userID = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails.profileurl.equals("")){
                    binding.imageButtonProfile.setImageResource(R.drawable.jinx);
                }
                else{
                    //Uri uri = firebaseUser.getPhotoUrl();
                    //Set use's current DP in ImageView(if uploaded already.) We will Picasso since imageviewer setImage
                    //Regular URI's
                    Picasso.get().load(readUserDetails.profileurl).into(binding.imageButtonProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}