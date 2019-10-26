package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.MainActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RegistarUsuarioNuevo;
import com.example.fumigacionesmoncada.ui.clientes.Detalle_Cliente;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Aquirir_Servicio_Fragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private Aquirir_Servicio_ViewModel servicioViewModel;
    private EditText mostrarNombre,mostrarDireccion,mostraraTelefono;
    private EditText fecha, Hora;
    String tokenUsuario;
    private Button pedir;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicioViewModel =
                ViewModelProviders.of(this).get(Aquirir_Servicio_ViewModel.class);
        View view = inflater.inflate(R.layout.fragment_ad_servicio, container, false);
        cargarPreferencias();


        mostrarNombre = (EditText)view.findViewById(R.id.nombres);
        mostrarDireccion = (EditText)view.findViewById(R.id.direccion);
        mostraraTelefono = (EditText)view.findViewById(R.id.telefono);
        Hora = (EditText)view.findViewById(R.id.hora);
        fecha = (EditText)view.findViewById(R.id.fecha);
        pedir= view.findViewById(R.id.pedir);
        request = Volley.newRequestQueue(getActivity());
        cargarClienteWeb();


        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SubirWeb();
            }
        });
        return view;

    }

    private void SubirWeb() {
        try {
            if(mostrarNombre.getText().toString().equals("")||mostraraTelefono.getText().toString().equals("")||mostraraTelefono.getText().toString().equals("")|| fecha.getText().toString().equals("") || Hora.getText().toString().equals("")){
                Toast.makeText(getContext(),"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
            }else {
                    if (mostraraTelefono.getText().toString().length() < 8 ) {
                        Toast.makeText(getContext(), "No es un numero Telefonico", Toast.LENGTH_LONG).show();
                    } else {

                                progreso = new ProgressDialog(getContext());
                                progreso.setMessage("Cargando...");
                                progreso.show();
                                String ip = getString(R.string.ip);
                                String url = ip + "/api/peticioncita?Nombre=" + mostrarNombre.getText().toString()
                                        + "&Direccion=" + mostrarDireccion.getText().toString() + "&Telefono=" + mostraraTelefono.getText().toString()
                                + "&FechaFumigacion="+ fecha.getText().toString() + "&Hora=" + Hora.getText().toString();
                                url = url.replace(" ", "%20");
                                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this);
                                request.add(jsonObjectRequest);
                            }
                        }



        }catch (Exception exe){
            Toast.makeText(getContext(),exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onErrorResponse (VolleyError error){
        progreso.hide();
        if (error.toString().equals("com.android.volley.ServerError")) {
            Toast.makeText(getContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

        } else if (error.toString().equals("com.android.volley.TimeoutError")) {
            Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), " "+error.toString(), Toast.LENGTH_SHORT).show();
        }
        //  Toast.makeText(getApplicationContext(),"No se pudo registrar , Hubo un error al conectar por favor verifica la conexión a internet o intente nuevamente o su correo ya esta registrado , Error : "+ error.toString(), Toast.LENGTH_LONG).show();

        // Log.i("ERROR", error.toString());
    }





    @Override
    public void onResponse (JSONObject response){

        Toast.makeText(getContext(),"Peticion Realizada correctamente", Toast.LENGTH_SHORT).show();
        progreso.hide();
    }


    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
         tokenUsuario = preferences.getString("token", "");

    }
    private void cargarClienteWeb() {

        String ip = "http://192.168.0.111/api/auth/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("cliente");
                            mostrarNombre.setText(object.getString("name"));
                            mostraraTelefono.setText(object.getString("telefono"));
                            mostrarDireccion.setText(object.getString("recidencia"));



                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "sinoda.", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else if (error.toString().equals("com.android.volley.AuthFailureError")) {
                    Toast.makeText(getContext(), "Correo o password invalido.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), " "+error.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                return parametros;
            }
        };

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


    }

}