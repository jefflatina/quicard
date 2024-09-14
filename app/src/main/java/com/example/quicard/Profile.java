package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding binding;

    FirebaseAuth authProfile;
    DatabaseReference reference;
    BottomNavigationView bottomNavigationView;

    String username, fullname, dateofbirth, sex, course, email;
    String userID;

    ImageButton imageButton;
    ImageView createdthumbnail, profilepic;

    DatabaseReference PostsRef;
    FirebaseAuth mAuth;
    String currentUserId;
    TextView MyPosts;
    int countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =authProfile.getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        MyPosts = (TextView) findViewById(R.id.num1);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Decks");

        //profilepic = findViewById(R.id.profilepic);

        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
            }
        });


        if (firebaseUser == null){
            Toast.makeText(Profile.this, "Something went wrong. Please log in again", Toast.LENGTH_SHORT).show();
        }
        else {

            showUserProfile(firebaseUser);
            showMiniProfile(firebaseUser);


        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
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
                        return true;
                }
                return false;
            }
        });

        createdthumbnail = findViewById(R.id.createdthumbnail);
        createdthumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });


        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Settings.class);
                startActivity(intent);
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            countPosts = (int) snapshot.getChildrenCount();
                            MyPosts.setText(Integer.toString(countPosts) + "  QUICARDS");
                        }
                        else
                        {
                            MyPosts.setText("0 QUICARDS");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                    binding.profilepic.setImageResource(R.drawable.jinx);
                }
                else{
                    //Uri uri = firebaseUser.getPhotoUrl();
                    //Set use's current DP in ImageView(if uploaded already.) We will Picasso since imageviewer setImage
                    //Regular URI's
                    Picasso.get().load(readUserDetails.profileurl).into(binding.profilepic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                if (readUserDetails != null){
                    username = readUserDetails.username;
                    fullname = readUserDetails.fullname;
                    dateofbirth = readUserDetails.dateofbirth;
                    sex = readUserDetails.sex;
                    course = readUserDetails.course;
                    email = firebaseUser.getEmail();

                    binding.txtviewUsername.setText(username);
                    binding.txtviewFullname.setText(fullname);
                    binding.txtviewDateofbirth.setText(dateofbirth);
                    binding.txtviewSex.setText(sex);
                    binding.txtviewCourse.setText(course);
                    binding.txtviewEmail.setText(email);
                    
                }
                else {
                    Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Something went wrong. Please log in again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}