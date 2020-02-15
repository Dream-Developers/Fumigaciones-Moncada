package com.example.fumigacionesmoncada.ui.citas;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleCitaScrollingActivity extends AppCompatActivity {
    private TextView nombre, direccion,precio,fecha,hora;
    private Citas citas;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cita_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        nombre = findViewById(R.id.detalle_nombre);
        direccion = findViewById(R.id.detalle_direccion);
        precio = findViewById(R.id.detalle_precio);
        fecha = findViewById(R.id.detalle_fecha);
        hora = findViewById(R.id.detalle_hora);

        id = getIntent().getIntExtra("id_citas",0);
        cargarCitasWeb(String.valueOf(id));

    }
    private void cargarCitasWeb(final String id) {
        String ip=getString(R.string.ip);

        String url = ip+"/api/citas/"+id+"/mostrar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("citas");
                            nombre.setText(object.getString("Nombre"));
                            direccion.setText(object.getString("Direccion"));
                            precio.setText(object.getString("Precio"));
                            fecha.setText(object.getString("FechaFumigacion"));
                            hora.setText(object.getString("Hora"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DetalleCitaScrollingActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
