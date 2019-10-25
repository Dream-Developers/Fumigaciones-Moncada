package com.example.fumigacionesmoncada.ui.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ClientesFragment extends Fragment {
    private FloatingActionButton addClientes;
    ListView lista;
    ClientesAdapter clientesAdapter ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
        lista = view.findViewById(R.id.lista_clientes);
        addClientes = view.findViewById(R.id.add_clientes);
        lista.setDivider(null);


        addClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegistarUsuarioNuevo.class);
                startActivity(intent);
            }
        });



        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientesVO clientesVO = (ClientesVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), Detalle_Cliente.class);
                intent.putExtra("id",clientesVO.getId());

                startActivity(intent);
            }
        });
        cargarClientes();

        return view;
    }

    private void cargarClientes() {

        String ip = "http://192.168.1.114/api/clientes";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<ClientesVO> cliente = new ArrayList<>();
                ClientesVO clientesVO = null;
                try {
                    JSONArray array = response.getJSONArray("clientes");
                    JSONObject object = null;
                    for(int i=0; i<array.length();i++){
                        clientesVO = new ClientesVO();
                        object = array.getJSONObject(i);
                        clientesVO.setId(""+object.getInt("id"));
                        clientesVO.setNombre(object.getString("name"));
                        clientesVO.setTelefono(object.getString("telefono"));

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

}