package com.example.fumigacionesmoncada.ui.clientes;

import android.app.Activity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Detalle_Cliente extends Activity {
    private TextView nombre, residencia, telefono,correo;
    private ClientesVO clientesVO;
    String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_cliente);
        nombre= findViewById(R.id.nombre_detalle);
        residencia= findViewById(R.id.residencia_detalle);
        telefono= findViewById(R.id.telefono_detalle);
        correo= findViewById(R.id.correo_detalle);

        id = getIntent().getStringExtra("id");

        cargarClienteWeb(id);

    }

    private void cargarClienteWeb(final String id) {

        String ip = "http://192.168.43.134/api/cliente/"+id+"/mostrar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("cliente");
                                nombre.setText(object.getString("name")+object.getString("lasname"));
                                telefono.setText(object.getString("telefono"));
                                correo.setText(object.getString("email"));
                                residencia.setText(object.getString("recidencia"));


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
}
