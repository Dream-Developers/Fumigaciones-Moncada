package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView imageView;
    //MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.imgSplash);
        //mediaPlayer = MediaPlayer.create(this, R.raw.maraca);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animacion);
        imageView.startAnimation(animation);



        Thread timer = new Thread(){

            public void run(){
                try {
                    sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    int splash = Integer.parseInt(preferences.getString("splash", "0"));

                    if (splash==1){
                        Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        editor.putString("splash", "1");
                        editor.commit();

                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);

                        Intent i2 = new Intent(SplashActivity.this, ContenedorActivity.class);
                        startActivity(i2);
                    }
                    finish();
                }
            }
        };
        timer.start();
        //mediaPlayer.start();
    }
}
