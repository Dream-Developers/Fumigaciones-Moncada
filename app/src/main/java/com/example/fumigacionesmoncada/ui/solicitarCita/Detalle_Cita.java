package com.example.fumigacionesmoncada.ui.solicitarCita;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.AdquirirServicio.Aquirir_Servicio_Fragment;
import com.example.fumigacionesmoncada.ui.citas.Citas;
import com.example.fumigacionesmoncada.ui.citas.CitasFragment;
import com.example.fumigacionesmoncada.ui.citas.Crear_Citas;
//import com.example.fumigacionesmoncada.ui.citas.CitaVO;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Detalle_Cita  extends Activity {
    private TextView nombre, direccion, telefono,fecha;
    private CitaVO citaVO;
    private Button btnAceptar, btnRechazar;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentVIew(R.layout.detalle_cita);
        nombre= findViewById(R.id.detalle_nombre);
        direccion= findViewById(R.id.detalle_direccion);
        telefono= findViewById(R.id.detalle_telefono);
        fecha= findViewById(R.id.detalle_fecha);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnRechazar = findViewById(R.id.btnRechazar);

        id = getIntent().getStringExtra("id_cita");

        cargarCitaWeb(id);


                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =  new Intent(Detalle_Cita.this, Crear_Citas.class);
                        startActivity(intent);
                    }
                });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Detalle_Cita.this, SolicitarCitaFragment.class);
                startActivity(intent);
            }
        });


    }


    private void setContentVIew(int detalle_cita) {
    }


    private void cargarCitaWeb(final String id) {

        String ip = getString(R.string.ip);
        String url = ip + "/api/peticionCita"+id+"/mostrarCita";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("cita");
                            nombre.setText(object.getString("name"));
                            direccion.setText(object.getString("direccion"));
                            telefono.setText(object.getString("telefono"));
                            fecha.setText(object.getString("fecha"));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    }, new Response.ErrorListener() {
           @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Detalle_Cita.this, "Error", Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parametros = new HashMap<>();
                parametros.put("id",id);
                return parametros;
            }
        };

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }
}



