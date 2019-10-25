package com.example.fumigacionesmoncada;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;


public class RegistarUsuarioNuevo extends AppCompatActivity implements
        Response.Listener<JSONObject>, Response.ErrorListener{
    EditText nombre,contraseña,correo,confcontra, telefono , apellidos;
    TextView col;
    Button registrar;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ProgressBar pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario_nuevo);
        confcontra =  findViewById(R.id.comprobacion_contraseña);
        nombre =  findViewById(R.id.registro_nombres);
        correo = findViewById(R.id.registro_correo);
        telefono = findViewById(R.id.registro_telefono);
        apellidos = findViewById(R.id.registro_apellidos);
        contraseña = findViewById(R.id.registro_contraseña);
        registrar =  findViewById(R.id.registrar);
        request = Volley.newRequestQueue(this);

        nombre.setSingleLine(false);
        nombre.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        correo.setSingleLine(false);
        correo.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        apellidos.setSingleLine(false);
        apellidos.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        telefono.setSingleLine(false);
        telefono.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);

    }
    public void cargarwebservice(){

        try {
            if(nombre.getText().toString().equals("")||correo.getText().toString().equals("")||confcontra.getText().toString().equals("")||contraseña.getText().toString().equals("")
                    || telefono.getText().toString().equals("") || apellidos.getText().toString().equals("")){
                Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
            }else {
                if (contraseña.getText().toString().length() < 8 || confcontra.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "La contraseñia no debe ser menor a ocho caracteres", Toast.LENGTH_LONG).show();
                } else {
                    if (telefono.getText().toString().length() < 8 ) {
                        Toast.makeText(getApplicationContext(), "No es un numero Telefonico", Toast.LENGTH_LONG).show();
                    } else {
                        if (contraseña.getText().toString().equals(confcontra.getText().toString())) {
                            if (!validarEmail(correo.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Correo no valido", Toast.LENGTH_LONG).show();
                            } else {

                                progreso = new ProgressDialog(this);
                                progreso.setMessage("Cargando...");
                                progreso.show();
                                String ip = getString(R.string.ip);
                                String url = ip + "/api/auth/signup?name=" + nombre.getText().toString()
                                        + "&recidencia=" + apellidos.getText().toString() + "&telefono="
                                        + telefono.getText().toString() + "&email=" + correo.getText().toString() + "&password=" + contraseña.getText().toString() + "&password_confirmation=" + confcontra.getText().toString();
                                url = url.replace(" ", "%20");
                                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this);
                                request.add(jsonObjectRequest);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "nueva password con confirmar password no coinciden", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }catch (Exception exe){
            Toast.makeText(this,exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

        @Override
        public void onErrorResponse (VolleyError error){
            progreso.hide();
            if (error.toString().equals("com.android.volley.ServerError")) {
                Toast.makeText(getApplicationContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

            } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                Toast.makeText(getApplicationContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
            } else {
            }
          //  Toast.makeText(getApplicationContext(),"No se pudo registrar , Hubo un error al conectar por favor verifica la conexión a internet o intente nuevamente o su correo ya esta registrado , Error : "+ error.toString(), Toast.LENGTH_LONG).show();

           // Log.i("ERROR", error.toString());
        }





        @Override
        public void onResponse (JSONObject response){

            Toast.makeText(this,"Se registrado correctamente", Toast.LENGTH_SHORT).show();
            progreso.hide();
            nombre.setText("");
            correo.setText("");
            confcontra.setText("");
            contraseña.setText("");
            telefono.setText("");
            apellidos.setText("");
        }

    public void registrar(View view) {
        cargarwebservice();
    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}

