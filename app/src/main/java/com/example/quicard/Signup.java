package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.quicard.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Signup extends AppCompatActivity {
    ActivitySignupBinding binding;

    String username, fullname, dateofbirth, sex, course, email, password, conpassword, profileurl, spinner;
    //for mydecks
    ArrayList<String> myDecks;

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    private DatePickerDialog picker;
    //
    private ArrayList<Deck> allDecks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> courses =  new ArrayList<String>();
        courses.add(0, "Course");
        courses.add("Accountancy");
        courses.add("Architecture");
        courses.add("Arts and Design");
        courses.add("Biology");
        courses.add("Business Management");
        courses.add("Chemistry");
        courses.add("Computer Engineering");
        courses.add("Computer Science");
        courses.add("English");
        courses.add("Geology");
        courses.add("History");
        courses.add("Information Technology");
        courses.add("Law");
        courses.add("Literature");
        courses.add("Mathematics");
        courses.add("Mechanical Engineering");
        courses.add("Nursing");
        courses.add("Physics");

        //spinner for sex
        List<String> gender = new ArrayList<>();
        gender.add(0, "Sex");
        gender.add("Male");
        gender.add("Female");

        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(Signup.this, R.layout.spinner_selected, gender);
        binding.spinnerSex.setAdapter(adapterSex);
        adapterSex.setDropDownViewResource(R.layout.spinner_dropdown);


        binding.spinnerSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sex = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_selected, courses);
        binding.spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    course = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //
        allDecks = (ArrayList<Deck>) getIntent().getSerializableExtra("allDecks");
        mAuth = FirebaseAuth.getInstance();

        //setting datepicker on edittext
        binding.signdateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date picker dialog
                picker = new DatePickerDialog(Signup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        binding.signdateofbirth.setText(day + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);
                picker.show();
            }
        });

        //maredirect sa Terms.java na page
        binding.txtterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Terms.class));
            }
        });

        //pag niclick yung create account kukunin yung string na ininput
        binding.btncreateacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = binding.signusername.getText().toString();
                fullname = binding.signfullname.getText().toString();
                dateofbirth = binding.signdateofbirth.getText().toString();
                //sex = binding.sign.getText().toString();
                // course = binding.signcourse.getText().toString();
                email = binding.signemail.getText().toString();
                password = binding.signpassword.getText().toString();
                conpassword = binding.signconpassword.getText().toString();

                //condition to check kung may laman lahat ng box
                /*if (username.isEmpty() || fullname.isEmpty() || age.isEmpty() || sex.isEmpty() || course.isEmpty()
                || email.isEmpty() || password.isEmpty() || conpassword.isEmpty()){
                    Toast.makeText(Signup.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }*/
                if (username.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    binding.signusername.setError("Username required");
                }
                else if (fullname.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    binding.signfullname.setError("Full name is required");
                }
                else if (dateofbirth.isEmpty()){
                    Toast.makeText(Signup.this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
                    binding.signdateofbirth.setError("Date of birth is required");
                }
                /*
                else if (sex.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter your sex", Toast.LENGTH_SHORT).show();
                    binding.signsex.setError("Sex is required");
                }

                else if (course.equals("Choose your course")){
                    Toast.makeText(Signup.this, "Please enter your course", Toast.LENGTH_SHORT).show();
                    binding.signcourse.setError("Course is required");
                }
                */

                else if (sex.equals("Sex")){
                    Toast.makeText(Signup.this, "Please select your sex", Toast.LENGTH_SHORT).show();
                }

                else if (course.equals("Course")){
                    Toast.makeText(Signup.this, "Please select your course", Toast.LENGTH_SHORT).show();
                }

                else if (email.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    binding.signemail.setError("Email is required");
                    if (!email.isEmpty()){
                        checkemail(email);
                    }
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Signup.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    binding.signemail.setError("Valid Email is required");
                }
                else if (password.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    binding.signpassword.setError("Password is required");
                }
                else if (password.length() < 6){
                    Toast.makeText(Signup.this, "Password should be at least 6 digits/characters,letters", Toast.LENGTH_SHORT).show();
                    binding.signpassword.setError("Password is too weak");
                }
                else if (conpassword.isEmpty()){
                    Toast.makeText(Signup.this, "Please enter same password", Toast.LENGTH_SHORT).show();
                    binding.signconpassword.setError("Same password is required");
                }
                else if (!password.equals(conpassword)){
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    binding.signpassword.clearComposingText();
                    binding.signconpassword.clearComposingText();
                }
                //pag nasatisfy lahat then kukunin na info at lalagay sa database
                else{
                    Users user = new Users(username, fullname, dateofbirth, sex, course, email, profileurl, myDecks);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");

                    //sa firebase authentication papasok naman
                    //createUser();
                    mAuth = FirebaseAuth.getInstance();
                    email = binding.signemail.getText().toString();
                    password = binding.signpassword.getText().toString();
                    username = binding.signusername.getText().toString();


                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                HashMap uname = new HashMap();
                                uname.put("username", username);
                                uname.put("fullname", fullname);
                                uname.put("dateofbirth", dateofbirth);
                                uname.put("sex", sex);
                                uname.put("course", course);
                                uname.put("email", email);
                                uname.put("profileurl", "");

                                reference.child(firebaseUser.getUid()).setValue(uname).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        binding.signusername.setText(" ");
                                        binding.signfullname.setText(" ");
                                        binding.signdateofbirth.setText(" ");
                                        //binding.signsex.setText(" ");
                                        //binding.signcourse.setText(" ");
                                        binding.signemail.setText(" ");
                                        binding.signpassword.setText(" ");
                                        binding.signconpassword.setText(" ");

                                        //redirect sa login page kasi walang back button
                                        //startActivity(new Intent(Signup.this, Login.class));
                                        Intent intent = new Intent(Signup.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish(); //close register activity
                                    }
                                });
                            }
                            else {
                                Toast.makeText(Signup.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }
            }
        });
    }

    private void checkusername() {
        String userUsername = binding.signusername.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUsernameDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUsernameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.signusername.setError("Username already used");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkemail(String email) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkEmailDatabase = reference.orderByChild("email").equalTo(email);

        checkEmailDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(Signup.this, "Email already used", Toast.LENGTH_SHORT).show();
                    binding.signemail.setError("Email already used ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void createUser() {
        String email = binding.signemail.getText().toString();
        String password = binding.signpassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Signup.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Signup.this, "Registration Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}