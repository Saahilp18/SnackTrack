package com.example.foodlogger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

public class settingsFragment extends Fragment {
    Button changePasswordButton, logoutButton, changeUserButton, changeEmailButton;
    DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        changePasswordButton = fragmentView.findViewById(R.id.id_changePasswordButton);
        logoutButton = fragmentView.findViewById(R.id.id_logoutButton);
        changeUserButton = fragmentView.findViewById(R.id.id_changeUserButton);
        changeEmailButton = fragmentView.findViewById(R.id.id_changeEmailButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        databaseRef = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePasswordIntent = new Intent(getActivity(), changePasswordIntent.class);
                startActivity(changePasswordIntent);
            }
        });
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeEmailIntent = new Intent(getActivity(), changeEmail.class);
                startActivity(changeEmailIntent);
            }
        });
        changeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeUserIntent = new Intent(getActivity(), changeUsername.class);
                startActivity(changeUserIntent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            OutputStreamWriter writer = new OutputStreamWriter(getActivity().openFileOutput("info.json", MODE_PRIVATE));
                            writer.write(jsonObject.toString());
                            writer.close();
                        } catch (Exception e) {

                        }
                        auth.signOut();

                        Intent returntoMainIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(returntoMainIntent);
                    }
                });  //yes/confirm button

                alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alert.setTitle("Are you sure you want to log out?");
                // alert.setMessage("Here is my message");

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });
        return fragmentView;
    }
}
