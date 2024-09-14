package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyQuicards extends AppCompatActivity implements View.OnClickListener{

    ArrayList<Deck> allDecks = new ArrayList<>();
    ArrayList<String> myDeckKeys = new ArrayList<>();
    Map<String, Deck> keyedDecks = new HashMap<>();
    ArrayList<Deck> personalDecks = new ArrayList<>();
    ListView lvDecks;

    private boolean isGuest = false;
    private boolean inPublic = true;

    FirebaseDatabase rootRef;
    FirebaseAuth mAuth;

    ImageButton btnClose2;

    private ImageButton myDecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quicards);

        rootRef = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //check if current user is a guest:
        isGuest = checkGuest();

        if ((ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks") != null) {
            personalDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks");
        }

        if ((ArrayList<Deck>) getIntent().getSerializableExtra("allDecks") != null){
            allDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("allDecks");
        }

        if (!isGuest) {
            DatabaseReference thisUser = rootRef.getReference("Users").child(mAuth.getCurrentUser().getUid())
                    .child("MyDecks");
            getUserData(thisUser, new OnGetDataListener() {

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onFailure() {
                }
            });
        }

        btnClose2 = (ImageButton) findViewById(R.id.btnClose2);
        btnClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyQuicards.this, Home.class));
            }
        });


        //get the users decks
        if (!isGuest) {
            Log.d("chk_guest", "not guest");
        }


        myDecks = (ImageButton) findViewById(R.id.tvMyDecks);
        lvDecks = (ListView) findViewById(R.id.lvDecksPublic);

        myDecks.setOnClickListener(this);


        try{
            inPublic = getIntent().getBooleanExtra("isPublic", false);
        }
        catch(Exception e){
        }

        if ((ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks") != null) {
            personalDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("personalDecks");
        }



        if (inPublic){
            inPublic = false;
            MyDeckListAdapter adapter = new MyDeckListAdapter(isGuest,this, R.layout.mydeck_lv_item, personalDecks);
            lvDecks.setAdapter(adapter);
            switchToMine();

        }
        else{
            inPublic = false;
            MyDeckListAdapter adapter = new MyDeckListAdapter(isGuest,this, R.layout.deck_lv_item, personalDecks);
            lvDecks.setAdapter(adapter);
            switchToMine();


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

                Intent i = new Intent(MyQuicards.this, ViewCard.class);
                i.putExtra("Deck", sendDeck.get(position));
                startActivity(i);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v){

        switch (v.getId()){

            case R.id.tvMyDecks:

                if (isGuest){
                    Toast.makeText(MyQuicards.this, "You must be signed in.", Toast.LENGTH_LONG).show();
                }
                else {
                    //switch decks
                    inPublic = false;
                    MyDeckListAdapter adapter = new MyDeckListAdapter(isGuest,this, R.layout.deck_lv_item, personalDecks);
                    lvDecks.setAdapter(adapter);
                    switchToMine();
                }
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

    public void switchToMine(){
        inPublic = false;
    }

    public void getUserData(DatabaseReference thisUser, final OnGetDataListener listener){


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

    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);
        void onFailure();
    }
}