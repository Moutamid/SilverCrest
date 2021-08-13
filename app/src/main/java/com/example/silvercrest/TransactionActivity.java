package com.example.silvercrest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class TransactionActivity extends AppCompatActivity {
    EditText accName,accNumber,bankName,amount,swiftCode;
    String  name,number,bank,tAmount,swift,purpos;
    Spinner purpose;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        accName=findViewById(R.id.account_name);
        accNumber=findViewById(R.id.account_number);
        bankName=findViewById(R.id.bank_name);
        amount=findViewById(R.id.amount);
        swiftCode=findViewById(R.id.swift_code);
        purpose=findViewById(R.id.purpose);
        next=findViewById(R.id.bt_transaction);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=accName.getText().toString();
                number=accNumber.getText().toString();
                bank=bankName.getText().toString();
                tAmount=amount.getText().toString();
                swift=swiftCode.getText().toString();
                purpos=purpose.getSelectedItem().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number) &&
                        !TextUtils.isEmpty(bank) && !TextUtils.isEmpty(tAmount)&& !TextUtils.isEmpty(swift)) {

                    // Checking if passwordStr is equal to confirmed Password

                    Intent intent= new Intent(TransactionActivity.this,OTPVerifyActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("number",number);
                    intent.putExtra("bank",bank);
                    intent.putExtra("amount",tAmount);
                    intent.putExtra("swift",swift);
                    intent.putExtra("purpose",purpos);
                    startActivity(intent);

                    // Signing up user


                    // User Name is Empty
                }
                else if (TextUtils.isEmpty(name)) {


                    accName.setError("Please provide account Name");
                    accName.requestFocus();


                    // Password is Empty
                }
                else if (TextUtils.isEmpty(number)) {


                    accNumber.setError("Please provide account Number");
                    accNumber.requestFocus();


                    // Password is Empty
                }
                else if (TextUtils.isEmpty(bank)) {


                    bankName.setError("Please provide Bank Name");
                    bankName.requestFocus();


                    // Password is Empty
                }
                else if (TextUtils.isEmpty(tAmount)) {


                    amount.setError("Please provide amount");
                    amount.requestFocus();


                    // Password is Empty
                }
                else if (TextUtils.isEmpty(swift)) {


                    swiftCode.setError("Please provide swift code");
                    swiftCode.requestFocus();


                    // Password is Empty
                }

            }
        });






    }
}