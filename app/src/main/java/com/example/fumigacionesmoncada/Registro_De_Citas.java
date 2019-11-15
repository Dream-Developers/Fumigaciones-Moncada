package com.example.fumigacionesmoncada;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.fumigacionesmoncada.ui.citas.Citas;
import com.example.fumigacionesmoncada.ui.citas.Citas_Adapter;
import com.example.fumigacionesmoncada.ui.citas.Crear_Citas;
import com.example.fumigacionesmoncada.ui.solicitarCita.CitaVO;
import com.example.fumigacionesmoncada.ui.solicitarCita.CitasAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Registro_De_Citas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Registro_De_Citas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Registro_De_Citas extends Fragment {


    ListView lista_citas;
    Regstro_peticiones_adapter citasAdapter;
    ArrayList<Citas_Peticiones> cita;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String tokenUsuario;
    private String Usuario_id;
    ProgressDialog progreso;
    private int cancelar = 3;
    public Registro_De_Citas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Registro_De_Citas.
     */
    // TODO: Rename and change types and number of parameters
    public static Registro_De_Citas newInstance(String param1, String param2) {
        Registro_De_Citas fragment = new Registro_De_Citas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro__de__citas, container, false);
        lista_citas = view.findViewById(R.id.registro_citas);
        cita = new ArrayList<>();
        lista_citas.setAdapter(new Regstro_peticiones_adapter(getContext(), cita));
        lista_citas.setVisibility(View.VISIBLE);

        lista_citas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Citas_Peticiones citas = (Citas_Peticiones) parent.getItemAtPosition(position);
                eliminarCitas(citas, position);
                return true;
            }
        });

        cargarPreferencias();
        cargarCitas();
        setHasOptionsMenu(true);
        return view;
    }

    private void eliminarCitas(final Citas_Peticiones cit, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmacion");
        builder.setMessage("Esta seguro que desea Cancelar la Peticion de cita");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarCitaWebService(cit.getId(),cit.getEstado(), position);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();

                    cargarCitas();

                }
            });
        }
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void eliminarCitaWebService(String id,String Estado, final int position) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando datos...");
        progreso.show();

        if (Estado == "1"){
        try {
            String ip = getString(R.string.ip);
            String url = ip + "/api/peticionesCitas/" + id + "/update";

            JSONObject parametros = new JSONObject();
            parametros.put("Estado_id", cancelar);

            // parametros.put("email",mostrarCorreo.getText().toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    try {
                        Toast.makeText(getContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cargarCitas();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    Toast.makeText(getContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("volley", "onErrorResponse: " + error.networkResponse);
                }
            }) {

                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("Content-Type", "application/json");
                    parametros.put("X-Requested-With", "XMLHttpRequest");

                    return parametros;
                }
            };
            ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


        } catch(Exception exe){
        Toast.makeText(getContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
    }}else {
            progreso.dismiss();
            Toast.makeText(getContext(), "Solo Puede cancelar Las citas Pendinetes", Toast.LENGTH_LONG).show();

        }

    }


    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        Usuario_id = preferences.getString("id", "");

    }
    private void cargarCitas() {
        String ip=getString(R.string.ip);
        String url = ip+"/api/recuperar/"+Usuario_id+"/peticionesCitas";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                cita = new ArrayList<>();
                Citas_Peticiones citas = null;
                try {
                    JSONObject object  = response;
                    JSONArray array = response.getJSONArray("citas");
                    for (int i = 0; i < array.length(); i++) {
                        citas = new Citas_Peticiones();
                        object = array.getJSONObject(i);
                        citas.setNombre(object.getString("Nombre"));
                        citas.setNombre(object.getString("Nombre"));
                        citas.setDireccion(object.getString("Direccion"));
                        citas.setFecha(object.getString("FechaFumigacion"));
                        citas.setHora(object.getString("Hora"));
                        citas.setEstado(object.getString("Estado_id"));
                        citas.setId(object.getString("id"));

                        cita.add(citas);
                        citasAdapter = new Regstro_peticiones_adapter(getContext(), cita);
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













    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
