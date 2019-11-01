package Perfil;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;






public class PerfilFragment extends Fragment {
    private String tokenUsuario;
    private EditText mostrarNombre,mostrarDireccion,mostraraTelefono,mostrarCorreo;
    private TextView mostrarnombre1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        mostrarnombre1=view.findViewById(R.id.txtnombres);
        mostrarNombre = view.findViewById(R.id.nombresP);
        mostrarDireccion = view.findViewById(R.id.direccionP);
        mostraraTelefono = view.findViewById(R.id.telefonoP);
        mostrarCorreo = view.findViewById(R.id.correoP);
        cargarPreferencias();
        cargarClienteWeb();
        return view;



    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");

    }
    private void cargarClienteWeb() {
        String ip = getString(R.string.ip);

        String url = ip+"/api/auth/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response;
                            mostrarnombre1.setText(object.getString("name"));
                            mostrarNombre.setText(object.getString("name"));
                            mostraraTelefono.setText(object.getString("telefono"));
                            mostrarDireccion.setText(object.getString("recidencia"));
                            mostrarCorreo.setText(object.getString("email"));



                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "sinoda."+e, Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), "Presentamos problemas intentelo más tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                }  else {
                    Toast.makeText(getContext(), " "+error.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }};

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);


    }



}
