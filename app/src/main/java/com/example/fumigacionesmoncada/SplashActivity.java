package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.imgSplash);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animacion);
        imageView.startAnimation(animation);



        Thread timer = new Thread(){

            public void run(){
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);

                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
