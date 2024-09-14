package com.example.quicard;

import java.util.ArrayList;
import java.lang.reflect.Array;

public class Users {
    public String username, fullname, dateofbirth, sex, course, email, profileurl;
    // para sa quicard decks
    public ArrayList<String> myDecks = new ArrayList<>();

    public Users() {
    }

    //nagdagdag ako rito ng mydecks
    public Users(String username, String fullname, String dateofbirth, String sex, String course, String email, String profileurl, ArrayList<String> myDecks) {
        this.username = username;
        this.fullname = fullname;
        this.dateofbirth = dateofbirth;
        this.sex = sex;
        this.course = course;
        this.email = email;
        this.profileurl = profileurl;
        //this.password = password;
        this.myDecks = myDecks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }


    /* public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } */

    //for decks
    public ArrayList<String> getmyDecks() {
        return myDecks;
    }

    public void setMyDecks(ArrayList<String> username) {
        this.myDecks = username;
    }
}
