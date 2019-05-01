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

public class changePasswordIntent extends AppCompatActivity {
    EditText oldPassword, newPass1, newPass2;
    Button changePassConfirmButton;
    DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_intent);
        oldPassword = findViewById(R.id.id_currentPassword);
        newPass1 = findViewById(R.id.id_newPass1);
        newPass2 = findViewById(R.id.id_newPass2);
        changePassConfirmButton = findViewById(R.id.id_changePasswordConfirm);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        changePassConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPass1.getText().toString().trim().equals(newPass2.getText().toString().trim())) {
                    if (newPass1.length() > 6) {
                        databaseRef.child(user.getUid()).child("password").setValue(newPass1.getText().toString().trim());
                        Toast.makeText(changePasswordIntent.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(changePasswordIntent.this, "Password must be greater than 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(changePasswordIntent.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    newPass1.setText("");
                    newPass2.setText("");
                }
            }
        });


    }
}
