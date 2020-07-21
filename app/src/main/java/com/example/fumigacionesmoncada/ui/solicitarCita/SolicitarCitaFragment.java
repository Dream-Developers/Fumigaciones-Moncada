package com.example.fumigacionesmoncada.ui.solicitarCita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SolicitarCitaFragment extends Fragment {
        ListView lista;
    CitasAdapter citasAdapter ;
    ArrayList<CitaVO> cita;
    String tokenUsuario;
    String id_usuario;
    ConstraintLayout constraintLayout;
    TextView sin_conexion;



    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitar_cita, container, false);
        lista = view.findViewById(R.id.lista_citas_solicitud);
        constraintLayout = view.findViewById(R.id.error);
        sin_conexion = view.findViewById(R.id.sin_conexion);
        cita = new ArrayList<>();
        lista.setAdapter(new CitasAdapter(getContext(),cita));
        lista.setVisibility(View.VISIBLE);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CitaVO citaVO = (CitaVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Detalle_Cita.class);
                intent.putExtra("id",citaVO.getId());
                startActivity(intent);

                lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        CitaVO citaVO1 = (CitaVO) parent.getItemAtPosition(position);


                        return true;
                    }
                });


            }
        });
    cargarPreferencias();

        cargarCitas();
        setHasOptionsMenu(true);



         return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cita= new ArrayList<>();
        citasAdapter= new CitasAdapter(getContext(), cita);
        cargarCitas();
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        id_usuario = preferences.getString("id", "");
    }

    private void cargarCitas() {

        String ip = getString(R.string.ip);
        String url = ip + "/api/peticion/recuperar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                cita = new ArrayList<>();
                CitaVO citas = null;
                try {
                    JSONArray array = response.getJSONArray("citas");
                    JSONObject object;
                    for (int i = 0; i < array.length(); i++) {
                        citas = new CitaVO();
                        object = array.getJSONObject(i);
                        citas.setId(object.getString("id"));
                        citas.setNombre(object.getString("Nombre"));
                        citas.setDireccion(object.getString("Direccion"));
                        citas.setFecha(object.getString("FechaFumigacion"));
                        citas.setHora(object.getString("Hora"));


                       cita.add(citas);

                        citasAdapter = new CitasAdapter(getContext(),cita);
                        lista.setAdapter(citasAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                constraintLayout.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp);
                sin_conexion.setVisibility(View.VISIBLE);
                error.getStackTrace();
                Toast.makeText(getContext(), "Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Content-Type", "application/json");
                parametros.put("X-Requested-With", "XMLHttpRequest");
                parametros.put("Authorization", "Bearer" + " " + tokenUsuario);

                return parametros;
            }
        };


        //ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


    }


}