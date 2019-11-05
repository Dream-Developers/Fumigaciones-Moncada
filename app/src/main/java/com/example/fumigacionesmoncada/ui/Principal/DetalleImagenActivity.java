package com.example.fumigacionesmoncada.ui.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fumigacionesmoncada.R;

public class DetalleImagenActivity extends AppCompatActivity {

    private TextView titulo, descripcion;
    private ImageView imagen;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_imagen);
         titulo = findViewById(R.id.tituloImagenD);
         descripcion= findViewById(R.id.descripcionImagenD);
         imagen= findViewById(R.id.idImagenD);
        id = getIntent().getStringExtra("id_cliente");
    }


}
