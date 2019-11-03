package com.example.fumigacionesmoncada;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.fumigacionesmoncada.Providers.ContractParaListaUsers;
import com.example.fumigacionesmoncada.ui.chat.ChatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private EditText txtCorreo, txtContrasena;
    private TextView recuperarContra;
    private Button btn_registro, btn_login;
    private RadioButton RBsesion;
    // private static String URL_LOGIN = "http://192.168.0.101/api/auth/login";
    ProgressDialog dialogo_progreso;
    RequestQueue solicitar_cola;
    ProgressBar cargando;
    JsonObjectRequest solicitar_objeto_json;
    String success;
    String rol_id;



    //Shared Preferences
    //private SharedPreferences sharedPreferences;
    //private SharedPreferences.Editor editor;
    private boolean isActivateRadioButton;

    private static final String STRING_PREFERENCES = "example.preferencias";
    private static  final String PREFERENCE_ESTADO_BUTTON = "estado.button";



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (obtenerEstadoButton()){
            cargarPreferencias();
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

        RBsesion.setVisibility(View.GONE);


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



        Cursor cursor = getContentResolver().query(ContractParaListaUsers.CONTENT_URI, null,
                ContractParaListaUsers.Columnas.ROL+"=? or "+
                        ContractParaListaUsers.Columnas.ROL+"=?",
                new String[]{"1","2"},null,null);

        cursor.moveToNext();
        Log.i("USUARIO",""+cursor.getCount());

        try{
            if (cursor.getCount()==1) {

                Intent intent = new Intent(MainActivity.this, ChatFragment.class);
                startActivity(intent);
                finish();

            } else {

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((txtCorreo.getText().toString().trim().length() > 0) && (txtContrasena.getText().toString().trim().length() > 0)) {
                    if(!isEmailValid(txtCorreo.getText())){
                        txtCorreo.setError("No es un correo valido");
                    }else {
                        String email = txtCorreo.getText().toString().trim();
                        String contraseniaPass = txtContrasena.getText().toString().trim();
                        login(email, contraseniaPass);
                    }
                }
                else {
                    if (txtCorreo.getText().toString().length() == 0 ||
                            txtCorreo.getText().toString().trim().equalsIgnoreCase("")) {
                        txtCorreo.setError("Ingresa el correo");

                    }
                    if (txtContrasena.getText().toString().trim().length() == 0 ||
                            txtContrasena.getText().toString().trim().equalsIgnoreCase("")) {
                        txtContrasena.setError("Ingresa la contraseña");
                    }
                }
            }
        });


        //ojo
             }


        }catch (Exception exc){
            Log.i("Login_Activity",""+exc);
        }

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


        String email = preferences.getString("email", "");
        String contra = preferences.getString("contra", "");
        rol_id = preferences.getString("rol", "");

//        txtCorreo.setText(email);
  //      txtContrasena.setText(contra);

    }


    private void login(final String txtCorreo, final String txtContrasena){

        String ip = getString(R.string.ip);

        String url = ip + "/api/auth/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        guardarEstadoButton();
                        //Toast.makeText(MainActivity.this, "Si responde"+response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                             success = jsonObject.getString("access_token");
                             rol_id = jsonObject.getString("rol_id");

                            //Toast.makeText(MainActivity.this, "Si manda los resultados"+rol_id , Toast.LENGTH_LONG).show();

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
//                            Toast.makeText(MainActivity.this, "Error al iniciar sesión"+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        //
                        //Bienvenido();
                        savePreferences(success);
                        intem();
                        finish();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión, verifique que su " +
                                "contraseña esté correcta ", Toast.LENGTH_SHORT).show();

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


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void intem(){

        if (rol_id.equals("1")){

            Intent intent = new Intent(getApplicationContext(), NavegacionAdministradorActivity.class);
            startActivity(intent);
        }else {
            Intent i = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(i);
        }


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


    private void savePreferences(String token){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        String correo = txtCorreo.getText().toString();
        String contra = txtContrasena.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", correo);
        editor.putString("token", token);
        editor.putString("password", contra);
        editor.putString("rol", rol_id);
        editor.commit();

    }

    public void botonregistrar(View view) {
        Intent i = new Intent(getApplicationContext(), RegistarUsuarioNuevo.class);
        startActivity(i);

    }

    public void resetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), PasswordReset.class);
        startActivity(intent);
    }
}