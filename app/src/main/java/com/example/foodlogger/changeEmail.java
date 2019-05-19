package com.example.foodlogger;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class changeEmail extends AppCompatActivity {
    EditText newEmail1, newEmail2;
    Button changeEmailConfirmButton;
    DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        newEmail1 = findViewById(R.id.id_newEmail1);
        newEmail2 = findViewById(R.id.id_newEmail2);
        changeEmailConfirmButton = findViewById(R.id.id_emailChangeConfirmButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        changeEmailConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newEmail1.getText().toString().trim().length() > 0) {
                    if (newEmail1.getText().toString().trim().equals(newEmail1.getText().toString().trim())) {
                        databaseRef.child(user.getUid()).child("email").setValue(newEmail1.getText().toString().trim());
                        user.updateEmail(newEmail1.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(changeEmail.this, "Email Changed!", Toast.LENGTH_SHORT).show();
                                try {
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("info.json")));
                                    JSONObject jsonObject = new JSONObject(bufferedReader.readLine());
                                    jsonObject.remove("email");
                                    jsonObject.put("email", newEmail1.getText().toString().trim());
                                    OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("info.json", MODE_PRIVATE));
                                    writer.write(jsonObject.toString());
                                    writer.close();
                                } catch (Exception e) {

                                }
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(changeEmail.this, "Could not change email.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(changeEmail.this, "Email do not match.", Toast.LENGTH_SHORT).show();
                        newEmail2.setText("");
                    }
                }
            }
        });


    }
}
