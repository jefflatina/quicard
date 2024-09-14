package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.quicard.databinding.ActivityUpdateEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateEmail extends AppCompatActivity {
    ActivityUpdateEmailBinding binding;

    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    DatabaseReference referenceProfile;

    private String username, fullname, dateofbirth, sex, course, email, password;
    private String userID;
    private String userOldEmail, userNewEmail, userPwd;

    Button cancel_btn;
    ImageButton btn_back2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateEmail.this, Profile.class));
            }
        });

        //back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //make button disabled until verified
        binding.btnAuthenticate2.setEnabled(false);
        binding.edittxtNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //set old email ID on textview
        userOldEmail = firebaseUser.getEmail();
        binding.txtviewCurrentEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmail.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        else {
            reauthenticate(firebaseUser);
        }

    }



    //reauthenticate/verify user before updating email
    private void reauthenticate(FirebaseUser firebaseUser) {
        binding.btnAuthenticate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtain password for authentication
                userPwd = binding.edittxtPassword.getText().toString();

                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmail.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    binding.edittxtPassword.setError("Please eneter your password for authentication");
                    binding.edittxtPassword.requestFocus();
                }
                else {
                    //progressBar.setVisibility(View.VISIBLE);

                    //represents credential that firebase authentication can use to authenticate user
                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //progressBar.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmail.this, "Password has been verified. " + "You can update email now", Toast.LENGTH_SHORT).show();

                                //Set Textview to show that user is authenticated
                                binding.txtviewCurrentEmail.setText("You are now authenticated");

                                //Disable edit text for password, button to verify user and enable editext for new email and button
                                binding.edittxtNewEmail.setEnabled(true);
                                binding.edittxtPassword.setEnabled(false);
                                binding.btnAuthenticate1.setEnabled(false);
                                binding.btnAuthenticate2.setEnabled(true);

                                //Change color of update email button
                                //binding.btnAuthenticate2.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmail.this, R.color.teal_700));

                                binding.btnAuthenticate2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = binding.edittxtNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmail.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                            binding.edittxtNewEmail.setError("Please enter new email");
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmail.this, "Valid Email is required", Toast.LENGTH_SHORT).show();
                                            binding.edittxtNewEmail.setError("Please enter valid email");
                                        }
                                        else if (userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdateEmail.this, "New Email cannot be same as old email", Toast.LENGTH_SHORT).show();
                                            binding.edittxtNewEmail.setError("Please enter new email");
                                        }
                                        else {
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }
                            else{
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(UpdateEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    //verify email
                    firebaseUser.sendEmailVerification();


                    //dinagdag lang itey para maupdate sa database
                    //Users users = new Users(username, fullname, dateofbirth, sex, course, userNewEmail, password);

                    HashMap hashUsers = new HashMap();
                    hashUsers.put("email", userNewEmail);


                    userID = firebaseUser.getUid();
                    referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
                    referenceProfile.child(userID).updateChildren(hashUsers).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UpdateEmail.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateEmail.this, Profile.class) ;

                                //clear stack to prevent user from coming back after pressing back or logging out
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    //hanggang dito yun

                    //Toast.makeText(UpdateEmail.this, "Email has been updated. Please verify your email", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(UpdateEmail.this, Profile.class);
                    //startActivity(intent);
                    //finish();
                }
                else{
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(UpdateEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}