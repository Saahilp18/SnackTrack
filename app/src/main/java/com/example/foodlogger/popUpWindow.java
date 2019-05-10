package com.example.foodlogger;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class popUpWindow extends Activity {
    TextView foodNameTV, foodGroupTV, dateTV;
    ImageView foodImage;
    ImageView deleteButton;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayList<foodItem> foodList;
    Boolean one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);
        one = true;
        foodNameTV = findViewById(R.id.id_foodNamepopUpId);
        foodGroupTV = findViewById(R.id.id_foodGroupPopupTV);
        dateTV = findViewById(R.id.id_datePopUpTV);
        deleteButton = findViewById(R.id.id_deleteButton);
        foodImage = findViewById(R.id.id_popupImageFood);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        getWindow().setLayout((int) (w * .8), (int) (h * .6));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
        final Bundle stuff = this.getIntent().getExtras();
        foodNameTV.setText(stuff.getString("name").trim());
        foodGroupTV.setText(stuff.getString("type").trim());
        dateTV.setText(stuff.getString("date").trim());
        Glide.with(getApplicationContext()).load(stuff.getString("imageURI")).into(foodImage);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        foodList = new ArrayList<>();
                        long listLength = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").getChildrenCount();
                        for (int i = 0; i < (int) listLength; i++) {
                            String foodName = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodName").getValue().toString();
                            String foodType = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodType").getValue().toString();
                            String foodDate = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodDate").getValue().toString();
                            String imageURI = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("imageURI").getValue().toString();
                            foodList.add(new foodItem(foodName, foodType, foodDate, imageURI));
                        }
                        if (one) {
                            if ((int) getIntent().getExtras().get("pos") < foodList.size())
                                foodList.remove((int) getIntent().getExtras().get("pos"));
                            one = false;
                        }
                        databaseReference.child(user.getUid()).child("Foods").child("Food Info List").setValue(foodList).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DELETED");
                                one = false;
                                finish();
                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });
    }
}
