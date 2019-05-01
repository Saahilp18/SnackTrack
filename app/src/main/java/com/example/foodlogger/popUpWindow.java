package com.example.foodlogger;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class popUpWindow extends Activity {
    TextView foodNameTV, foodGroupTV, dateTV;
    ImageView foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);
        foodNameTV = findViewById(R.id.id_foodNamepopUpId);
        foodGroupTV = findViewById(R.id.id_foodGroupPopupTV);
        dateTV = findViewById(R.id.id_datePopUpTV);
        foodImage = findViewById(R.id.id_popupImageFood);
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
        Bundle stuff = this.getIntent().getExtras();
        foodNameTV.setText(stuff.getString("name").trim());
        foodGroupTV.setText(stuff.getString("type").trim());
        dateTV.setText(stuff.getString("date").trim());
        Glide.with(getApplicationContext()).load(stuff.getString("imageURI")).into(foodImage);

    }
}
