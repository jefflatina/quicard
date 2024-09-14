package com.example.quicard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quicard.databinding.ActivityUpdateProfileBinding;
import com.example.quicard.databinding.ActivityUpdateProfilePicBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class UpdateProfilePic extends AppCompatActivity {

    ActivityUpdateProfilePicBinding binding;

    FirebaseAuth authProfile;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    ImageButton btn_back5;

    private static final int  PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfilePicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();

        //Set use's current DP in ImageView(if uploaded already.) We will Picasso since imageviewer setImage
        //Regular URI's
        Picasso.get().load(uri).into(binding.imageView2);

        //Choose image to upload
        binding.btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //upload image
        binding.btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();
            }
        });

        //back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void uploadPic() {
        if (uriImage != null){
            //save image with uid of the currently logged in user
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));

            //Upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            //Finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates = new  UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);

                            String url = downloadUri.toString();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("profileurl").setValue(url);
                        }
                    });

                    //progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfilePic.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateProfilePic.this, Profile.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfilePic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            //progressBar.setVisibility(View.GONE);
            Toast.makeText(UpdateProfilePic.this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    //getting file extension of the image
    private String getFileExtension(Uri uriImage) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            binding.imageView2.setImageURI(uriImage);
        }
    }
}