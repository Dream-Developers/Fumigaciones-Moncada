package com.example.fumigacionesmoncada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class PasswordReset extends AppCompatActivity {


    EditText correo;
    Button enviar;
    FirebaseAuth firebaseAuth;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        correo = (EditText) findViewById(R.id.txtCorreResetPass);
        enviar = (Button) findViewById(R.id.btnEnviarCorreo);

        firebaseAuth = FirebaseAuth.getInstance();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      Toast.makeText(getApplicationContext(), "Funcion sin programar", Toast.LENGTH_LONG).show();


                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    if (correo.getText().toString().equalsIgnoreCase("")) {

                        if (correo.getText().toString().length() == 0 ||
                                correo.getText().toString().trim().equalsIgnoreCase("")) {
                            correo.setError(getString(R.string.ingresarcorreo));

                        }

                    } else {
                        if (!isEmailValid(correo.getText())) {
                            correo.setError(getString(R.string.correovalido));
                        }else {
                            enviarCorreo();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.accesointernet), Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void enviarCorreo() {



        loading = new ProgressDialog(this);
        loading.   show();
        loading.setContentView(R.layout.custom_progressdialog_reset);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loading.setCancelable(true);


        String ip = getString(R.string.ip);
        String url = ip + "/api/password/create";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        reseteoFirebase();
                        Toast.makeText(PasswordReset.this, getString(R.string.checkemail), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.hide();
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.timeerror), Toast.LENGTH_LONG).show();
                } if(error.toString().equals("com.android.volley.ServerError")){
                    Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.emailregister), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", correo.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void reseteoFirebase(){

        String email = correo .getText().toString().trim();

        if (email.equals("")){
            Toast.makeText(PasswordReset.this, getString(R.string.obligatorio), Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(PasswordReset.this, getString(R.string.changepass), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PasswordReset.this, LoginActivity.class));
                        overridePendingTransition(R.anim.botton_in, R.anim.top_out);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(PasswordReset.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

}
