package com.example.fumigacionesmoncada.ui.clientes;

import android.app.Activity;
import android.os.Bundle;
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

import androidx.annotation.Nullable;

public class Detalle_Cliente extends Activity {
    private TextView nombre, residencia, telefono,correo;
    private ClientesVO clientesVO;
    NetworkImageView imagen;
    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_cliente);
        nombre= findViewById(R.id.nombre_detalle);
        residencia= findViewById(R.id.residencia_detalle);
        telefono= findViewById(R.id.telefono_detalle);
        correo= findViewById(R.id.correo_detalle);

        imagen = findViewById(R.id.imagen_cliente);
        id = getIntent().getStringExtra("id_cliente");

        cargarClienteWeb(id);

    }

    private void cargarClienteWeb(final String id) {
        String ip=getString(R.string.ip);

        String url = ip+"/api/cliente/"+id+"/mostrar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("cliente");
                                nombre.setText(object.getString("name"));
                            residencia.setText(object.getString("recidencia"));
                                telefono.setText(object.getString("telefono"));
                                correo.setText(object.getString("email"));
                                correo.setText(object.getString("email"));
                                cargarImagen(object.getString("foto"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Detalle_Cliente.this, "Error", Toast.LENGTH_SHORT).show();
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
        String url = ip+"/foto/"+foto;
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(this).getImageLoader();

        imagen.setImageUrl(url,imageLoader);

    }
}
