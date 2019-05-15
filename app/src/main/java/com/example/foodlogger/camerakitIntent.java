package com.example.foodlogger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class camerakitIntent extends AppCompatActivity {
    Button captureButton;
    CameraView cameraKitView;
    AlertDialog spotsAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerakit_intent);
        cameraKitView = findViewById(R.id.id_cameraKitView);
        captureButton = findViewById(R.id.id_captureButton);
        cameraKitView.setFocus(CameraKit.Constants.FOCUS_TAP);
        cameraKitView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);
        cameraKitView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraKitView.setFlash(CameraKit.Constants.FLASH_AUTO);
        spotsAlertDialog = new SpotsDialog.Builder().setCancelable(false).setMessage("Taking Picture...").setContext(this).build();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotsAlertDialog.show();
                cameraKitView.start();
                cameraKitView.captureImage();
            }
        });
        cameraKitView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                Matrix matrix = new Matrix();

                //matrix.postRotate(90);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                rotatedBitmap = scaleDownBitmap(rotatedBitmap, 100, getApplicationContext());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                Intent sendInfoBack = new Intent();
                sendInfoBack.putExtra("byteArray", byteArray);
                setResult(RESULT_OK, sendInfoBack);
                finish();
                spotsAlertDialog.dismiss();
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraKitView.stop();
    }

    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }
}
