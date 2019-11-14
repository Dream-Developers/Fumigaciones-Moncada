package serviciosAdministrador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class DetalleServicioAdministradorActivity extends AppCompatActivity {
    private EditText titulo, descripcion;
    NetworkImageView imagen;
    private Button guardarCambios;
    String id;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    ProgressDialog progreso;
    private String tokenUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio_administrador);
        titulo = findViewById(R.id.tituloImagenDA);
        descripcion= findViewById(R.id.descripcionImagenDA);
        imagen= findViewById(R.id.idImagenDA);
        guardarCambios=findViewById(R.id.guardarcambios);
        id = getIntent().getStringExtra("id");
        cargarPreferencias();
        cargarImagenWeb(id);

        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar_datos(id);
            }
        });
    }
    public void actualizar_datos(final String id){

        try {
            if(titulo.getText().toString().equals("")
                    || descripcion.getText().toString().equals("")){
                Toast.makeText(this,"Todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();


            } else  {
                {

                }  {
                    progreso = new ProgressDialog(this);
                    progreso.setMessage("Cargando datos...");
                    progreso.show();

                    String ip = getString(R.string.ip);
                    String url  = ip+ "/api/servicios/"+id+"/update" ;


                    JSONObject parametros = new JSONObject();
                    parametros.put("nombre", titulo.getText().toString());
                    parametros.put("descripcion",descripcion.getText().toString());




                    jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progreso.dismiss();
                            try {
                                Toast.makeText(getApplicationContext(), ""+response.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            cargarImagenWeb(id);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), ""+error.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("volley", "onErrorResponse: "+error.networkResponse);
                        }
                    }){

                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> parametros = new HashMap<>();
                            parametros.put("Content-Type","application/json");
                            parametros.put("X-Requested-With","XMLHttpRequest");

                            return parametros;
                        }
                    };
                    ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);
                }
            }
        } catch (Exception exe){
            Toast.makeText(getApplicationContext(),exe.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
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

                Toast.makeText(DetalleServicioAdministradorActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }};

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }




    private void cargarImagen(String foto) {
        String ip = getResources().getString(R.string.ip);
        String url = ip+"/imagen/"+foto;
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(this).getImageLoader();

        imagen.setImageUrl(url,imageLoader);

    }
}
