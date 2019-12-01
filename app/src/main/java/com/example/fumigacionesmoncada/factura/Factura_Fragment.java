package com.example.fumigacionesmoncada.factura;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Factura_Fragment extends Fragment  implements SearchView.OnQueryTextListener{
    private FloatingActionButton addCliente;
    ListView lista_citas;
    facturas_adapter citasAdapter;
    ArrayList<Facturas> cita;
    private String tokenUsuario;
    private String Usuario_id;
    ProgressDialog progreso;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_factura, container, false);
        addCliente = view.findViewById(R.id.add_clientes);
        lista_citas = view.findViewById(R.id.lista);
        cita = new ArrayList<>();
        lista_citas.setAdapter(new facturas_adapter(getContext(), cita));
        lista_citas.setVisibility(View.VISIBLE);
        addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),crearFactura.class);
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        cargarPreferencias();
        cargarCitas();
        return view;
    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        Usuario_id = preferences.getString("id", "");

    }
    private void cargarCitas() {
        String ip=getString(R.string.ip);
        String url = ip+"/api/recuperar/factura";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                cita = new ArrayList<>();
                Facturas citas = null;
                try {
                    JSONObject object  = response;
                    JSONArray array = response.getJSONArray("citas");
                    for (int i = 0; i < array.length(); i++) {
                        citas = new Facturas();
                        object = array.getJSONObject(i);
                        citas.setNumero(object.getString("id"));
                        citas.setNombre(object.getString("Nombre"));
                        citas.setFecha(object.getString("Fecha"));
                        citas.setDetalle(object.getString("Detalle"));
                        citas.setTotal(object.getString("Total"));

                        cita.add(citas);
                        citasAdapter = new facturas_adapter(getContext(), cita);
                        lista_citas.setAdapter(citasAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), " " + error.toString(), Toast.LENGTH_SHORT).show();
                }

                error.getStackTrace();
                Toast.makeText(getContext(), "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menuprueba, menu);
        MenuItem item = menu.findItem(R.id.buscar);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
    }
@Override
public boolean onQueryTextSubmit(String query) {
        return false;
        }

@Override
public boolean onQueryTextChange(String s) {
        if (!(citasAdapter == null)) {
        ArrayList<Facturas> listaClientes = null;
        try {
        listaClientes = filtrarDatosDeptos(cita, s.trim());
        citasAdapter.filtrar(listaClientes);
        citasAdapter.notifyDataSetChanged();

        } catch (Exception e) {
        Toast.makeText(getContext(), "" + listaClientes, Toast.LENGTH_SHORT).show();

        }
        return true;


        }
        return false;
        }
private ArrayList<Facturas> filtrarDatosDeptos(ArrayList<Facturas> listaTarea, String dato) {
        ArrayList<Facturas> listaFiltradaPermiso = new ArrayList<>();
        try{
        dato = dato.toLowerCase();
        for(Facturas permisos: listaTarea){
        String nombre = permisos.getNombre().toLowerCase().trim();
            String detalle = permisos.getDetalle().toLowerCase().trim();



        if(nombre.toLowerCase().contains(dato)) {
        listaFiltradaPermiso.add(permisos);
        }else if(detalle.toLowerCase().contains(dato)){
                listaFiltradaPermiso.add(permisos);
            }
        }
        citasAdapter.filtrar(listaFiltradaPermiso);
        }catch (Exception e){
        Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
        e.getStackTrace();
        }

        return listaFiltradaPermiso;

        }





        }
