package com.example.fumigacionesmoncada.ui.citas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.MainActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.clientes.ClientesAdapter;
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CitasFragment extends Fragment {
private FloatingActionButton addcita;
    ListView lista_citas;
    Citas_Adapter citasAdapter;

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
        return view;


    }

    private void cargarCitas() {
        String ip = "http://192.168.137.1/api/citas";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ip, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                    ArrayList<Citas> cita = new ArrayList<>();
                    Citas citas = null;
                    try {
                        JSONArray array = response.getJSONArray("citas");
                        JSONObject object = null;
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

}



