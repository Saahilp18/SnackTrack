package com.example.foodlogger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import dmax.dialog.SpotsDialog;

public class signupIntent extends AppCompatActivity {
    EditText firstNameET, lastNameEt, emailET, passwordET, reenterPasswordET;
    Boolean firstNameB, lastNameB, emailB, passwordB, reenterPasswordB, passMatch;
    Button creatAccountButton;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_intent);
        setIds();
        alertDialog = new SpotsDialog.Builder().setCancelable(false).setMessage("Making Account...").setContext(this).build();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firstNameB = false;
        lastNameB = false;
        emailB = false;
        passwordB = false;
        reenterPasswordB = false;
        passMatch = false;
        firstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firstNameB = s.toString() != "";
            }
        });
        lastNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                lastNameB = s.toString() != "";
            }
        });
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailB = s.toString() != "";
            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordB = s.toString() != "";
            }
        });
        reenterPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reenterPasswordB = s.toString() != "";
            }
        });
        auth = FirebaseAuth.getInstance();
        creatAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNameB && lastNameB && emailB && passwordB && reenterPasswordB) {//if everything is filled out
                    if (passwordET.getText().toString().equals(reenterPasswordET.getText().toString())) {//if passwords match
                        if (passwordET.getText().toString().length() >= 6) {
                            finish();
                            alertDialog.show();
                            createUser();
                        } else {
                            Toast.makeText(signupIntent.this, "Passwords must be greater than 6 characters", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(signupIntent.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(signupIntent.this, "Please fill out every box", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUser() {
        final String email = emailET.getText().toString().trim();
        final String password = passwordET.getText().toString().trim();
        final String firstName = firstNameET.getText().toString().trim();
        final String lastName = lastNameEt.getText().toString().trim();
        alertDialog.dismiss();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    final User newUser = new User(email, password);
                    databaseReference.child(user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child(user.getUid()).child("Name").child("First Name").setValue(firstName);
                                databaseReference.child(user.getUid()).child("Name").child("Last Name").setValue(lastName);
                                databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").setValue(0);
                                databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").setValue(0);
                                databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").setValue(0);
                                databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").setValue(0);
                                databaseReference.child(user.getUid()).child("Foods").child("Type Counts").child("Dairy").setValue(0);
                                alertDialog.dismiss();

                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("email", emailET.getText().toString().trim());
                                    jsonObject.put("password", passwordET.getText().toString().trim());
                                    OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("info.json", MODE_PRIVATE));
                                    writer.write(jsonObject.toString());
                                    writer.close();
                                } catch (Exception e) {

                                }

                                Intent i = new Intent(getApplicationContext(), userAccount.class);
                                startActivity(i);
                                //finish();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Account Could not be made", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void setIds() {
        firstNameET = findViewById(R.id.id_firstNameET);
        lastNameEt = findViewById(R.id.id_lastNameET);
        emailET = findViewById(R.id.id_emailET);
        passwordET = findViewById(R.id.id_passwordET);
        reenterPasswordET = findViewById(R.id.id_reenterPasswordET);
        creatAccountButton = findViewById(R.id.id_creatButton);
    }
}
