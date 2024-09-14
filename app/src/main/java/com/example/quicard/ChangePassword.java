package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    ActivityChangePasswordBinding binding;

    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    private String userPwdCurr, userPwdNew, userPwdConfirmNew;

    Button cancel_btn2;
    ImageButton btn_back4;
    TextView forgotpasslink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //cancel button
        cancel_btn2 = findViewById(R.id.cancel_btn2);
        cancel_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePassword.this, Profile.class));
            }
        });

        //back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back4);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //forgotpass button
        forgotpasslink = findViewById(R.id.forgotpasslink);
        forgotpasslink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePassword.this, ForgotPass.class));
            }
        });

/*
        //redirect sa forgot password page
        forgotpasslink.findViewById((R.id.forgotpasslink));
        forgotpasslink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePassword.this, ForgotPass.class));
            }
        });


 */


        //Disable editText for New Password, Confirm new pass, and make change pwd button unclickable
        binding.edittxtNewPassword.setEnabled(false);
        binding.edittxtConfirmPass.setEnabled(false);
        binding.btnChangePass.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            //Intent intent =  new Intent(ChangePassword.this, Profile.class);
            //startActivity(intent);
            //finish();
        }
        else {
            reauthenticateUser(firebaseUser);
        }
    }

    //reauthenticateuser before changing password
    private void reauthenticateUser(FirebaseUser firebaseUser) {
        binding.btnAuthenticate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = binding.edittxtCurrentPassword.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePassword.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    binding.edittxtCurrentPassword.setError("Please enter current password to authenticate");
                    binding.edittxtCurrentPassword.requestFocus();
                }
                else{
                    //progressBar.setVisibility(View.VISIBLE);

                    //Reauthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //progressBar.setVisibility(View.GONE);

                                //disable text for current pass
                                binding.edittxtCurrentPassword.setEnabled(false);
                                binding.edittxtNewPassword.setEnabled(true);
                                binding.edittxtConfirmPass.setEnabled(true);

                                //Enable changepwd button and disable authenticate button
                                binding.btnAuthenticate1.setEnabled(false);
                                binding.btnChangePass.setEnabled(true);

                                //Set textview to show user is authenticated
                                //binding.txtviewCurrentPass.setText("You are authenticated" + "You can now change your password");
                                Toast.makeText(ChangePassword.this, "Password has been verified. " + "Change password now", Toast.LENGTH_SHORT).show();

                                //Update color of button
                                //binding.btnChangePass.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.teal_700));

                                binding.btnChangePass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }
                            else{
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                //progressBar.setVisibility(View.Gone);
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        userPwdNew = binding.edittxtNewPassword.getText().toString();
        userPwdConfirmNew = binding.edittxtConfirmPass.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePassword.this, "New Password is needed", Toast.LENGTH_SHORT).show();
            binding.edittxtNewPassword.setError("Please enter your new password");
            binding.edittxtNewPassword.requestFocus();
        }
        else if (TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePassword.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            binding.edittxtConfirmPass.setError("Please re-enter your new password");
            binding.edittxtConfirmPass.requestFocus();
        }
        else if (!userPwdNew.matches(userPwdConfirmNew)){
            Toast.makeText(ChangePassword.this, "Password did not match", Toast.LENGTH_SHORT).show();
            binding.edittxtNewPassword.setError("Please re-enter same password");
            binding.edittxtNewPassword.requestFocus();
        }
        else if (userPwdCurr.matches(userPwdNew)){
            Toast.makeText(ChangePassword.this, "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
            binding.edittxtNewPassword.setError("Please enter a new password");
            binding.edittxtNewPassword.requestFocus();
        }
        else {
            //progressBar.setVisibility(View.Visible);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePassword.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, Profile.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}