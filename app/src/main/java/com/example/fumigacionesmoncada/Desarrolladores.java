package com.example.fumigacionesmoncada;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Desarrolladores extends AppCompatActivity {
    TextView nombre_desa1, nombre_desa2, nombre_desa3, nombre_desa4, nombre_desa5;
    TextView cel_desa1, cel_desa2,cel_desa3, cel_desa4, cel_desa5;
    TextView correo_desa1,correo_desa2,correo_desa3,correo_desa4,correo_desa5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desarrolladores);

        nombre_desa1 = findViewById(R.id.nombre_desa1);
        nombre_desa2 = findViewById(R.id.nombre_desa2);
        nombre_desa3 = findViewById(R.id.nombre_desa3);
        nombre_desa4 = findViewById(R.id.nombre_desa4);
        nombre_desa5 = findViewById(R.id.nombre_desa5);
        cel_desa1 = findViewById(R.id.cel_desa1);
        cel_desa2 = findViewById(R.id.cel_desa2);
        cel_desa3 = findViewById(R.id.cel_desa3);
        cel_desa4 = findViewById(R.id.cel_desa4);
        cel_desa5 = findViewById(R.id.cel_desa5);
        correo_desa1 = findViewById(R.id.correo_desa1);
        correo_desa2 = findViewById(R.id.correo_desa2);
        correo_desa3 = findViewById(R.id.correo_desa3);
        correo_desa4 = findViewById(R.id.correo_desa4);
        correo_desa5 = findViewById(R.id.correo_desa5);


    }

}
