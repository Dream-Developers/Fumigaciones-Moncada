package com.example.fumigacionesmoncada.Database.serviciosAdministrador;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fumigacionesmoncada.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.RecyclerTouchListener;
import com.example.fumigacionesmoncada.ui.Principal.ClaseAdapterImagen;
import com.example.fumigacionesmoncada.ui.Principal.ServiciosVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServiciosAdministradorFragment extends Fragment  {
    RecyclerView recyclerUsuarios;
    ArrayList<ServiciosVO> listaUsuarios;
    ProgressDialog dialog;
    JsonObjectRequest jsonObjectRequest;
    String tokenUsuario;
    ConstraintLayout linearLayout;
    private OnFragmentInteractionListener mListener;
    ClaseAdapterImagen claseAdapterImagen;

    public ServiciosAdministradorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicios_administrador, container, false);
        listaUsuarios=new ArrayList<>();
        recyclerUsuarios =  view.findViewById(R.id.recycler_servicios);
        recyclerUsuarios.setLayoutManager(new GridLayoutManager(getContext(),2,LinearLayoutManager.HORIZONTAL,false));
        recyclerUsuarios.setHasFixedSize(true);
        linearLayout = view.findViewById(R.id.error);

        cargarWebService();
        cargarPreferencias();
        recyclerUsuarios.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerUsuarios, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ServiciosVO serviciosVO = listaUsuarios.get(position);
                Intent intent = new Intent(getContext(), DetalleServicioAdministradorScrollingActivity.class);
                intent.putExtra("id",serviciosVO.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                ServiciosVO serviciosVO =  listaUsuarios.get(position);
                eliminarServicio(serviciosVO,position);
            }
        }));
        return view;
    }

    private void eliminarServicio(final ServiciosVO serviciosVO, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.confirmacion));
        builder.setMessage(getString(R.string.eliminar_servicio));
        builder.setIcon(R.drawable.logofm);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarServicioWebService(serviciosVO.getId(), position);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
        }
        builder.setNegativeButton("No", null);
        builder.show();

    }


    @Override
    public void onResume() {
        cargarWebService();
        super.onResume();
    }

    private void eliminarServicioWebService(String id, final int position) {
        String ip=getString(R.string.ip);
        String url = ip+"/api/imagen/"+id+"/borrar";

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(getContext(), ""+object.getString("message"), Toast.LENGTH_LONG).show();
                    listaUsuarios= new ArrayList<>();
                    claseAdapterImagen = new ClaseAdapterImagen(listaUsuarios,getContext());
                    recyclerUsuarios.setAdapter(claseAdapterImagen);
                    cargarWebService();
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
                parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                return  parametros;
            }
        };
        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);

    }


    private void cargarWebService() {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Consultando Imagenes");
        dialog.show();
        String ip=getString(R.string.ip);

        String url=ip+"/api/recuperar";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ServiciosVO servicio=null;

                        listaUsuarios=new ArrayList<>();
                        JSONArray json=response.optJSONArray("servicio");

                        dialog.cancel();
                        try {

                            for (int i=0;i<json.length();i++){
                                servicio=new ServiciosVO();
                                JSONObject jsonObject=null;
                                jsonObject=json.getJSONObject(i);

                                servicio.setId(String.valueOf(jsonObject.getInt("id")));
                                servicio.setTitulo(jsonObject.optString("nombre"));
                                servicio.setDescripcion(jsonObject.optString("descripcion"));

                                servicio.setRutaImagen(jsonObject.optString("foto"));
                                listaUsuarios.add(servicio);
                            }
                            dialog.hide();
                            ClaseAdapterImagen adapter=new ClaseAdapterImagen(listaUsuarios, getContext());
                            recyclerUsuarios.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                                    " "+response, Toast.LENGTH_LONG).show();

                            dialog.hide();
                        }
                        Toast.makeText(getContext(), "Se cargaron los datos correctamente",Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    linearLayout.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp);
                    Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    linearLayout.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp);
                    Toast.makeText(getContext(), "Revise su conexión a internet" , Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), " " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
        dialog.hide();

    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        }catch (Exception e){

            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }

        }

    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
