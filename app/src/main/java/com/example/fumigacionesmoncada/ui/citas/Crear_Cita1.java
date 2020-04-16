package com.example.fumigacionesmoncada.ui.citas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
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

public class Crear_Cita1 extends AppCompatActivity   implements AdapterView.OnItemClickListener{
    private String profecha;
    private int dia, mes, anio;
    private EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    EditText etHora;
    EditText nombre, direccion, precio;
    TextView col;
    Button registrar;
    String id_usuario;
    String tokenUsuario;
    private static final int maximo = 20;
    private static final int minimo = 07;
    ProgressDialog progreso;
    ArrayList<ClientesVO> clientes;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ClientesAdapter clientesAdapter;
    private ClientesAdapter adapterClientes;
    private AlertDialog alertDialogClientes;
    private String nombretxt;
    private String direcciontxt,fechatxt,horatxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_citas);

        fecha = findViewById(R.id.registro_Fecha);
        etHora = findViewById(R.id.registro_Hora);
        nombre = findViewById(R.id.registro_nombres);
        direccion = findViewById(R.id.registro_direccion);
        precio = findViewById(R.id.registro_Precio);
        registrar = findViewById(R.id.registrar1);
        request = Volley.newRequestQueue(this);
        // llenarSpinner();
        cargarPreferencias1();
        cargarClientes();
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarCliente(v);
            }
        });
        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });
        etHora.setEnabled(false);
        cargarPreferencias();



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
                            Toast.makeText(Crear_Cita1.this, "" + listaClientes, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(Crear_Cita1.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders()throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("X-Requested-With", "XMLHttpRequest");
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };


        ClaseVolley.getIntanciaVolley(Crear_Cita1.this).addToRequestQueue(jsonObjectRequest);

    }

    private void obtenerHora() {

        Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minutes = c.get(Calendar.MINUTE);


        final int dia_hoy = c.get(Calendar.DAY_OF_MONTH);
        final int mes_hoy = c.get(Calendar.MONTH);
        final int anio_hoy = c.get(Calendar.YEAR);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String fecha_usuario = fecha.getText().toString();
        String fecha_actual = anio_hoy + "-" + (mes_hoy + 1) + "-" + dia_hoy;
        try {
            final Date fecha_usuarioDate = formatter.parse(fecha_usuario);
            final Date fecha_actualDate = formatter.parse(fecha_actual);

            final TimePickerDialog recogerHora = new TimePickerDialog(Crear_Cita1.this, new TimePickerDialog.OnTimeSetListener() {
                @Override

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    //Se validan la FechaFumigacion actual con la tomada por el usuario para asi
                    //tomar una validacion distinta, si la FechaFumigacion actual es igual a la que tomo el usuario
                    //la Hora actual debe ser menor a la Hora tomada.
                    if (fecha_usuarioDate.equals(fecha_actualDate)) {

                        if (hour < hourOfDay) {
                            if (hourOfDay < maximo && hourOfDay > minimo) {
                                String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                                String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                                String AM_PM;
                                if (hourOfDay < 12) {
                                    AM_PM = "a.m.";
                                } else {
                                    AM_PM = "p.m.";
                                }
                                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");
                            } else {
                                Toast.makeText(Crear_Cita1.this, "El Horario de atencion es de 7:00AM a 7:00PM ", Toast.LENGTH_LONG).show();
                                etHora.setText("");
                            }

                        } else {
                            Toast.makeText(Crear_Cita1.this, "La Hora seleccionada no es correcta, debe ser mayor a la Hora actual", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        if (hourOfDay < maximo && hourOfDay > minimo) {
                            String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                            String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "a.m.";
                            } else {
                                AM_PM = "p.m.";
                            }
                            etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");


                        } else {
                            etHora.setText("");
                            Toast.makeText(Crear_Cita1.this, "El Horario de atencion es de 7:00AM a 7:00PM", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, hora, minuto, false);

            recogerHora.show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void cargarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        id_usuario = preferences.getString("id", "");

    }
    private void cargarPreferencias1() {




        Bundle bundle = getIntent().getExtras();
        nombretxt = bundle.getString("nombre");
        direcciontxt = bundle.getString("direccion");
        fechatxt = bundle.getString("fecha");
        horatxt = bundle.getString("hora");

        nombre.setText(nombretxt);
        fecha.setText(fechatxt);
        direccion.setText(direcciontxt);
        etHora.setText(horatxt);
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
                etHora.setEnabled(true);
                etHora.setText("");

            }
        }, anio, mes, dia);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
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

        String url = ip + "/api/cita";
        try {

            JSONObject parametros = new JSONObject();
            parametros.put("Nombre", nombre.getText().toString());
            parametros.put("Direccion", direccion.getText().toString());
            parametros.put("Precio", precio.getText().toString());
            parametros.put("FechaFumigacion", fecha.getText().toString());
            parametros.put("Hora", etHora.getText().toString());
            parametros.put("FechaProxima", fecha.getText().toString());
            parametros.put("id_usuario", id_usuario);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,parametros,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progreso.hide();

                            finish();

                            Toast.makeText(Crear_Cita1.this, "Se ha registrado con exito", Toast.LENGTH_SHORT).show();

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
                public Map<String, String> getHeaders()throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("X-Requested-With", "XMLHttpRequest");
                    params.put("Authorization", "Bearer" + " " + tokenUsuario);


                    return params;
                }
            };
            //request.add(stringRequest);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch(Exception exe){
            Toast.makeText(getApplicationContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validacion() {
        if (nombre.getText().toString().equals("") || direccion.getText().toString().equals("") || precio.getText().toString().equals("") || fecha.getText().toString().equals("")
                || etHora.getText().toString().equals("")) {
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

    public void registrar1(View view) {
        validacion();
    }

    public void fechass(View view) {
        obtenerFecha();
    }



}

