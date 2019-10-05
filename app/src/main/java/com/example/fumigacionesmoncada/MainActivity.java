package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registro = findViewById(R.id.resgistro);
    }



    public void resgistro(View view) {
        Intent i = new Intent(getApplication(),RegistarUsuarioNuevo.class);
        startActivity(i);
    }
}
