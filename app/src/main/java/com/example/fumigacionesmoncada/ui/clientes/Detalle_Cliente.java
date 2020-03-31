package com.example.fumigacionesmoncada.ui.clientes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Detalle_Cliente extends AppCompatActivity {
    private TextView nombre, residencia, telefono,correo;
    private ClientesVO clientesVO;
    NetworkImageView imagen;
    String id;
    String tokenUsuario;
    String id_usuario;
    TextView fab, phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_cliente);
        setTitle("Perfil");
        getSupportActionBar();
        nombre= findViewById(R.id.nombre_detalle);
        residencia= findViewById(R.id.residencia_detalle);
        telefono= findViewById(R.id.telefono_detalle);
        correo= findViewById(R.id.correo_detalle);

        imagen = findViewById(R.id.imagen_cliente);
        id = getIntent().getStringExtra("id_cliente");
        fab=  findViewById(R.id.correo);
        phone =  findViewById(R.id.phone);
cargarPreferencias();
        cargarClienteWeb(id);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviarCorreo("");
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent llamar = new Intent(Intent.ACTION_CALL);
                llamar.setData(Uri.parse(getString(R.string.tel)+telefono.getText()));
                int permisoCheck = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
                if(permisoCheck == PackageManager.PERMISSION_GRANTED){

                    if(llamar.resolveActivity(getPackageManager())!=null){
                        getApplication().startActivity(llamar);

                    }else{
                        Toast.makeText(getApplicationContext(), R.string.app,Toast.LENGTH_LONG).show();
                    }

                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                102);
                    }
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_cliente, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idm = item.getItemId();

        if (idm == R.id.llamar) {
            hacerllamada();
            return true;
        }

        if (idm == R.id.correo) {
            enviarCorreo("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void cargarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        id_usuario = preferences.getString("id", "");

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

                Toast.makeText(Detalle_Cliente.this, R.string.error, Toast.LENGTH_SHORT).show();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parametros = new HashMap<>();
                parametros.put("id",id);

                return parametros;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> parametros= new HashMap<>();
                parametros.put("Content-Type","application/json");
                parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                return  parametros;
            }
        };

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }

    public void hacerllamada(){
        Intent llamar = new Intent(Intent.ACTION_CALL);
        llamar.setData(Uri.parse(getString(R.string.tel)+telefono.getText()));
        int permisoCheck = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if(permisoCheck == PackageManager.PERMISSION_GRANTED){

            if(llamar.resolveActivity(getPackageManager())!=null){
                getApplication().startActivity(llamar);

            }else{
                Toast.makeText(getApplicationContext(), R.string.app,Toast.LENGTH_LONG).show();
            }

        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        102);
            }
        }


    }
    public void enviarCorreo(String mensaje) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.msjCorre);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(correo.getText())});
        startActivity(intent);
    }

    private void cargarImagen(String foto) {
        String ip = getResources().getString(R.string.ip);
        String url = ip+"/foto/"+foto;
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(this).getImageLoader();

        imagen.setImageUrl(url,imageLoader);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 102:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent llamar1 = new Intent(Intent.ACTION_CALL);
                    llamar1.setData(Uri.parse(getString(R.string.tel)+telefono.getText()));
                    startActivity(llamar1);

                } else{
                    Toast.makeText(this, R.string.permiso, Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }
}
