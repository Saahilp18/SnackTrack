package com.example.foodlogger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class addFoodInfo extends AppCompatActivity {
    RadioGroup radioGroup;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseUser user;
    FirebaseAuth auth;
    ImageView foodImageView;
    String imageURI;
    Button addFoodButton;
    Boolean fruitB, veggieB, grainsB, proteinB, dairyB;
    EditText foodNameET, foodDateET;
    String foodType;
    ArrayList<foodItem> foodList;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_info);
        setIds();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Food...");

        imageURI = getIntent().getStringExtra("imageURI");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        foodList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                foodList = new ArrayList<>();
                long listLength = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").getChildrenCount();
                for (int i = 0; i < (int) listLength; i++) {
                    String foodName = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodName").getValue().toString();
                    String foodType = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodType").getValue().toString();
                    String foodDate = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodDate").getValue().toString();
                    String imageURI = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("imageURI").getValue().toString();
                    foodList.add(new foodItem(foodName, foodType, foodDate, imageURI));
                }
                addFoodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (foodNameET.getText().toString().length() > 0 && foodDateET.getText().toString().length() > 0) {
                            if (!fruitB && !veggieB && !grainsB && !proteinB && !dairyB) {
                                Toast.makeText(addFoodInfo.this, "Please Select A Food Type", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.show();
                                foodList.add(new foodItem(foodNameET.getText().toString(), foodType, foodDateET.getText().toString(), imageURI));
                                databaseReference.child(user.getUid()).child("Foods").child("Food Info List").setValue(foodList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();

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
                                        if (foodType.equals("Fruit")) {
                                            fruitCount++;
                                            databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").setValue(fruitCount);
                                        }
                                        if (foodType.equals("Vegetable")) {
                                            vegeCount++;
                                            databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").setValue(vegeCount);
                                        }
                                        if (foodType.equals("Grains")) {
                                            grainCount++;
                                            databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").setValue(grainCount);
                                        }
                                        if (foodType.equals("Protein")) {
                                            proteinCount++;
                                            databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").setValue(proteinCount);
                                        }
                                        if (foodType.equals("Dairy")) {
                                            dairyCount++;
                                            databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Dairy").setValue(dairyCount);
                                        }

                                        Toast.makeText(addFoodInfo.this, "Food Added !", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), userAccount.class);
                                        startActivity(i);
                                    }
                                });
                            }

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fruitB = false;
        veggieB = false;
        grainsB = false;
        proteinB = false;
        dairyB = false;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_fruitRB:
                        fruitB = true;
                        veggieB = false;
                        grainsB = false;
                        proteinB = false;
                        dairyB = false;
                        foodType = "Fruit";
                        break;
                    case R.id.id_vegiRB:
                        fruitB = false;
                        veggieB = true;
                        grainsB = false;
                        proteinB = false;
                        dairyB = false;
                        foodType = "Vegetable";
                        break;
                    case R.id.id_grainsRB:
                        fruitB = false;
                        veggieB = false;
                        grainsB = true;
                        proteinB = false;
                        dairyB = false;
                        foodType = "Grains";
                        break;
                    case R.id.id_proteinRB:
                        fruitB = false;
                        veggieB = false;
                        grainsB = false;
                        proteinB = true;
                        dairyB = false;
                        foodType = "Protein";
                        break;
                    case R.id.id_dairyRB:
                        fruitB = false;
                        veggieB = false;
                        grainsB = false;
                        proteinB = false;
                        dairyB = true;
                        foodType = "Dairy";
                        break;
                }
            }
        });
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Food Images").child("foodImage.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("TAG", "Image URI: " + uri.toString());
                imageURI = uri.toString();
                Glide.with(getApplicationContext()).load(imageURI).into(foodImageView);
            }
        });


    }

    public void setIds() {
        foodImageView = findViewById(R.id.id_imageforFoodItem);
        radioGroup = findViewById(R.id.id_radioGroup);
        addFoodButton = findViewById(R.id.id_AddFoodButton);
        foodNameET = findViewById(R.id.id_foodNameET);
        foodDateET = findViewById(R.id.id_dateET);
    }

}