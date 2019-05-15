package com.example.foodlogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class userAccount extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextView usersNameTV;
    String firstName, lastName;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            try {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        usersNameTV.setText("User: " + firstName + " " + lastName.charAt(0));
                        selectedFragment = new homeFragment();
                        break;
                    case R.id.navigation_add:
                        usersNameTV.setText("User: " + firstName + " " + lastName.charAt(0));
                        selectedFragment = new addFoodFragment();
                        break;
                    case R.id.navigation_settings:
                        usersNameTV.setText("User: " + firstName + " " + lastName.charAt(0));
                        selectedFragment = new settingsFragment();
                        break;
                    case R.id.navigation_graphs:
                        usersNameTV.setText("User: " + firstName + " " + lastName.charAt(0));
                        selectedFragment = new graphs_fragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.id_fragmentContainer, selectedFragment).commit();
            } catch (Exception e) {

            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        getSupportFragmentManager().beginTransaction().replace(R.id.id_fragmentContainer, new homeFragment()).commit();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setIds();
        usersNameTV.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("Name").child("First Name").getValue().toString();
                lastName = dataSnapshot.child("Name").child("Last Name").getValue().toString();

                usersNameTV.setText("Welcome " + firstName + " !");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setIds() {
        usersNameTV = findViewById(R.id.id_nameTV);
    }

}

