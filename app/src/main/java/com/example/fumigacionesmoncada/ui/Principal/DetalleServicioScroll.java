package com.example.fumigacionesmoncada.ui.Principal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.fumigacionesmoncada.AdquirirServicioActivity;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fumigacionesmoncada.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleServicioScroll extends AppCompatActivity {

    private TextView titulo, descripcion;
    NetworkImageView imagen;
    String id;
    String tokenUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(R.string.detalle_servicio_ti);

        titulo = findViewById(R.id.tituloImagenD);
        descripcion= findViewById(R.id.descripcionImagenD);
        imagen= findViewById(R.id.idImagenD);
        id = getIntent().getStringExtra("id");
        cargarImagenWeb(id);
        cargarPreferencias();
    }

    private void cargarPreferencias() {
        SharedPreferences preferences =this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");


    }


    private void cargarImagenWeb(final String id) {
        String ip=getString(R.string.ip);

        String url = ip+"/api/imagen/"+id+"/mostrar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("servicio");
                            titulo.setText(object.getString("nombre"));
                            descripcion.setText(object.getString("descripcion"));
                            cargarImagen(object.getString("foto"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(DetalleServicioScroll.this, R.string.presentamosProblemas, Toast.LENGTH_LONG).show();
                    Toast.makeText(DetalleServicioScroll.this, R.string.reviseConexion, Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {

                    Toast.makeText(DetalleServicioScroll.this, R.string.reviseConexion, Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(DetalleServicioScroll.this,  R.string.reviseConexion , Toast.LENGTH_SHORT).show();
                }

            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parametros = new HashMap<>();
                parametros.put("id",id);
                return parametros;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }


    private void cargarImagen(String foto) {
        String ip = getResources().getString(R.string.ip);
        String url = ip+"/imagen/"+foto;
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(this).getImageLoader();

        imagen.setImageUrl(url,imageLoader);

    }


    public void adquiiirServicio(View view) {
        Intent i = new Intent(getApplication(), AdquirirServicioActivity.class);
        startActivity(i);
    }
}
