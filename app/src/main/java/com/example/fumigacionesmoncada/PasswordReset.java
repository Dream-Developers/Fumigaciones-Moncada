package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordReset extends AppCompatActivity {


    EditText correo;
    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        correo = (EditText) findViewById(R.id.txtCorreResetPass);
        enviar = (Button) findViewById(R.id.btnEnviarCorreo);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Funcion sin programar", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void enviarCorreo(View view) {
    }
}
