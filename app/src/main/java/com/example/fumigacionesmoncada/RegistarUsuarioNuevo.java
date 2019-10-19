package com.example.fumigacionesmoncada;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
                progreso = new ProgressDialog(this);
                progreso.setMessage("Cargando...");
                progreso.show();

                String url = "http://192.168.0.101/api/auth/signup?name=" + nombre.getText().toString()
                        + "&lasname=" + apellidos.getText().toString()+  "&telefono="
                        + telefono.getText().toString()+ "&email=" + correo.getText().toString()+"&password=" + contraseña.getText().toString()+ "&password_confirmation=" + confcontra.getText().toString();
                url = url.replace(" ", "%20");
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this);
                request.add(jsonObjectRequest);
            }

        }catch (Exception exe){
            Toast.makeText(this,exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

        @Override
        public void onErrorResponse (VolleyError error){
            progreso.hide();
            Toast.makeText(getApplicationContext(),"No se pudo registrar , Hubo un error al conectar por favor verifica la conexión a internet o intente nuevamente o su correo ya esta registrado , Error : "+ error.toString(), Toast.LENGTH_LONG).show();

            Log.i("ERROR", error.toString());



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
}

