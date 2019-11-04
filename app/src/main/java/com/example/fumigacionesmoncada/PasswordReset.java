package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

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
                //      Toast.makeText(getApplicationContext(), "Funcion sin programar", Toast.LENGTH_LONG).show();


                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    if (correo.getText().toString().equalsIgnoreCase("")) {

                    } else {
                        enviarCorreo();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Sin acceso a internet", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    private void enviarCorreo() {

        final ProgressDialog loading = ProgressDialog.show(
                this,
                "Por favor espere...",
                "Enviando petición...",
                false,
                false);

        //String REGISTER_URL = Utils.URL + "api/password/create";

        String ip = getString(R.string.ip);
        String url = ip + "/api/password/create";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "Revisa tu correo electrónico y obten una nueva contraseña", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getApplicationContext(), "Revisa tu conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "correo invalido", Toast.LENGTH_LONG).show();
                }
            }
        }) {

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
}
