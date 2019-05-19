package com.example.foodlogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class changeUsername extends AppCompatActivity {
    EditText firstnameET, lastnameET;
    Button changeUsernameConfirmButton;
    DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);
        firstnameET = findViewById(R.id.id_oldEmail);
        lastnameET = findViewById(R.id.id_newEmail1);
        changeUsernameConfirmButton = findViewById(R.id.id_changeUserConfirm);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        changeUsernameConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstnameET.getText().toString().trim().length() > 0 && lastnameET.getText().toString().trim().length() > 0) {
                    databaseRef.child(user.getUid()).child("Name").child("First Name").setValue(firstnameET.getText().toString().trim());
                    databaseRef.child(user.getUid()).child("Name").child("Last Name").setValue(lastnameET.getText().toString().trim());
                    finish();
                } else {
                    Toast.makeText(changeUsername.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
