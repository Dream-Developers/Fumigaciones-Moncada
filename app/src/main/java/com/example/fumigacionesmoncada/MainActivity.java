package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText txtCorreo, txtContrasena;
    private Button btn_registro, btn_login;
    private static String URL_LOGIN = "http://";
    ProgressDialog dialogo_progreso;
    //RequestQueue solicitar_cola;
    ProgressBar cargando;
    JsonObjectRequest solicitar_objeto_json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCorreo = findViewById(R.id.idCorreoLogin);
        txtContrasena = findViewById(R.id.idContraseñaLogin);
        btn_login = findViewById(R.id.idLoginLogin);
        btn_registro = findViewById(R.id.idRegistroLogin);

        /*btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = txtCorreo.getText().toString().trim();
                String mPassword = txtContrasena.getText().toString().trim();

                if (!mEmail.isEmpty() || !mPassword.isEmpty()) {
                    login(mEmail, mPassword);
                } else {
                    Toast.makeText(MainActivity.this, "Al menos un campo está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistarUsuarioNuevo.class));
            }
        });*/
    }

            /*private void login(String txtCorreo, String txtContrasena){

                cargando.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.GONE);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                                    if (success.equals("1")){
                                        for(int i = 0; i < jsonArray.length(); i++){
                                            JSONObject object = jsonArray.getJSONObject(i);
                                            String name = object.getString("name").trim();
                                            String email = object.getString("email").trim();
                                            Toast.makeText(MainActivity.this, "Se ha logeado. " +
                                                    " \nTu nombre : " +name+"" +
                                                    "\nTu correo :" +email, Toast.LENGTH_SHORT).show();
                                            cargando.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cargando.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Error al iniciar sesión"+e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                cargando.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "Error al iniciar sesión"+error.toString(), Toast.LENGTH_SHORT).show();

                            }
                        })

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("email", email);
                        parametros.put("password", password);
                        return parametros;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

    }*/
   public void registro(View view) {
        Intent i = new Intent(getApplication(),RegistarUsuarioNuevo.class);
        startActivity(i);
    }
}