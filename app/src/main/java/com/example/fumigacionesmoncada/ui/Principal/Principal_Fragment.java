package com.example.fumigacionesmoncada.ui.Principal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.AdquirirServicioActivity;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RecyclerTouchListener;
import com.example.fumigacionesmoncada.notifications.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class Principal_Fragment extends Fragment   {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    RecyclerView recyclerUsuarios;
    ArrayList<ServiciosVO> listaUsuarios;
   RelativeLayout linearLayout;
    ProgressDialog dialog;
    String tokenUsuario;
    FloatingActionButton adservicio;

    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    private OnFragmentInteractionListener mListener;
    //firebase
    FirebaseUser currentUser;
    String variable;

    public Principal_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultaListaUsuarioImagenUrlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Principal_Fragment newInstance(String param1, String param2) {
        Principal_Fragment fragment = new Principal_Fragment();
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


    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View vista=inflater.inflate(R.layout.fragment_principal,container,false);
        ActionBar actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.gradient_background_app) ;
        cargarPreferencias();
        adservicio = vista.findViewById(R.id.ad_servicios);
        listaUsuarios=new ArrayList<>();
        recyclerUsuarios =  vista.findViewById(R.id.recycler_servicios);

        //firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        variable =  getActivity().getIntent().getStringExtra("USER");
        System.out.println(variable);

        //recyclerUsuarios.setLayoutManager(new GridLayoutManager(this.getContext(),2));
        recyclerUsuarios.setLayoutManager(new GridLayoutManager(getContext(),1,LinearLayoutManager.HORIZONTAL,false));
        recyclerUsuarios.setHasFixedSize(true);
linearLayout = vista.findViewById(R.id.error);
       // getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        cargarWebService();
        recyclerUsuarios.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerUsuarios, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ServiciosVO serviciosVO = listaUsuarios.get(position);
                Intent intent = new Intent(getContext(),DetalleServicioScroll.class);
                intent.putExtra("id",serviciosVO.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        updateToken(FirebaseInstanceId.getInstance().getToken());
        adservicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdquirirServicioActivity.class);
                startActivity(intent);
            }
        });

        return  vista;



    }





    private void cargarWebService() {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.consultaimage));
        dialog.show();
        String ip=getString(R.string.ip);

        String url=ip+"/api/recuperar";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ServiciosVO servicio=null;

                        JSONArray json=response.optJSONArray("servicio");

                        try {

                            for (int i=0;i<json.length();i++){
                                servicio=new ServiciosVO();
                                JSONObject jsonObject=null;
                                jsonObject=json.getJSONObject(i);

                                servicio.setId(String.valueOf(jsonObject.getInt("id")));
                                servicio.setTitulo(jsonObject.optString("nombre"));
                                servicio.setDescripcion(jsonObject.getString("descripcion"));
                                servicio.setRutaImagen(jsonObject.optString("foto"));
                                listaUsuarios.add(servicio);
                            }
                            dialog.hide();
                            ClaseAdapterImagen adapter=new ClaseAdapterImagen(listaUsuarios, getContext());
                            recyclerUsuarios.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.conexionServidor) +
                                    " "+response, Toast.LENGTH_LONG).show();
                            dialog.hide();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), R.string.presentamastarde, Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    linearLayout.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp);
                    Toast.makeText(getContext(), R.string.reviseconexion, Toast.LENGTH_LONG).show();
                } else {

                    linearLayout.setBackgroundResource(R.drawable.ic_cloud_off_black_24dp);
                    Toast.makeText(getContext(), R.string.reviseconexion , Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUser.getUid()).setValue(token1);
    }

}