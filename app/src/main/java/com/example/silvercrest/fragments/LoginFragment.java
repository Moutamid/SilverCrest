package com.example.silvercrest.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.silvercrest.MainActivity;
import com.example.silvercrest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        emailEditText=view.findViewById(R.id.email_login);
        passwordEditText=view.findViewById(R.id.password_login);
        loginBtn=view.findViewById(R.id.login_bt);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing you in...");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = emailEditText.getText().toString();
                String passwordStr = passwordEditText.getText().toString();

                if (!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr)) {

                    signInUserWithNameAndPassword(emailStr, passwordStr);

                } else if (TextUtils.isEmpty(emailStr)) {

                    emailEditText.setError("Please enter emailStr");
                    emailEditText.requestFocus();

                } else if (TextUtils.isEmpty(passwordStr)) {

                    passwordEditText.setError("Please enter password");
                    passwordEditText.requestFocus();

                }

            }
        });

        return view;
    }
    private void signInUserWithNameAndPassword(final String emailStr, final String passwordStr) {

        progressDialog.show();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            //if Email Address is Invalid..

            progressDialog.dismiss();
            emailEditText.setError("Email is not valid. Make sure no spaces and special characters are included");
            emailEditText.requestFocus();
        } else {

            mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();

                    } else {

                        progressDialog.dismiss();
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }
}