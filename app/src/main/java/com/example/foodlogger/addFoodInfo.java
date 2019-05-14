package com.example.foodlogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

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
    AlertDialog spotsAlertDialog;
    FirebaseVisionImage firebaseVisionImage;
    ArrayList<String> imageLabels;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "STARTED");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_info);
        Log.d("TAG", "STARTED");

        setIds();
        imageLabels = new ArrayList<>();
        spotsAlertDialog = new SpotsDialog.Builder().setCancelable(false).setMessage("Retrieving Data...").setContext(this).build();
        spotsAlertDialog.show();
        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
        Date c = Calendar.getInstance().getTime();
        String formattedDate = curFormater.format(c);
        foodDateET.setText(formattedDate);
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
                                spotsAlertDialog.setMessage("Adding Food...");
                                spotsAlertDialog.show();
                                foodList.add(0, new foodItem(foodNameET.getText().toString(), foodType, foodDateET.getText().toString(), imageURI));
                                databaseReference.child(user.getUid()).child("Foods").child("Food Info List").setValue(foodList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        spotsAlertDialog.dismiss();

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

                                        Toast.makeText(getApplicationContext(), "Food Added !", Toast.LENGTH_SHORT).show();
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
                imageURI = uri.toString();
                Glide.with(getApplicationContext()).load(imageURI).into(foodImageView);
                spotsAlertDialog.dismiss();

                try {
                    Glide.with(getApplicationContext()).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap myBitmap = resource;
                            firebaseVisionImage = FirebaseVisionImage.fromBitmap(myBitmap);
                            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
                            labeler.processImage(firebaseVisionImage)
                                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                            imageLabels = new ArrayList<>();
                                            for (int i = 0; i < 5; i++) {
                                                if (labels.size() > i) {
                                                    imageLabels.add(labels.get(i).getText());
                                                }
                                            }
                                            final Dialog dialog = new Dialog(addFoodInfo.this);
                                            dialog.setContentView(R.layout.choose_food_dialog);
                                            dialog.setTitle("Choose Food Name");
                                            dialog.setCanceledOnTouchOutside(false);
                                            Button select1 = dialog.findViewById(R.id.id_selectButton1);
                                            Button select2 = dialog.findViewById(R.id.id_selectButton2);
                                            Button select3 = dialog.findViewById(R.id.id_selectButton3);
                                            Button select4 = dialog.findViewById(R.id.id_selectButton4);
                                            Button select5 = dialog.findViewById(R.id.id_selectButton5);
                                            Button noneButton = dialog.findViewById(R.id.id_cancelButton);
                                            final TextView selectText1 = dialog.findViewById(R.id.id_selectText1);
                                            final TextView selectText2 = dialog.findViewById(R.id.id_selectText2);
                                            final TextView selectText3 = dialog.findViewById(R.id.id_selectText3);
                                            final TextView selectText4 = dialog.findViewById(R.id.id_selectText4);
                                            final TextView selectText5 = dialog.findViewById(R.id.id_selectText5);
                                            for (int i = 1; i <= 5; i++) {
                                                if (i == 1) {
                                                    selectText1.setText("" + imageLabels.get(i - 1));
                                                }
                                                if (i == 2) {
                                                    selectText2.setText("" + imageLabels.get(i - 1));
                                                }
                                                if (i == 3) {
                                                    selectText3.setText("" + imageLabels.get(i - 1));
                                                }
                                                if (i == 4) {
                                                    selectText4.setText("" + imageLabels.get(i - 1));
                                                }
                                                if (i == 5) {
                                                    selectText5.setText("" + imageLabels.get(i - 1));
                                                }
                                            }
                                            select1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    foodNameET.setText(imageLabels.get(0));
                                                    dialog.dismiss();
                                                }
                                            });
                                            select2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    foodNameET.setText(imageLabels.get(1));
                                                    dialog.dismiss();
                                                }
                                            });
                                            select3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    foodNameET.setText(imageLabels.get(2));
                                                    dialog.dismiss();
                                                }
                                            });
                                            select4.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    foodNameET.setText(imageLabels.get(3));
                                                    dialog.dismiss();
                                                }
                                            });
                                            select5.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    foodNameET.setText(imageLabels.get(4));
                                                    dialog.dismiss();

                                                }
                                            });
                                            noneButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            dialog.show();

                                            Log.d("TAG", "Labels: " + imageLabels);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
                        }
                    });
                } catch (Exception e) {

                }
            }
        }); //adjust confidence level of image labeler

    }

    public void setIds() {
        foodImageView = findViewById(R.id.id_imageforFoodItem);
        radioGroup = findViewById(R.id.id_radioGroup);
        addFoodButton = findViewById(R.id.id_AddFoodButton);
        foodNameET = findViewById(R.id.id_foodNameET);
        foodDateET = findViewById(R.id.id_dateET);
    }

}