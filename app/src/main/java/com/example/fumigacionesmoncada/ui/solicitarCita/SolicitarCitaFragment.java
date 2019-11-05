package com.example.fumigacionesmoncada.ui.solicitarCita;

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
import com.example.fumigacionesmoncada.ui.citas.Citas_Adapter;
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;
import com.example.fumigacionesmoncada.ui.clientes.Detalle_Cliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SolicitarCitaFragment extends Fragment implements SearchView.OnQueryTextListener{
    private FloatingActionButton addCita;
    ListView lista;
    CitasAdapter citasAdapter ;
    ArrayList<CitaVO> cita;
    private Object CitasAdapter;
    private Object CitaVO;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitar_cita, container, false);
        lista = view.findViewById(R.id.lista_citas);
        addCita = view.findViewById(R.id.add_citas);

        cargarCitas();
        setHasOptionsMenu(true);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CitaVO citaVO = (com.example.fumigacionesmoncada.ui.solicitarCita.CitaVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Detalle_Cliente.class);
                intent.putExtra("id_cita",citaVO.getId());
                startActivity(intent);


            }
        });
        addCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Aquirir_Servicio_Fragment.class);
                startActivity(intent);
            }
        });




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

        String ip = "http://192.168.0.101/api/citas";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                cita = new ArrayList<>();
                CitaVO citaVO = null;
                try {
                    JSONArray array = response.getJSONArray("cita");
                    JSONObject object;
                    for(int i=0; i<array.length();i++){
                        CitaVO = new CitaVO() {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public int getCount() {
                                return 0;
                            }

                            @Override
                            public long getItemId(int position) {
                                return 0;
                            }

                            @Nullable
                            @Override
                            public com.example.fumigacionesmoncada.ui.solicitarCita.CitaVO getItem(int position) {
                                return null;
                            }
                        };
                        object = array.getJSONObject(i);
                        citaVO.setNombre(object.getString("name"));
                        citaVO.setTelefono(object.getString("telefono"));
                        citaVO.setId(String.valueOf(object.getInt("id")));

                        cita.add(citaVO);
                        CitasAdapter = new CitasAdapter(getContext(), cita);
                        lista.setAdapter((ListAdapter) citasAdapter);
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


        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!(citasAdapter == null)) {
            ArrayList<CitaVO> listaCita = null;
            try {
                listaCita = filtrarDatosDeptos(cita, s.trim());
                citasAdapter.filtrar(listaCita);
                // citasAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(getContext(), "" + listaCita, Toast.LENGTH_SHORT).show();

            }
            return true;


        }
        return false;
    }
    private ArrayList<CitaVO> filtrarDatosDeptos(ArrayList<CitaVO> listaTarea, String dato) {
        ArrayList<CitaVO> listaFiltradaPermiso = new ArrayList<>();
        try{
            dato = dato.toLowerCase();
            for(CitaVO permisos: listaTarea){
                String nombre = permisos.getNombre().toLowerCase().trim();
                String telefono = permisos.getTelefono().toLowerCase().trim();


                if(nombre.toLowerCase().contains(dato)){
                    listaFiltradaPermiso.add(permisos);
                }
                if(telefono.toLowerCase().contains(dato)){
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