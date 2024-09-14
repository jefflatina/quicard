package com.example.quicard;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewCard extends AppCompatActivity implements View.OnClickListener {

    private List<List<String>> cards = new ArrayList<>();
    private TextView tvTitle, tvCourse, tvCard;
    private List currCard;
    private boolean front = true;
    private static int f = 0, b = 1;
    private Deck thisDeck;
    private int inc = 0;

    TextView showValue;
    TextView showValue1;
    int counter = 0;
    int counter1 = 0;

    ImageButton btnClose;
    Button button;
    Button button2;

    ImageButton texttospeech;

    TextToSpeech t1;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);

        tvTitle = (TextView) findViewById(R.id.tvCardTitle);
        tvCourse = (TextView) findViewById(R.id.tvCardCourse);
        tvCard = (TextView) findViewById(R.id.tvCard);
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        showValue = (TextView) findViewById(R.id.counterValue);
        showValue1 = (TextView) findViewById(R.id.counterValue1);

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR);
                t1.setLanguage(Locale.ENGLISH);
            }
        });
        texttospeech = findViewById(R.id.texttospeech);
        tvCard = findViewById(R.id.tvCard);

        texttospeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            String text = tvCard.getText().toString();
            t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });



        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        //slide_in_left = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
        //slide_out_left = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);


        try {
            thisDeck = (Deck) getIntent().getSerializableExtra("Deck");
            cards = thisDeck.getCards();
            currCard = cards.get(inc);
            tvTitle.setText(thisDeck.title);
            tvCourse.setText(thisDeck.course);
            tvCard.setText(currCard.get(f).toString());
            //tvCard.setTypeface(Typeface.DEFAULT_BOLD);
        }
        catch(Exception e){
            Log.d("debug", "Empty deck");
        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                front = true;
                inc++;
                //tvCard.setBackgroundColor(Color.parseColor("#A3FFFFFF"));
                //tvCard.setTypeface(Typeface.DEFAULT_BOLD);
                tvCard.setBackground(getDrawable(R.drawable.whitebg2));
                counter++;
                showValue.setText(Integer.toString(counter));



                try {
                    currCard = cards.get(inc);
                    tvCard.setText(currCard.get(f).toString());
                } catch(Exception e) {
                    inc = 0;
                    currCard = cards.get(inc);
                    //tvCard.setText(currCard.get(f).toString());
                    //startActivity(new Intent(ViewCard.this, Results.class));
                    Intent i = new Intent(ViewCard.this, Results.class);
                    i.putExtra("knowint", counter);
                    i.putExtra("learnint", counter1);
                    startActivity(i);

                    // Toast.makeText(ViewCard.this,"Done", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                front = true;
                inc++;
                //tvCard.setBackgroundColor(Color.parseColor("#A3FFFFFF"));
                //tvCard.setTypeface(Typeface.DEFAULT_BOLD);
                tvCard.setBackground(getDrawable(R.drawable.whitebg2));
                counter1++;
                showValue1.setText(Integer.toString(counter1));



                try {
                    currCard = cards.get(inc);
                    tvCard.setText(currCard.get(f).toString());
                } catch(Exception e) {
                    inc = 0;
                    currCard = cards.get(inc);
                    //tvCard.setText(currCard.get(f).toString());
                    //startActivity(new Intent(ViewCard.this, Results.class));
                    Intent i1 = new Intent(ViewCard.this, Results.class);
                    i1.putExtra("learnint", counter1);
                    i1.putExtra("knowint", counter);
                    startActivity(i1);


                    // Toast.makeText(ViewCard.this,"Done", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvCard.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onClick(){
                if (front){
                    //viewFlipper.setInAnimation(slide_in_right);
                    //viewFlipper.setOutAnimation(slide_out_left);
                    front = false;
                    tvCard.setText(currCard.get(b).toString());
                    //tvCard.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    tvCard.setBackground(getDrawable(R.drawable.whitebg2));

                    //tvCard.setTypeface(Typeface.DEFAULT);

                    Log.d("dhk", currCard.get(b).toString());
                }
                else{
                    front = true;
                    // tvCard.setTypeface(Typeface.DEFAULT_BOLD);
                    //tvCard.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    tvCard.setBackground(getDrawable(R.drawable.whitebg2));

                    tvCard.setText(currCard.get(f).toString());
                    Log.d("dhk", currCard.get(f).toString());
                }
            }
        });

    }

    @Override
    public void onClick(View v){

        switch (v.getId()){
        }
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }
        public void onClick() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick();
                return super.onSingleTapUp(e);
            }
        }
    }
}