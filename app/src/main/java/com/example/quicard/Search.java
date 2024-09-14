package com.example.quicard;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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


import com.example.quicard.databinding.ActivityMainBinding;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search extends AppCompatActivity implements View.OnClickListener{

    BottomNavigationView bottomNavigationView;
    SearchView searchView;
    ListView listView;
    //
    ArrayList<Deck> allDecks = new ArrayList<>();
    ArrayList<String> myDeckKeys = new ArrayList<>();
    Map<String, Deck> keyedDecks = new HashMap<>();
    ArrayList<Deck> personalDecks = new ArrayList<>();
    private ListView lvDecks;

    private boolean isGuest = false;
    private boolean inPublic = true;

    FirebaseDatabase rootRef;
    FirebaseAuth mAuth;

    private TextView publicDecks;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.search);

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


        rootRef = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //check if current user is a guest:
        isGuest = checkGuest();

        if ((ArrayList<Deck>) getIntent().getSerializableExtra("allDecks") != null){
            allDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("allDecks");
        }
        if (!isGuest) {
            DatabaseReference thisUser = rootRef.getReference("Users").child(mAuth.getCurrentUser().getUid())
                    .child("MyDecks");
            getUserData(thisUser, new PublicDecks.OnGetDataListener() {

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onFailure() {
                }
            });
        }


        //get the users decks
        if (!isGuest) {
            Log.d("chk_guest", "not guest");
        }

        publicDecks = (TextView) findViewById(R.id.tvPublicDecks);
        lvDecks = (ListView) findViewById(R.id.lvDecksPublic);


        try{
            inPublic = getIntent().getBooleanExtra("isPublic", true);
        }
        catch(Exception e){
        }

        if ((ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks") != null) {
            personalDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks");
        }



        if (inPublic){
            DeckListAdapter adapter = new DeckListAdapter(isGuest,this, R.layout.deck_lv_item, allDecks);
            lvDecks.setAdapter(adapter);

        }
        else{
            MyDeckListAdapter adapter = new MyDeckListAdapter(isGuest,this, R.layout.mydeck_lv_item, personalDecks);
            publicDecks.setTextAppearance(this, android.R.style.TextAppearance_Material_Body1);
            lvDecks.setAdapter(adapter);

        }

        lvDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Deck> sendDeck = new ArrayList<>();
                if(inPublic){
                    sendDeck = allDecks;
                }
                else{
                    sendDeck = personalDecks;
                }

                Intent i = new Intent(Search.this, ViewCard.class);
                i.putExtra("Deck", sendDeck.get(position));
                startActivity(i);
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    // @Override
    public void onClick(View v){

        switch (v.getId()){

            case R.id.tvPublicDecks:
                inPublic = true;
                publicDecks.setTextAppearance(this, android.R.style.TextAppearance_Large);
                DeckListAdapter adapter = new DeckListAdapter(isGuest,this, R.layout.deck_lv_item, allDecks);

                lvDecks.setAdapter(adapter);
                switchToPublic();
                break;
        }
    }
    public boolean checkGuest(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user==null){
            return true;
        }
        return false;
    }

    public void switchToPublic(){
        inPublic = true;
    }

    public void getUserData(DatabaseReference thisUser, final PublicDecks.OnGetDataListener listener){


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot){
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    //usr = snapshot.getValue(User.class);


                    String hi = snapshot.getValue(String.class);
                    myDeckKeys.add(hi);
                }


                for (Deck d : allDecks){
                    keyedDecks.put(d.deckId, d);
                }
                //find user specific decks:
                for (String s : myDeckKeys){
                    personalDecks.add(keyedDecks.get(s));
                }
                listener.onSuccess(datasnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }
        };
        thisUser.addListenerForSingleValueEvent(valueEventListener);
    }

}