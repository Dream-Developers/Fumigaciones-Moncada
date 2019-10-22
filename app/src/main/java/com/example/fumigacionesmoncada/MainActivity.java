package com.example.fumigacionesmoncada;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
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

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private EditText txtCorreo, txtContrasena;
    private TextView recuperarContra;
    private Button btn_registro, btn_login;
    private RadioButton RBsesion;
    private static String URL_LOGIN = "http://192.168.0.111/api/auth/login";
    ProgressDialog dialogo_progreso;
    RequestQueue solicitar_cola;
    ProgressBar cargando;
    JsonObjectRequest solicitar_objeto_json;


    //Shared Preferences
    //private SharedPreferences sharedPreferences;
    //private SharedPreferences.Editor editor;
    private boolean isActivateRadioButton;
    private static final String STRING_PREFERENCES = "example.preferencias";
    private static  final String PREFERENCE_ESTADO_BUTTON = "estado.button";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (obtenerEstadoButton()){
            intem();
            finish();
        }

        txtCorreo = findViewById(R.id.idCorreoLogin);
        txtContrasena = findViewById(R.id.idContraseñaLogin);
        recuperarContra = findViewById(R.id.recuperarPass);
        RBsesion = findViewById(R.id.noSalir);
        btn_login = findViewById(R.id.idLoginLogin);
        btn_registro = findViewById(R.id.idRegistroLogin);

        isActivateRadioButton = RBsesion.isChecked();

        RBsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActivateRadioButton){
                    RBsesion.setChecked(false);
                }
                isActivateRadioButton = RBsesion.isChecked();
            }
        });


        cargarPreferencias();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = txtCorreo.getText().toString().trim();
                String mPassword = txtContrasena.getText().toString().trim();

                if (!mEmail.isEmpty() || !mPassword.isEmpty() || !isEmailValid(mEmail)) {
                    login(mEmail, mPassword);
                    //Bienvenido();


                }
                else {
                    Toast.makeText(MainActivity.this, "Al menos un campo está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistarUsuarioNuevo.class));
            }
        });
    }

    public void guardarEstadoButton(){

        SharedPreferences preferencias = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferencias.edit().putBoolean(PREFERENCE_ESTADO_BUTTON, RBsesion.isChecked()).apply();
    }

    public boolean obtenerEstadoButton(){

        SharedPreferences preferencias = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferencias.getBoolean(PREFERENCE_ESTADO_BUTTON, false);
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);


        String email = preferences.getString("email", "jacob07@gmail.com");
        String contra = preferences.getString("contra", "secret");

        txtCorreo.setText(email);
        txtContrasena.setText(contra);

    }


    private void login(final String txtCorreo, final String txtContrasena){

//         btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        guardarEstadoButton();
                        Toast.makeText(MainActivity.this, "Si responde"+response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("access_token");
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
                            Toast.makeText(MainActivity.this, "Error al iniciar sesión"+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        //
                        Bienvenido();
                        savePreferences();
                        intem();
                        finish();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión, verifique que su " +
                                "contraseña esté correcta "+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("email", txtCorreo);
                parametros.put("password", txtContrasena);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
}

    public void registro(View view) {
        Intent i = new Intent(getApplication(),RegistarUsuarioNuevo.class);
        startActivity(i);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void intem(){

        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(i);

    }

    public void Bienvenido(){
        LayoutInflater inflater= (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View customToast=inflater.inflate(R.layout.toas_personalizado,null);
        TextView txt= (TextView)customToast.findViewById(R.id.txtToast);
        txt.setText("Bienvenido, Gracias por preferirnos. " +
                " Sea feliz, que Dios lo bendiga ");
        Toast toast =new Toast(this);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToast);
        toast.show();

        //Agregar arriba Bienvenido();
    }


    private void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        String correo = txtCorreo.getText().toString();
        String contra = txtContrasena.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", correo);
        editor.putString("password", contra);
        editor.commit();

    }
}