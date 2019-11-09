package com.example.fumigacionesmoncada.ui.citas;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SearchView;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class CitasFragment extends Fragment implements SearchView.OnQueryTextListener {
private FloatingActionButton addcita;
    ListView lista_citas;
    Citas_Adapter citasAdapter;
    ArrayList<Citas> cita;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_citas, container, false);
        addcita = view.findViewById(R.id.add_citas);
        lista_citas= view.findViewById(R.id.lista_citas);




        addcita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Crear_Citas.class);
                startActivity(intent);
            }
        });

        cargarCitas();
        setHasOptionsMenu(true);
        return view;


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menuprueba, menu);
        MenuItem item = menu.findItem(R.id.buscar);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    private void cargarCitas() {
        String ip=getString(R.string.ip);
        String url = ip+"/api/citas";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                    cita = new ArrayList<>();
                    Citas citas = null;
                    try {
                        JSONArray array = response.getJSONArray("citas");
                        JSONObject object;
                        for (int i = 0; i < array.length(); i++) {
                            citas = new Citas();
                            object = array.getJSONObject(i);
                            citas.setNombre(object.getString("Nombre"));
                            citas.setDireccion(object.getString("Direccion"));
                            citas.setPrecio(object.getString("Precio"));
                            citas.setFecha(object.getString("FechaFumigacion"));
                            citas.setHora(object.getString("Hora"));


                            cita.add(citas);
                            citasAdapter = new Citas_Adapter(getContext(), cita);
                            lista_citas.setAdapter(citasAdapter);
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
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!(citasAdapter == null)) {
            ArrayList<Citas> listacitas = null;
            try {
                listacitas = filtrarDatosDeptos(cita, s.trim());
                citasAdapter.filtrar(listacitas);
                citasAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(getContext(), "" + listacitas, Toast.LENGTH_SHORT).show();

            }
            return true;


        }
        return false;
    }
    private ArrayList<Citas> filtrarDatosDeptos(ArrayList<Citas> listaTarea, String dato) {
        ArrayList<Citas> listaFiltradaPermiso = new ArrayList<>();
        try{
            dato = dato.toLowerCase();
            for(Citas permisos: listaTarea){
                String nombre = permisos.getNombre().toLowerCase().trim();
                String precio = permisos.getPrecio().toLowerCase().trim();

                if(nombre.toLowerCase().contains(dato)){
                    listaFiltradaPermiso.add(permisos);
                }else
                if (precio.toLowerCase().contains(dato)){
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



