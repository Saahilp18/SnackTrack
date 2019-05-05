package com.example.foodlogger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class addFoodFragment extends Fragment {
    ArrayList<foodItem> foodList;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    Button takePictueButton, choosePictureButton;
    static final int SELECT_IMAGE = 1;
    static final int TAKE_IMAGE = 2;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_addfooditems, container, false);
        takePictueButton = fragmentView.findViewById(R.id.id_takePictureButton);
        choosePictureButton = fragmentView.findViewById(R.id.id_choosePictureButton);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Food Images").child("foodImage.jpg");
        foodList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });


        takePictueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), camerakitIntent.class);
                startActivityForResult(intent, TAKE_IMAGE);
            }
        });

        return fragmentView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        final Intent addInfoIntent = new Intent(getContext(), addFoodInfo.class);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] byteArray = baos.toByteArray();
                        UploadTask uploadTask = storageReference.putBytes(byteArray);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addInfoIntent.putExtra("imageURI", uri.toString());
                            }
                        });
                        startActivity(addInfoIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == TAKE_IMAGE) {
                final Intent addInfoIntent = new Intent(getContext(), addFoodInfo.class);
                Log.d("TAG", "WORKKK:" + data.getByteArrayExtra("byteArray").toString());

                UploadTask uploadTask = storageReference.putBytes(data.getByteArrayExtra("byteArray"));
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "IMAGE UPLOAD FAILED");
                    }
                });
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        addInfoIntent.putExtra("imageURI", uri.toString());
                    }
                });
                startActivity(addInfoIntent);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
