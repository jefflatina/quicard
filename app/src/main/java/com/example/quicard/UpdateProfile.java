package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class UpdateProfile extends AppCompatActivity {

    ActivityUpdateProfileBinding binding;

    DatabaseReference referenceProfile;
    FirebaseAuth authProfile;
    DatabaseReference reference;

    ImageButton btn_back2, updateprofilebtn2;

    String username, fullname, dateofbirth, sex, course, email, password;
    String sex2, course2;
    String userID;
    private RadioGroup radioGroupUpdateGenderSelected;
    private RadioButton radioButtonUpdateGenderSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =authProfile.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        updateprofilebtn2 = findViewById(R.id.updateprofilebtn2);

        //show current user profile
        showProfile(firebaseUser);
        showMiniProfile(firebaseUser);

        //setting date picker on edit text
        binding.signdateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSADoB[] = dateofbirth.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1; //take care of month index
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //date picker dialog
                picker = new DatePickerDialog(UpdateProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        binding.signdateofbirth.setText(day + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);
                picker.show();
            }
        });

        //spinner kukunin selected item for sex
        binding.signsex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sex2 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //spinner kukunin selected item for course

        binding.signcourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                course2 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //updateprofile button
        updateprofilebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfile.this, UpdateProfilePic.class);
                startActivity(intent);
            }
        });

        //Update profile
        binding.btnupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });

        //Pagpalit ng profile pic
        binding.imageButtonProfile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfile.this, UpdateProfilePic.class);
                startActivity(intent);
            }
        });



    }

    private void updateProfile(FirebaseUser firebaseUser) {
        if (username.isEmpty()){
            Toast.makeText(UpdateProfile.this, "Please enter your username", Toast.LENGTH_SHORT).show();
            binding.signusername.setError("Username required");
        }
        else if (fullname.isEmpty()){
            Toast.makeText(UpdateProfile.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            binding.signfullname.setError("Full name is required");
        }
        else if (dateofbirth.isEmpty()){
            Toast.makeText(UpdateProfile.this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            binding.signdateofbirth.setError("Date of birth is required");
        }
        /*else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfile.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }
        /*
        else if (course.isEmpty()){
            Toast.makeText(UpdateProfile.this, "Please enter your course", Toast.LENGTH_SHORT).show();
            binding.signcourse.setError("Course is required");
        }
        */

        else {
            //Obtain data entered by user entered data
            username = binding.signusername.getText().toString();
            fullname = binding.signfullname.getText().toString();
            dateofbirth = binding.signdateofbirth.getText().toString();
            //sex = binding.signsex.getText().toString();
            //course = binding.signcourse.getText().toString();

            //Users users = new Users(username, fullname, dateofbirth, sex, course, email, password);

            HashMap hashUsers = new HashMap();
            hashUsers.put("username", username);
            hashUsers.put("fullname", fullname);
            hashUsers.put("dateofbirth", dateofbirth);
            hashUsers.put("sex", sex2);
            hashUsers.put("course", course2);

            userID = firebaseUser.getUid();
            referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
            referenceProfile.child(userID).updateChildren(hashUsers).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UpdateProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfile.this, Profile.class) ;

                        //clear stack to prevent user from coming back after pressing back or logging out
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }
    }

    private void showMiniProfile(FirebaseUser firebaseUser) {
        userID = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails.profileurl.equals("")){
                    binding.imageButtonProfile2.setImageResource(R.drawable.jinx);
                }
                else{
                    //Uri uri = firebaseUser.getPhotoUrl();
                    //Set use's current DP in ImageView(if uploaded already.) We will Picasso since imageviewer setImage
                    //Regular URI's
                    Picasso.get().load(readUserDetails.profileurl).into(binding.imageButtonProfile2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showProfile(FirebaseUser firebaseUser) {
        userID = firebaseUser.getUid();

        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails != null){
                    username = readUserDetails.username;
                    fullname = readUserDetails.fullname;
                    dateofbirth = readUserDetails.dateofbirth;
                    sex = readUserDetails.sex;
                    course = readUserDetails.course;

                    binding.signusername.setText(username);
                    binding.signfullname.setText(fullname);
                    binding.signdateofbirth.setText(dateofbirth);
                    //binding.signsex.setText(sex);
                    //binding.signcourse.setText(course);


                    //show yung piniling sex sa list
                    List<String> gender = new ArrayList<>();
                    gender.add(0, sex);
                    if (!sex.equals("Male")){
                        gender.add("Male");
                    }
                    if (!sex.equals("Female")){
                        gender.add("Female");
                    }

                    ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(UpdateProfile.this, R.layout.spinner_selected, gender);
                    binding.signsex.setAdapter(adapterSex);
                    adapterSex.setDropDownViewResource(R.layout.spinner_dropdown);


                    //show yung piniling course at list
                    List<String> courses = new ArrayList<>();
                    courses.add(0, course);
                    if (!course.equals("Accountancy")){
                        courses.add("Accountancy");
                    }
                    if (!course.equals("Architecture")){
                        courses.add("Architecture");
                    }
                    if (!course.equals("Arts and Design")){
                        courses.add("Arts and Design");
                    }
                    if (!course.equals("Biology")){
                        courses.add("Biology");
                    }
                    if (!course.equals("Business Management")){
                        courses.add("Business Management");
                    }
                    if (!course.equals("Chemistry")){
                        courses.add("Chemistry");
                    }
                    if (!course.equals("Computer Engineering")){
                        courses.add("Computer Engineering");
                    }
                    if (!course.equals("Computer Science")){
                        courses.add("Computer Science");
                    }
                    if (!course.equals("English")){
                        courses.add("English");
                    }
                    if (!course.equals("Geology")){
                        courses.add("Geology");
                    }
                    if (!course.equals("History")){
                        courses.add("History");
                    }
                    if (!course.equals("Information Technology")){
                        courses.add("Information Technology");
                    }
                    if (!course.equals("Law")){
                        courses.add("Law");
                    }
                    if (!course.equals("Literature")){
                        courses.add("Literature");
                    }
                    if (!course.equals("Mathematics")){
                        courses.add("Mathematics");
                    }
                    if (!course.equals("Mechanical Engineering")){
                        courses.add("Mechanical Engineering");
                    }
                    if (!course.equals("Nursing")){
                        courses.add("Nursing");
                    }
                    if (!course.equals("Physics")){
                        courses.add("Physics");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateProfile.this, R.layout.spinner_selected, courses);
                    binding.signcourse.setAdapter(adapter);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}