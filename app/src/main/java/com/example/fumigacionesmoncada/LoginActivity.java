package com.example.fumigacionesmoncada;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
import android.graphics.Paint;
import android.util.Patterns;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.Providers.ContractParaListaUsers;
import com.example.fumigacionesmoncada.ui.Mensajes.MensajesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText txtCorreo, txtContrasena;
    private TextView recuperarContra, registrar;
    private Button btn_login;
    private RadioButton RBsesion;
    // private static String URL_LOGIN = "http://192.168.0.101/api/auth/login";
    ProgressDialog dialogo_progreso;
    RequestQueue solicitar_cola;
    ProgressBar cargando;
    JsonObjectRequest solicitar_objeto_json;
    String success;
    String rol_id;
    String firebase;
    String usuario_id;


    //Shared Preferences
    //private SharedPreferences sharedPreferences;
    //private SharedPreferences.Editor editor;
    private boolean isActivateRadioButton;

    private static final String STRING_PREFERENCES = "example.preferencias";
    private static final String PREFERENCE_ESTADO_BUTTON = "estado.button";
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    FirebaseUser user;


    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (obtenerEstadoButton()) {
            cargarPreferencias();
            intem();
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId();
        txtCorreo = findViewById(R.id.idCorreoLogin);
        txtContrasena = findViewById(R.id.idContraseñaLogin);
        recuperarContra = findViewById(R.id.recuperarPass);
        recuperarContra.setPaintFlags(recuperarContra.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        RBsesion = findViewById(R.id.noSalir);
        btn_login = findViewById(R.id.idLoginLogin);
        registrar = findViewById(R.id.registarLogin);
        isActivateRadioButton = RBsesion.isChecked();


        String text = getString(R.string.cuenta);

        SpannableString ss = new SpannableString(text);

        StyleSpan boldSpan = new StyleSpan(Typeface.NORMAL);
        StyleSpan italicSpan = new StyleSpan(Typeface.BOLD);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();

        ss.setSpan(boldSpan, 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(italicSpan, 22, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        registrar.setText(ss);


        RBsesion.setVisibility(View.GONE);


        RBsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActivateRadioButton) {
                    RBsesion.setChecked(false);
                }
                isActivateRadioButton = RBsesion.isChecked();
            }
        });


        cargarPreferencias();


        Cursor cursor = getContentResolver().query(ContractParaListaUsers.CONTENT_URI, null,
                ContractParaListaUsers.Columnas.ROL + "=? or " +
                        ContractParaListaUsers.Columnas.ROL + "=?",
                new String[]{"1", "2"}, null, null);


        try {
            if (cursor!=null&& cursor.getCount() == 1) {

                Log.i("USUARIO", "" + cursor.getCount());
                cursor.moveToNext();
                Intent intent = new Intent(LoginActivity.this, MensajesFragment.class);
                startActivity(intent);
                finish();

            } else {

                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        validaciones();


                    }


                });


                //ojo
            }


        } catch (Exception exc) {
            Log.i("Login_Activity", "" + exc);
        }

    }


    public void validaciones() {

        txtCorreo.setError(null);
        txtContrasena.setError(null);

        if ((txtCorreo.getText().toString().trim().length() > 0) && (txtContrasena.getText().toString().trim().length() > 0)) {
            if (!isEmailValid(txtCorreo.getText())) {
                txtCorreo.setError(getString(R.string.correovalido));
            }else {
                    String email = txtCorreo.getText().toString().trim();
                    String contraseniaPass = txtContrasena.getText().toString().trim();
                    loginLaravel(email, contraseniaPass);
                }
            }

            if ((txtContrasena.getText().toString().trim().length() > 0) && (txtCorreo.getText().toString().trim().length() > 0)) {
                if (!isPasswordValid(txtContrasena.getText())) {
                    txtContrasena.setError(getString(R.string.caracteres));
                }
            }

            // Compruebe que la contraseña sea válida, si el usuario la introdujo.
            String password = txtContrasena.getText().toString();
            if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                txtContrasena.setError(getString(R.string.caracteres));
            }

          //Here



        else {
            if (txtCorreo.getText().toString().length() == 0 ||
                    txtCorreo.getText().toString().trim().equalsIgnoreCase("")) {
                txtCorreo.setError(getString(R.string.ingresarcorreo));

            }
            if (txtContrasena.getText().toString().trim().length() == 0 ||
                    txtContrasena.getText().toString().trim().equalsIgnoreCase("")) {
                txtContrasena.setError(getString(R.string.ingresarcontra));
            }

        }

    }


    /**private void logeoFirebase() {
        String email = txtCorreo.getText().toString();
        String password = txtContrasena.getText().toString();

        Log.i("Teste", email);
        Log.i("Teste", password);

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("Teste", task.getResult().getUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste", e.getMessage());

                    }
                });
    }*/


    public void guardarEstadoButton() {

        SharedPreferences preferencias = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferencias.edit().putBoolean(PREFERENCE_ESTADO_BUTTON, RBsesion.isChecked()).apply();
    }

    public boolean obtenerEstadoButton() {

        SharedPreferences preferencias = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferencias.getBoolean(PREFERENCE_ESTADO_BUTTON, false);
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);


        String email = preferences.getString("email", "");
        String contra = preferences.getString("contra", "");
        rol_id = preferences.getString("rol", "");
        firebase = preferences.getString("uid", "");

    }


    private void loginLaravel(final String txtCorreo, final String txtContrasena) {

        progressDialog = new ProgressDialog(this);
        //progressDialog.   setMessage("Iniciando sesión");
        progressDialog.   show();
        progressDialog.setContentView(R.layout.custom_progressdialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(true);

        String ip = getString(R.string.ip);

        String url = ip + "/api/auth/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        logeoFirebase(response);
                        //progressDialog. dismiss();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();


                        if (error.toString().equals("com.android.volley.ServerError")){
                            Toast.makeText(getApplicationContext(), getString(R.string.servererror),
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.timeerror), Toast.LENGTH_LONG).show();
                        }
                        if (error.toString().equals("com.android.volley.AuthFailureError")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.autherror), Toast.LENGTH_LONG).show();
                        }

                        if (error.toString().equals("com.android.volley.ClientError")) {
                          //  Log.i("Error","No se pudo consultar el registro: "+error.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.clienterror), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), error+ "", Toast.LENGTH_LONG).show();
                        }



                    }
                }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<>();
                parametros.put("email", txtCorreo);
                parametros.put("password", txtContrasena);
                return parametros;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }



        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        ClaseVolley.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void logeoFirebase(String response) {
        String email = txtCorreo.getText().toString();
        String passw = txtContrasena.getText().toString().trim();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtCorreo.setError(getString(R.string.correovalido));
            txtCorreo.setFocusable(true);
        }else {
            loginUser(email, passw, response);
        }
    }

    private void loginUser(String email, String passw, final String response) {
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            firebase = user.getUid();
                            System.out.println(user.getUid());
                            guardarEstadoButton();
                            //Toast.makeText(LoginActivity.this, "Si responde"+response.toString(), Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getString("access_token");
                                rol_id = jsonObject.getString("rol_id");
                                usuario_id = jsonObject.getString("id");

                                //Toast.makeText(LoginActivity.this, "Si manda los resultados"+rol_id , Toast.LENGTH_LONG).show();

                                //JSONArray jsonArray = jsonObject.getJSONArray("login");
                                if (success.equals("1")) {
                                    /**for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        String name = object.getString("name").trim();
                                        String email = object.getString("email").trim();
                                        Toast.makeText(LoginActivity.this, "Se ha logeado. " +
                                                " \nTu nombre : " + name + "" +
                                                "\nTu correo :" + email, Toast.LENGTH_SHORT).show();

                                    }*/
                                    cargando.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
//                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión"+e.toString(), Toast.LENGTH_SHORT).show();
                            }

                            savePreferences(success, usuario_id);
                            intem();
                            progressDialog. dismiss();
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.fallaauth),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    boolean isPasswordValid(CharSequence password) {
        /**Si la cadena supera los 7 caracteres es una contraseña valida*/
        return password.length() > 7;
    }


    public void intem() {

        if (rol_id.equals("1")) {

            Intent intent = new Intent(getApplicationContext(), NavegacionAdministradorActivity.class);
            //intent.putExtra("USER", user.getUid());
            startActivity(intent);
        } else {
            Intent i = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(i);
        }


    }


    private void savePreferences(String token, String usuario_id) {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        String correo = txtCorreo.getText().toString();
        String contra = txtContrasena.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", correo);
        editor.putString("token", token);
        editor.putString("password", contra);
        editor.putString("rol", rol_id);
        editor.putString("id", usuario_id);
        editor.putString("uid", user.getUid());

        String token_firebase = preferences.getString("token_firebase", "");
        guardarTokenFirebaseEnLaravel(usuario_id, token_firebase);
        editor.apply();

    }

    private void guardarTokenFirebaseEnLaravel(String usuario_id, String token_firebase) {

        String ip = getString(R.string.ip);
        String url = ip + "/api/token_firebase";


        JSONObject parametros = new JSONObject();

        try {
            parametros.put("id", usuario_id);
            parametros.put("firebase_token", token_firebase);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Toast.makeText(LoginActivity.this, "Se actualizo el token firebase", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("volley", "onErrorResponse: " + error.networkResponse);
            }
        }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Content-Type", "application/json");
                parametros.put("X-Requested-With", "XMLHttpRequest");

                return parametros;
            }
        };
        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);
    }

    public void crearcuenta(View view) {
        Intent intent = new Intent(this, RegistarUsuarioNuevo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void resetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), PasswordReset.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}