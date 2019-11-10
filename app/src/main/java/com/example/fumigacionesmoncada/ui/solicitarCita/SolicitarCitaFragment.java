package com.example.fumigacionesmoncada.ui.solicitarCita;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RegistarUsuarioNuevo;
import com.example.fumigacionesmoncada.ui.AdquirirServicio.Aquirir_Servicio_Fragment;
import com.example.fumigacionesmoncada.ui.citas.Citas;
import com.example.fumigacionesmoncada.ui.citas.Citas_Adapter;
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;
import com.example.fumigacionesmoncada.ui.clientes.Detalle_Cliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SolicitarCitaFragment extends Fragment {
        ListView lista;
    CitasAdapter citasAdapter ;
    ArrayList<CitaVO> cita;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitar_cita, container, false);
        lista = view.findViewById(R.id.lista_citas_solicitud);
        cita = new ArrayList<>();
        lista.setAdapter(new CitasAdapter(getContext(),cita));
        lista.setVisibility(View.VISIBLE);



        cargarCitas();
        setHasOptionsMenu(true);



         return view;
    }


    private void cargarCitas() {

        String ip = getString(R.string.ip);
        String url = ip + "/api/peticionesCitas";

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

                error.getStackTrace();
                Toast.makeText(getContext(), "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


    }








}