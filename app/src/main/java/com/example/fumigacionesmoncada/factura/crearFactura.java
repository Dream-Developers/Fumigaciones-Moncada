package com.example.fumigacionesmoncada.factura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.citas.Crear_Citas;
import com.example.fumigacionesmoncada.ui.clientes.ClientesAdapter;
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class crearFactura extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String profecha;
    private int dia, mes, anio;
    EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    EditText etHora;
    EditText nombre, detalle, total, descuento;
    TextView col;
    Button registrar;
    String tokenUsuario;
    private static final int maximo = 20;
    private static final int minimo = 07;
    ProgressDialog progreso;
    ArrayList<ClientesVO> clientes;
    ListView lista_citas;
    facturas_adapter citasAdapter;
    ArrayList<Facturas> cita;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private ClientesAdapter adapterClientes;
    private AlertDialog alertDialogClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crear_factura);
        fecha = findViewById(R.id.reg_Fecha);
        nombre = findViewById(R.id.registro_nombres);
        detalle = findViewById(R.id.registrodetalle);
        registrar = findViewById(R.id.registrar12);
        descuento = findViewById(R.id.registro_descuento);
        cargarClientes();
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarCliente(v);
            }
        });

        total = findViewById(R.id.registro_Precio);
        cargarPreferencias();
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerFecha();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validacion();
            }
        });
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");

    }

    private void seleccionarCliente(View v) {
        AlertDialog.Builder builde = new AlertDialog.Builder(this);
        View dialogoLayout = getLayoutInflater().inflate(R.layout.lista_clientes_dialog, null);

        View buscadorLayout = getLayoutInflater().inflate(R.layout.buscador_alertdialog, null);
        ListView listaclientesDialogo = (ListView) dialogoLayout.findViewById(R.id.lista_clientes_dialog);
        TextView sinClientes = dialogoLayout.findViewById(R.id.sinclientes);
        EditText buscarET = dialogoLayout.findViewById(R.id.buscar_clientes);


        if (clientes.size() > 0) {
            sinClientes.setVisibility(View.GONE);
            builde.setTitle("Seleccione el cliente");

            adapterClientes = new ClientesAdapter(this, clientes);
            listaclientesDialogo.setAdapter(adapterClientes);
            buscarET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!(s == null)) {
                        ArrayList<ClientesVO> listaClientes = clientes;
                        try {
                            listaClientes = filtrarClientes(listaClientes, String.valueOf(s));
                            adapterClientes.filtrar(listaClientes);
                            adapterClientes.notifyDataSetChanged();

                        } catch (Exception e) {
                            Toast.makeText(crearFactura.this, "" + listaClientes, Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            listaclientesDialogo.setOnItemClickListener(this);
            builde.setView(dialogoLayout);

             alertDialogClientes = builde.create();
            alertDialogClientes.show();
        } else {
            sinClientes.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No hay clientes registrados aun", Toast.LENGTH_LONG).show();
        }

    }

    private ArrayList<ClientesVO> filtrarClientes(ArrayList<ClientesVO> listaTarea, String dato) {
        ArrayList<ClientesVO> listaFiltradaPermiso = new ArrayList<>();
        try {
            dato = dato.toLowerCase();
            for (ClientesVO permisos : listaTarea) {
                String nombre = permisos.getNombre().toLowerCase().trim();


                if (nombre.toLowerCase().contains(dato)) {
                    listaFiltradaPermiso.add(permisos);
                }
            }
            adapterClientes.filtrar(listaFiltradaPermiso);
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }

        return listaFiltradaPermiso;

    }

    private void cargarClientes() {
        String ip = getString(R.string.ip);

        String url = ip + "/api/clientes";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                clientes = new ArrayList<>();
                ClientesVO clientesVO = null;
                try {
                    JSONArray array = response.getJSONArray("clientes");
                    JSONObject object;
                    for (int i = 0; i < array.length(); i++) {
                        clientesVO = new ClientesVO();
                        object = array.getJSONObject(i);
                        clientesVO.setNombre(object.getString("name"));
                        clientesVO.setTelefono(object.getString("telefono"));
                        clientesVO.setId(String.valueOf(object.getInt("id")));
                        clientesVO.setImagen(object.getString("foto"));
                        clientesVO.setApellido(object.getString("recidencia"));

                        clientes.add(clientesVO);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.getStackTrace();
                Toast.makeText(crearFactura.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Content-Type", "application/json");
                parametros.put("X-Requested-With", "XMLHttpRequest");
                parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                return parametros;
            }
        };


        ClaseVolley.getIntanciaVolley(crearFactura.this).addToRequestQueue(jsonObjectRequest);

    }


    private void obtenerFecha() {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fecha.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

            }
        }, anio, mes, dia);
        datePickerDialog.show();

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date fechai = dateParser.parse(fecha.getText().toString().trim());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechai);
            c.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 30);
            Date newDate = c.getTime();
            String fechaf = dateParser.format(newDate);

            fecha.setText(fechaf);


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void cargarWebService() {

        progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip = getString(R.string.ip);

        String url = ip + "/api/factura";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.hide();

                        finish();

                        Toast.makeText(getApplicationContext(), "Se ha registrado con exito", Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getApplicationContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getApplicationContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), error + "Revise su conexión a internet", Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> parametros = new HashMap<>();
                parametros.put("Nombre", nombre.getText().toString());
                parametros.put("Detalle", detalle.getText().toString());
                parametros.put("Total", total.getText().toString());
                parametros.put("Fecha", fecha.getText().toString());
                parametros.put("Descuento", descuento.getText().toString());
                return parametros;

            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };
        //request.add(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validacion() {
        if (nombre.getText().toString().equals("") || total.getText().toString().equals("") || detalle.getText().toString().equals("") || fecha.getText().toString().equals("")
                || fecha.getText().toString().equals("")) {
            Toast.makeText(this, "Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo", Toast.LENGTH_LONG).show();
        } else {

            cargarWebService();

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ClientesVO cliente = (ClientesVO) parent.getItemAtPosition(position);
        nombre.setText(cliente.getNombre());
        alertDialogClientes.dismiss();
    }
}


