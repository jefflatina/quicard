package com.example.quicard;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeckListAdapter extends ArrayAdapter<Deck> {

    Context ctx;
    boolean isGuest;
    List<Deck> deck;

    public DeckListAdapter(boolean isGuest, Context ctx, int resource, List<Deck> deck) {
        super( ctx, resource, deck);
        this.ctx = ctx;
        this.deck = deck;
        this.isGuest = isGuest;
    }

    @NonNull
    @Override
    public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View v = inflater.inflate(R.layout.deck_lv_item, null);

        //TextView creator = (TextView) v.findViewById(R.id.tvDeckAuthor);
        TextView course = (TextView) v.findViewById(R.id.tvDeckCourse);
        TextView title = (TextView) v.findViewById(R.id.tvDeckName);

        Deck myDeck = deck.get(position);

        //creator.setText("by " + myDeck.getCreator());
        course.setText(myDeck.getCourse());
        title.setText(myDeck.getTitle());

        return v;
    }

}
