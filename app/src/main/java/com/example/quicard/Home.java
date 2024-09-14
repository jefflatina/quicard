package com.example.quicard;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quicard.databinding.ActivityHomeBinding;
import com.example.quicard.databinding.ActivityMainBinding;
import com.example.quicard.databinding.ActivityUpdateProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity{

    ActivityHomeBinding binding;

    BottomNavigationView bottomNavigationView;
    ImageView imageView11, imageView12, imageView13, imageView8;

    DatabaseReference reference;
    DatabaseReference referenceProfile;
    FirebaseAuth authProfile;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =authProfile.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        showMiniProfile(firebaseUser);

        ImageSlider imageSlider = findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.pic1));
        slideModels.add(new SlideModel(R.drawable.pic2));
        slideModels.add(new SlideModel(R.drawable.pic3));

        imageSlider.setImageList(slideModels, true);

        //image clickable
        imageView8 = findViewById(R.id.profilepichome);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        imageView13 = findViewById(R.id.imageView13);

        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Profile.class);
                startActivity(intent);
            }
        });

        imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Create.class);
                startActivity(intent);
            }
        });

        imageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Achievements.class);
                startActivity(intent);
            }
        });

        imageView13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.home:
                        return true;

                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.create:
                        startActivity(new Intent(getApplicationContext(),Create.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.achievements:
                        startActivity(new Intent(getApplicationContext(),Achievements.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
    private void showMiniProfile(FirebaseUser firebaseUser) {
        userID = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails.profileurl.equals("")){
                    binding.profilepichome.setImageResource(R.drawable.jinx);
                }
                else{
                    //Uri uri = firebaseUser.getPhotoUrl();
                    //Set use's current DP in ImageView(if uploaded already.) We will Picasso since imageviewer setImage
                    //Regular URI's
                    Picasso.get().load(readUserDetails.profileurl).into(binding.profilepichome);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}