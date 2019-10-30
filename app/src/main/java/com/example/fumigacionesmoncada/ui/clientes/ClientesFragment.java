package com.example.fumigacionesmoncada.ui.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RegistarUsuarioNuevo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClientesFragment extends Fragment implements SearchView.OnQueryTextListener{
    private FloatingActionButton addCliente;
    ListView lista;
    ClientesAdapter clientesAdapter ;
    ArrayList<ClientesVO> cliente;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
        lista = view.findViewById(R.id.lista_clientes);
        addCliente = view.findViewById(R.id.add_clientes);

        cargarClientes();
        setHasOptionsMenu(true);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientesVO clientesVO = (ClientesVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Detalle_Cliente.class);
                intent.putExtra("id_cliente",clientesVO.getId());
                startActivity(intent);


            }
        });
        addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegistarUsuarioNuevo.class);
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

    private void cargarClientes() {

        String ip = "http://192.168.137.1/api/clientes";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                 cliente = new ArrayList<>();
                ClientesVO clientesVO = null;
                try {
                    JSONArray array = response.getJSONArray("clientes");
                    JSONObject object = null;
                    for(int i=0; i<array.length();i++){
                        clientesVO = new ClientesVO();
                        object = array.getJSONObject(i);
                        clientesVO.setNombre(object.getString("name"));
                        clientesVO.setTelefono(object.getString("telefono"));
                        clientesVO.setId(String.valueOf(object.getInt("id")));

                        cliente.add(clientesVO);
                        clientesAdapter = new ClientesAdapter(getContext(), cliente);
                        lista.setAdapter(clientesAdapter);
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
        if (!(clientesAdapter == null)) {
            ArrayList<ClientesVO> listaClientes = null;
            try {
                listaClientes = filtrarDatosDeptos(cliente, s.trim());
                clientesAdapter.filtrar(listaClientes);
                clientesAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(getContext(), "" + listaClientes, Toast.LENGTH_SHORT).show();

            }
            return true;


        }
        return false;
    }
    private ArrayList<ClientesVO> filtrarDatosDeptos(ArrayList<ClientesVO> listaTarea, String dato) {
        ArrayList<ClientesVO> listaFiltradaPermiso = new ArrayList<>();
        try{
            dato = dato.toLowerCase();
            for(ClientesVO permisos: listaTarea){
                String nombre = permisos.getNombre().toLowerCase().trim();


                if(nombre.toLowerCase().contains(dato)){
                    listaFiltradaPermiso.add(permisos);
                }
            }
            clientesAdapter.filtrar(listaFiltradaPermiso);
        }catch (Exception e){
            Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }

        return listaFiltradaPermiso;

    }
}