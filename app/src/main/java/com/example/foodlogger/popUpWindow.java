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
                            long fruitCountlong = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").getValue();
                            long vegeCountlong = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").getValue();
                            long grainCountlong = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").getValue();
                            long proteinCountlong = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").getValue();
                            long dairyCountlong = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Dairy").getValue();
                            int fruitCount = (int) fruitCountlong;
                            int vegeCount = (int) vegeCountlong;
                            int grainCount = (int) grainCountlong;
                            int proteinCount = (int) proteinCountlong;
                            int dairyCount = (int) dairyCountlong;
                            int pos = (int) getIntent().getExtras().get("pos");
                            if (pos < foodList.size()) {
                                switch (foodList.get(pos).getFoodType()) {
                                    case "Fruit":
                                        fruitCount--;
                                        databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").setValue(fruitCount);
                                        break;
                                    case "Vegetable":
                                        vegeCount--;
                                        databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").setValue(vegeCount);
                                        break;
                                    case "Grains":
                                        grainCount--;
                                        databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").setValue(grainCount);
                                        break;
                                    case "Protein":
                                        proteinCount--;
                                        databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").setValue(proteinCount);
                                        break;
                                    case "Dairy":
                                        dairyCount--;
                                        databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Dairy").setValue(dairyCount);
                                        break;
                                }
                               /* if (foodList.get(pos).getFoodType().equals("Fruit")) {
                                    fruitCount--;
                                    databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").setValue(fruitCount);
                                }
                                if (foodList.get(pos).getFoodType().equals("Vegetable")) {
                                    vegeCount--;
                                    databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").setValue(vegeCount);
                                }
                                if (foodList.get(pos).getFoodType().equals("Grains")) {
                                    grainCount--;
                                    databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").setValue(grainCount);
                                }
                                if (foodList.get(pos).getFoodType().equals("Protein")) {
                                    proteinCount--;
                                    databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").setValue(proteinCount);
                                }
                                if (foodList.get(pos).getFoodType().equals("Dairy")) {
                                    dairyCount--;
                                    databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Dairy").setValue(dairyCount);
                                }*/
                                foodList.remove((int) getIntent().getExtras().get("pos"));
                                one = false;
                                databaseReference.child(user.getUid()).child("Foods").child("Food Info List").setValue(foodList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DELETED");
                                        one = false;
                                        finish();
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });
    }
}
