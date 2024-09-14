package com.example.quicard;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck implements Serializable {
    String title, creator, course, Uid, deckId;
    List<List<String>> cards = new ArrayList<>();

    public Deck(){

    }

    public Deck (String deckId, String Uid, String title, String course, String creator, List<List<String>> cards){
        this.deckId = deckId;
        this.title = title;
        this.creator = creator;
        this.course = course;
        this.cards = cards;
        this.Uid = Uid;
    }

    public String getTitle() {
        return title;
    }
    public String getCreator() {
        return creator;
    }
    public String getCourse() { return course; }
    public String getDeckId() {return deckId;}
    public String getUid(){
        return Uid;
    }
    public List<List<String>> getCards(){
        return cards;
    }
}
