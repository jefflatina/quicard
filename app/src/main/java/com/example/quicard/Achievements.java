package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Achievements extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button see, createmore;

    DatabaseReference reference, PostsRef;
    FirebaseAuth mAuth;
    String currentUserId;
    TextView MyPosts;

    int countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        see = findViewById(R.id.see);
        createmore = findViewById(R.id.createmore);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.achievements);
        //////////////////////
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        MyPosts = (TextView) findViewById(R.id.num);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Decks");



        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Achievements.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Achievements.this, Create.class);
                startActivity(intent);
                finish();
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
}