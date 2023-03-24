package com.example.imole;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class splash_screen extends AppCompatActivity {
    private ImageView Logo;
    private ImageView logotitletext;
    private static int splashTimeOut=3500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Logo = findViewById(R.id.bulb_logo);
        logotitletext = findViewById(R.id.logo_title_text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(splash_screen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);
    }
}