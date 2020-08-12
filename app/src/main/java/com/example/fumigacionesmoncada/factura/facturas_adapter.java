package com.example.fumigacionesmoncada.factura;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.factura.Factura_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class facturas_adapter extends ArrayAdapter<Facturas> {
    private ListView lista_citas;

    TextView  Nombre , Detalle ,Fecha,Total ;
    EditText EditNombre,EditFecha,EditDetalle,EditTotal,EditDescuento;
int resultado;
    private int dia, mes, anio;

    private String tokenUsuario;
    private String Usuario_id;

    AlertDialog alertDialogFactura;
    private ArrayList<Facturas> usuarios_cita;
    public facturas_adapter(@NonNull Context context, ArrayList<Facturas>lista_citas) {
        super(context, R.layout.lista_facturas_item,lista_citas);
        this.usuarios_cita = lista_citas;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lista_facturas_item, null, false);


        TextView numeroFactura = convertView.findViewById(R.id.numerofactura);
        TextView fecha = convertView.findViewById(R.id.mostrarfecha);
        TextView nombre = convertView.findViewById(R.id.mostrarNombre);
        TextView total = convertView.findViewById(R.id.mostrartotal);
        TextView subtotal = convertView.findViewById(R.id.mostrarsubtotal);
        TextView descuento = convertView.findViewById(R.id.mostrardescuento);
        TextView detalle = convertView.findViewById(R.id.mosstardetalle);
        ImageButton guardar = convertView.findViewById(R.id.guar);


        final Facturas citas = getItem(position);
        numeroFactura.setText(citas.getNumero());
        fecha.setText(citas.getFecha());
        nombre.setText(citas.getNombre());
        subtotal.setText(citas.getTotal());
        detalle.setText(citas.getDetalle());
        descuento.setText(citas.getDescuento());
         int suma_a  = Integer.parseInt(citas.getTotal());
        int suma_b  = Integer.parseInt(citas.getDescuento());
        int resul = suma_a - suma_b;

        total.setText(Objects.toString(resul));

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFactura(citas,position);
            }
        });
        return convertView;





    }

    private void seleccionarFactura(final Facturas facturas, int position) {
        AlertDialog.Builder builde = new AlertDialog.Builder(getContext());
        View dialogoLayout = LayoutInflater.from(getContext()).inflate(R.layout.item_edit_alertdialogo, null);

        Nombre = dialogoLayout.findViewById(R.id.mostrarNombre);
        Detalle = dialogoLayout.findViewById(R.id.mostarDetalle);
        Total = dialogoLayout.findViewById(R.id.mostrartotal);
        Fecha = dialogoLayout.findViewById(R.id.mostrarfecha);
        EditNombre = dialogoLayout.findViewById(R.id.nombre);
        EditFecha = dialogoLayout.findViewById(R.id.fecha);
        EditDetalle = dialogoLayout.findViewById(R.id.detalle);
        EditTotal = dialogoLayout.findViewById(R.id.total);
        EditDescuento = dialogoLayout.findViewById(R.id.descuento);
        EditNombre.setText(facturas.getNombre());
        EditDetalle.setText(facturas.getDetalle());
        EditFecha.setText(facturas.getFecha());
        EditTotal.setText(facturas.getTotal());
        EditDescuento.setText(facturas.getDescuento());
        EditFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerFecha();
            }
        });

        builde.setCancelable(false);
        builde.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar(facturas.getNumero());
            }
        });
        builde.setNegativeButton( "Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });

        builde.setView(dialogoLayout);
        alertDialogFactura = builde.create();
        alertDialogFactura.show();

        alertDialogFactura.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notifyDataSetChanged();
            }
        });
    }

    public void aceptar(final String id) {


        try {
            if (EditNombre.getText().toString().trim().equals("")
                    || EditDetalle.getText().toString().trim().equals("")
                    || EditFecha.getText().toString().trim().equals("")
                    || EditTotal.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), "Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo", Toast.LENGTH_LONG).show();
            } else {
                if (Integer.parseInt(EditTotal.getText().toString()) < Integer.parseInt(EditDescuento.getText().toString())) {
                    EditDescuento.setError("El descuento no puede ser mayor que el total");
                } else {
                }
            }

            {

                String ip = getContext().getString(R.string.ip);
                String url = ip + "/api/actualizarFactura/" + id + "/update";
                JSONObject parametros = new JSONObject();
                parametros.put("Nombre", EditNombre.getText().toString());
                parametros.put("Detalle", EditDetalle.getText().toString());
                parametros.put("Fecha", EditFecha.getText().toString());
                parametros.put("Total", EditTotal.getText().toString());
                parametros.put("Descuento", EditDescuento.getText().toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Toast.makeText(getContext(), "Refresque la pantalla deslizando hacia abajo \n" + response.getString("message"), Toast.LENGTH_SHORT).show();

                            Factura_Fragment.refreshLayout.setRefreshing(true);
                            Factura_Fragment.refreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("volley", "onErrorResponse: " + error.networkResponse);
                    }
                }) {

                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("Content-Type", "application/json");
                        parametros.put("X-Requested-With", "XMLHttpRequest");
                        parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                        return parametros;
                    }
                };
                ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
            }

        } catch (Exception exe) {
            Toast.makeText(getContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }



    public void cancelar() {
    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        Usuario_id = preferences.getString("id", "");

    }

    private void obtenerFecha() {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext()
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                EditFecha.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

            }
        }, anio, mes, dia);
        datePickerDialog.show();

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date fechai = dateParser.parse(EditFecha.getText().toString().trim());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechai);
            c.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 30);
            Date newDate = c.getTime();
            String fechaf = dateParser.format(newDate);

            EditFecha.setText(fechaf);


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getCount() {
        return usuarios_cita.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Facturas getItem(int position) {
        return usuarios_cita.get(position);
    }

    public void filtrar(ArrayList<Facturas> permisosgetYset) {
        this.usuarios_cita = new ArrayList<>();
        this.usuarios_cita.addAll(permisosgetYset);
        notifyDataSetChanged();


    }
}
