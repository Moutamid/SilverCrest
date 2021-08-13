package com.example.silvercrest.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.silvercrest.MainActivity;
import com.example.silvercrest.R;
import com.example.silvercrest.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.example.silvercrest.Utils.*;


public class SignupFragment extends Fragment {

    EditText email,password,confirmPass,nameFull;
    String emailStr,passwordStr,phone,fullName;
    private ProgressDialog mDialog;
    String Storage_Path = "All_Image_Uploads/";
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUsers;
    ImageView imgProfile;
    Uri FilePathUri;
    Button signUpBt;
    int Image_Request_Code = 7;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signup, container, false);
        email=view.findViewById(R.id.email_signup);
        password=view.findViewById(R.id.password_signup);
        confirmPass=view.findViewById(R.id.phone_no);
        nameFull=view.findViewById(R.id.full_name_signup);
        signUpBt=view.findViewById(R.id.signup_bt);
        imgProfile=view.findViewById(R.id.img_profile);
        storageReference = FirebaseStorage.getInstance().getReference();

        mDialog = new ProgressDialog(getContext());
        mDialog.setCancelable(false);
        mDialog.setMessage("Signing you in...");
        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers.keepSynced(true);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });
        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatusOfEditTexts();
            }
        });
        return view;
    }
    private void checkStatusOfEditTexts() {

        // Getting strings from edit texts

        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        phone = confirmPass.getText().toString();
        fullName=nameFull.getText().toString();


        // Checking if Fields are empty or not
        if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr) && !TextUtils.isEmpty(phone)) {

            // Checking if passwordStr is equal to confirmed Password


                // Signing up user
                signUpUserWithNameAndPassword();


            // User Name is Empty
        }
        else if (TextUtils.isEmpty(fullName)) {


            email.setError("Please provide your Name");
            email.requestFocus();


            // Password is Empty
        }
        else if (TextUtils.isEmpty(emailStr)) {


            email.setError("Please provide a emailStr");
            email.requestFocus();


            // Password is Empty
        }
        else if (TextUtils.isEmpty(passwordStr)) {

            password.setError("Please provide a passwordStr");
            password.requestFocus();


            // Confirm Password is Empty
        } else if (TextUtils.isEmpty(phone)) {

            confirmPass.setError("Please provide your phone");
            confirmPass.requestFocus();


        }

    }
    private void signUpUserWithNameAndPassword() {
        mDialog.show();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            //if Email Address is Invalid..

            mDialog.dismiss();
            email.setError("Please enter a valid email with no spaces and special characters included");
            email.requestFocus();
        } else {

            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        addUserDetailsToDatabase();

                    } else {

                        mDialog.dismiss();
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
//        } else {
//
//            mDialog.dismiss();
//            Toast.makeText(this, "You are not online", Toast.LENGTH_SHORT).show();
//        }
    }
    private void addUserDetailsToDatabase() {
        UploadImageFileToFirebaseStorage();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                imgProfile.setImageBitmap(bitmap);

                // After selecting image change choose button above text.

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
//            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.


            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.


                            // Hiding the progressDialog after done uploading.
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    DatabaseReference mRef = mDatabaseUsers.child("users").child(mAuth.getCurrentUser().getUid());
                                    mRef.child("Name").setValue(fullName);
                                    mRef.child("Email").setValue(emailStr);
                                    mRef.child("Password").setValue(passwordStr);
                                    mRef.child("Phone").setValue(phone);
                                    mRef.child("Balance").setValue("0");
                                    mRef.child("profile").setValue(downloadUrl.toString());

                                    store("emailStr", emailStr);
                                    store("passwordStr", passwordStr);

                                    mDialog.dismiss();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(intent);

                                    getActivity().

                                            finish();

                                    Toast.makeText(

                                            getContext(), "You are signed up!", Toast.LENGTH_SHORT).

                                            show();

                                    //Do what you want with the url
                                }
                            });
                            // Showing toast message after done uploading.

//                            String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                            mDatabaseRef=mDatabaseRef.child("Categories").child(TempImageName);
//                            mDatabaseRef.child("url").setValue(url);
//                            @SuppressWarnings("VisibleForTests")
//                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());
//
//                            // Getting image upload ID.
//                            String ImageUploadId = databaseReference.push().getKey();
//
//                            // Adding image upload id s child element into databaseReference.
//                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.

                            // Showing exception erro message.
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.


                        }
                    });
        }
        else {

            Toast.makeText(getContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
}