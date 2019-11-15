package com.example.fumigacionesmoncada.ui.clientes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RegistarUsuarioNuevo;
import com.example.fumigacionesmoncada.ui.citas.Citas;
import com.example.fumigacionesmoncada.ui.citas.Citas_Adapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


                lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ClientesVO cliente = (ClientesVO) parent.getItemAtPosition(position);
                        eliminarClientes(cliente,position);

                        return true;
                    }
                });


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

    private void eliminarClientes(final ClientesVO clientesVO, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmacion");
        builder.setMessage("Esta seguro que desea eliminar de tu lista");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarClienteWebService(clientesVO.getId(),position);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                    cargarClientes();

                }
            });
        }
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void eliminarClienteWebService(String id, final int position) {
        String ip=getString(R.string.ip);
        String url = ip+"/api/registro/"+id+"/delete";

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(getContext(), ""+object.getString("message"), Toast.LENGTH_LONG).show();
                    cliente = new ArrayList<>();
                    clientesAdapter = new ClientesAdapter(getContext(),cliente);
                    lista.setAdapter(clientesAdapter);
                    cargarClientes();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error al borrar", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> parametros= new HashMap<>();
                parametros.put("Content-Type","application/json");
                return  parametros;
            }
        };
        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);

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
        String ip=getString(R.string.ip);

        String url = ip+"/api/clientes";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                 cliente = new ArrayList<>();
                ClientesVO clientesVO = null;
                try {
                    JSONArray array = response.getJSONArray("clientes");
                    JSONObject object;
                    for(int i=0; i<array.length();i++){
                        clientesVO = new ClientesVO();
                        object = array.getJSONObject(i);
                        clientesVO.setNombre(object.getString("name"));
                        clientesVO.setTelefono(object.getString("telefono"));
                        clientesVO.setId(String.valueOf(object.getInt("id")));
                        clientesVO.setImagen(object.getString("foto"));

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
                String telefono = permisos.getTelefono().toLowerCase().trim();


                if(nombre.toLowerCase().contains(dato)){
                    listaFiltradaPermiso.add(permisos);
                }else if(telefono.toLowerCase().contains(dato)){
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