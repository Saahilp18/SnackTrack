package com.example.foodlogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class testThing extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_thing);
        imageView = findViewById(R.id.id_testImageVIEW);
        String uri = getIntent().getExtras().getString("imageURI");
        Log.d("TAG", "TESTTHING: " + uri);
        Glide.with(this).load(uri).into(imageView);
    }
}
