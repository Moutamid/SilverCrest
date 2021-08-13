package com.example.silvercrest;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.silvercrest.fragments.LoginFragment;
import com.example.silvercrest.fragments.SignupFragment;


public class LoginActivity extends AppCompatActivity {

    RelativeLayout relativeLogin,relativeSignup;
    TextView tvLogin,tvSignin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relativeLogin=findViewById(R.id.relative_login);
        relativeSignup=findViewById(R.id.relative_signup);
        tvLogin=findViewById(R.id.tv_login);
        tvSignin=findViewById(R.id.tv_signin);

        LoginFragment loginFragment=new LoginFragment();
        replaceFragment(loginFragment);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void goToLogin(View view) {
        LoginFragment loginFragment=new LoginFragment();
        replaceFragment(loginFragment);
        relativeLogin.setBackgroundResource(R.drawable.linear2_back);
        relativeSignup.setBackground(null);
        tvLogin.setTextColor(getResources().getColor(R.color.white));
        tvSignin.setTextColor(getResources().getColor(R.color.purple_500));

    }

    public void goToSignup(View view) {
        SignupFragment signupFragment=new SignupFragment();
        replaceFragment(signupFragment);
        relativeSignup.setBackgroundResource(R.drawable.linear2_back);
        relativeLogin.setBackground(null);
        tvLogin.setTextColor(getResources().getColor(R.color.purple_500));
        tvSignin.setTextColor(getResources().getColor(R.color.white));
    }

}