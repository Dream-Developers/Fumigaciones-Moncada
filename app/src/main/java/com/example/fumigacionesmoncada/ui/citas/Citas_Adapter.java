package com.example.fumigacionesmoncada.ui.citas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.factura.Factura_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Citas_Adapter extends ArrayAdapter<Citas> {

    private ListView lista_citas;
    private String tokenUsuario;
    private String Usuario_id;

    AlertDialog alertDialogFactura;
    static SwipeRefreshLayout refreshLayout;
    private ArrayList<Citas> usuarios;
    public Citas_Adapter(@NonNull Context context, ArrayList<Citas>lista_citas) {
        super(context, R.layout.item_lista_citas,lista_citas);
        this.usuarios = lista_citas;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_citas, null, false);

        TextView nombre = convertView.findViewById(R.id.nombre_cita);
        TextView precio = convertView.findViewById(R.id.precio_cita);

        ImageView guardar = convertView.findViewById(R.id.borrar);
        TextView fecha = convertView.findViewById(R.id.fecha_cita);
        TextView hora = convertView.findViewById(R.id.hora_cita);
       
        final Citas citas = getItem(position);

cargarPreferencias();
        nombre.setText(citas.getNombre());

        precio.setText(citas.getPrecio());
        fecha.setText(citas.getFechaFumigacion());
        hora.setText(citas.getHora());

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarCitas(citas,position);
            }
        });
        return convertView;

    }
        private void eliminarCitas(final Citas cit, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.confirmacion);
            builder.setMessage(R.string.eliminarlista);
            builder.setIcon(R.drawable.fm);
            builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    eliminarCitaWebService(String.valueOf(cit.getId()),position);

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
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        }
        private void cargarPreferencias() {
            SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            tokenUsuario = preferences.getString("token", "");
            Usuario_id = preferences.getString("id", "");

        }
    private void eliminarCitaWebService(String id, final int position) {

        String ip = getContext().getString(R.string.ip);
        String url = ip+"/api/citas/"+id+"/borrar";

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(getContext(), "se Borro\n"
         +            "Refresque, deslizando hacia abajo", Toast.LENGTH_LONG).show();
                    CitasFragment.refreshLayout.setRefreshing(true);
                    CitasFragment.refreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.errorborrra, Toast.LENGTH_SHORT).show();
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


    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Citas getItem(int position) {
        return usuarios.get(position);
    }

    public void filtrar(ArrayList<Citas> permisosgetYset) {
        this.usuarios = new ArrayList<>();
        this.usuarios.addAll(permisosgetYset);
        notifyDataSetChanged();


    }

}
