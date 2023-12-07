package com.example.hoaxhound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button OpenCameraViewButton = findViewById(R.id.open_camera_view_button);
//
//        OpenCameraViewButton.setOnClickListener(view -> {
//            Intent I = new Intent(MainActivity.this,CameraDetect.class);
//            startActivity(I);
//        });

        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), CameraDetect.class));
            finish();
        }, 1500);

    }
}