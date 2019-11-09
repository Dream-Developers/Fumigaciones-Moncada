package com.example.fumigacionesmoncada.ui.Principal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleImagenActivity extends AppCompatActivity {

    private TextView titulo, descripcion;
    NetworkImageView imagen;
    ClaseAdapterImagen claseAdapterImagen ;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_imagen);
         titulo = findViewById(R.id.tituloImagenD);
         descripcion= findViewById(R.id.descripcionImagenD);
         imagen= findViewById(R.id.idImagenD);
         id = getIntent().getStringExtra("id");
        cargarImagenWeb(id);




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

                Toast.makeText(DetalleImagenActivity.this, "Error", Toast.LENGTH_SHORT).show();
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


    private void cargarImagen(String foto) {
        String ip = getResources().getString(R.string.ip);
        String url = ip+"/imagen/"+foto;
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(this).getImageLoader();

        imagen.setImageUrl(url,imageLoader);

    }


}
