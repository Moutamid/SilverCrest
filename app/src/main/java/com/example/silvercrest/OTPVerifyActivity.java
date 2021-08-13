package com.example.silvercrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPVerifyActivity extends AppCompatActivity {
    String name, number, bank, tAmount, swift, purpos, otpCode, phone = "0", balance = "0";
    EditText otp;
    Button btComplete;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    DatabaseReference mRef;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);

        otp = findViewById(R.id.otp);
        btComplete = findViewById(R.id.bt_complete);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        bank = intent.getStringExtra("bank");
        tAmount = intent.getStringExtra("amount");
        swift = intent.getStringExtra("swift");
        purpos = intent.getStringExtra("purpose");
        mRef = mDatabaseUsers.child("users").child(mAuth.getCurrentUser().getUid());
        mRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        if (dataSnapshot.child("Phone").exists())
                            phone = dataSnapshot.child("Phone").getValue().toString();
                        Toast.makeText(OTPVerifyActivity.this, "" + phone, Toast.LENGTH_SHORT).show();
                        if (dataSnapshot.child("Balance").exists())
                            balance = dataSnapshot.child("Balance").getValue().toString();
                        sendVerificationCode(phone);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyVerificationCode(otp.getText().toString());
//                saveData();
            }
        });
    }

    private void saveData() {
        DatabaseReference mNewRef = mDatabaseUsers.child("Transactions").child(mAuth.getCurrentUser().getUid()).push();

        mNewRef.child("account name").setValue(name);
        mNewRef.child("account number").setValue(number);
        mNewRef.child("bank name").setValue(bank);
        mNewRef.child("amount").setValue(tAmount);
        mNewRef.child("swift code").setValue(swift);
        mNewRef.child("purpose").setValue(purpos);
        int newBlance = Integer.parseInt(balance) - Integer.parseInt(tAmount);
        mRef.child("Balance").setValue(String.valueOf(newBlance));
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setText(code);
                //verifying the code
//                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTPVerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(OTPVerifyActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        String mycode = credential.getSmsCode();
        if (mycode.equals(otp.getText().toString())) {

            Toast.makeText(this, "verify", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not verify", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPVerifyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Toast.makeText(OTPVerifyActivity.this, "verify", Toast.LENGTH_SHORT).show();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
}