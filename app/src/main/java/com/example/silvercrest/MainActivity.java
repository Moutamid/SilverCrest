package com.example.silvercrest;

import static com.example.silvercrest.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    TextView showUser;
    ImageView menuImage, imgHeader, addMoney;
    private FirebaseAuth mAuth;
    TextView tvEasy, tvTotal;
    private RecyclerView listRecyclerView;
    private RecyclerView.LayoutManager listLayoutManager;
    private ProgressDialog mDialog;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.getString("emailStr", "n").equals("n")) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        showUser = headerView.findViewById(R.id.header_user);
        imgHeader = headerView.findViewById(R.id.img_header);
        addMoney = findViewById(R.id.image_add_money);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        tvEasy = findViewById(R.id.tv_easybalace);
        tvTotal = findViewById(R.id.tv_total);
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage("Loading...");


        mDialog.show();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference();
//        if (user != null) {
//            String userEmail = user.getEmail();
////            Toast.makeText(this, ""+userEmail, Toast.LENGTH_SHORT).show();
//            showUser.setText(userEmail);
//        } else {
//            // No user is signed in
//        }


        DatabaseReference mRef = mDatabaseUsers.child("users").child(mAuth.getCurrentUser().getUid());
        mRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        String userName = "name", profUrl = "url", balance = "0";
                        if (dataSnapshot.child("Name").exists())
                            userName = dataSnapshot.child("Name").getValue().toString();
                        if (dataSnapshot.child("profile").exists())
                            profUrl = dataSnapshot.child("profile").getValue().toString();
                        if (dataSnapshot.child("Balance").exists())
                            balance = dataSnapshot.child("Balance").getValue().toString();
                        tvEasy.setText("$" + balance);
                        tvTotal.setText("$" + balance);
//            Toast.makeText(this, ""+userEmail, Toast.LENGTH_SHORT).show();
                        showUser.setText(userName);

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round);


                        Glide.with(MainActivity.this).load(profUrl).apply(options).into(imgHeader);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        ArrayList<TransactionListModel> arrayList = new ArrayList<>();
        DatabaseReference ref = mDatabaseUsers.child("Transactions").child(mAuth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String name = child.getKey();
                            String accname = dataSnapshot.child(name).child("account name").getValue().toString();
                            String number = dataSnapshot.child(name).child("account number").getValue().toString();
                            String bank = dataSnapshot.child(name).child("bank name").getValue().toString();
                            String amount = dataSnapshot.child(name).child("amount").getValue().toString();
                            String swift = dataSnapshot.child(name).child("swift code").getValue().toString();
                            String purpose = dataSnapshot.child(name).child("purpose").getValue().toString();


                            arrayList.add(new TransactionListModel(accname, number, bank, amount, swift, purpose));
                        }
                        listRecyclerView = findViewById(R.id.recyclerview_list2);
                        listRecyclerView.setHasFixedSize(true);
                        listLayoutManager = new LinearLayoutManager(MainActivity.this);
                        TransactionListAdapter booksListAdapter = new TransactionListAdapter(arrayList, MainActivity.this);
                        listRecyclerView.setAdapter(booksListAdapter);
                        listRecyclerView.setLayoutManager(listLayoutManager);
                        booksListAdapter.notifyDataSetChanged();

                        mDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        menuImage = findViewById(R.id.menuBtn);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_money_dialog);
                EditText editTextMoney = dialog.findViewById(R.id.edit_money);
                Button add = dialog.findViewById(R.id.bt_add_money);
                dialog.show();

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String money = editTextMoney.getText().toString();
                        DatabaseReference mRef = mDatabaseUsers.child("users").child(mAuth.getCurrentUser().getUid());
                        mRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Get map of users in datasnapshot
                                        String balance;
                                        balance = dataSnapshot.child("Balance").getValue().toString();
                                        int newBal = Integer.parseInt(money) + Integer.parseInt(balance);

                                        mRef.child("Balance").setValue(String.valueOf(newBal));
                                        tvEasy.setText("$" + String.valueOf(newBal));
                                        tvTotal.setText("$" + String.valueOf(newBal));

                                        Toast.makeText(MainActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, ""+userEmail, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //handle databaseError
                                    }
                                });
                    }
                });
            }
        });

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.about_us:
                        Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                        startActivity(intent);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.rate_us:
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.logout:
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
//set icon
                                .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                                .setTitle("Are you sure to Logout")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what would happen when positive button is clicked
                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        auth.signOut();

                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //set what should happen when negative button is clicked
                                        drawer.closeDrawer(GravityCompat.START);

                                    }
                                })
                                .show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}