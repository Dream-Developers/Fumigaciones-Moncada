package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ui.AdquirirServicio.Aquirir_Servicio_ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdquirirServicioActivity extends AppCompatActivity {
    private int dia, mes, anio;
    private EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    private static final int maximo = 20;
    private static final int minimo = 07;
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);

    final int minuto = c.get(Calendar.MINUTE);
    private Aquirir_Servicio_ViewModel servicioViewModel;
    private EditText mostrarNombre, mostrarDireccion, mostraraTelefono;
    private EditText Hora;
    String tokenUsuario;
    private Button pedir;
    private Spinner mostrarservicion;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private String Usuario_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adquirir_servicio);

        cargarPreferencias();


        setTitle("Adquirir servicio");
        mostrarNombre = (EditText) findViewById(R.id.nombres);
        mostrarDireccion = (EditText) findViewById(R.id.direccion);
        mostraraTelefono = (EditText) findViewById(R.id.telefono);
        Hora = (EditText) findViewById(R.id.hora);
        fecha = (EditText) findViewById(R.id.fecha);
        pedir = findViewById(R.id.pedir);
        mostrarservicion = findViewById(R.id.servicio);
        request = Volley.newRequestQueue(this);
        Hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });
        Hora.setEnabled(false);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });
        cargarClienteWeb();

        String [] opciones = {"Inceptos","Roedores"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,opciones);
        mostrarservicion.setAdapter(adapter);
        mostrarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdquirirServicioActivity.this, "El Nombre no se puede modificar", Toast.LENGTH_SHORT).show();

            }
        });
        mostrarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdquirirServicioActivity.this, "El Nombre no se puede modificar", Toast.LENGTH_SHORT).show();

            }
        });


        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mostrarNombre.getText().toString().equals("") || mostraraTelefono.getText().toString().equals("")
                        || mostraraTelefono.getText().toString().equals("") || fecha.getText().toString().equals("") ||
                        Hora.getText().toString().equals("")) {
                    Toast.makeText(AdquirirServicioActivity.this, "Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo", Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(AdquirirServicioActivity.this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Estos Datos Son los correctos ?" + "\n" +
                            "Nombre  " + ":  " + mostrarNombre.getText().toString() + "\n" +
                            "Direccion De Fumigacion " + ":  " + mostrarDireccion.getText().toString() + "\n" +
                            "Telefono  " + ":  " + mostraraTelefono.getText().toString() + "\n"

                    );
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            aceptar();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            cancelar();
                        }
                    });
                    dialogo1.show();
                }
            }
        });

    }

    public void aceptar() {
        validacion();
    }

    public void cancelar() {
    }


    private void cargarWebService() {

        progreso=new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip=getString(R.string.ip);

        String url=ip+"/api/peticioncita";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.hide();
                        Hora.setText("");
                        fecha.setText("");

                        Toast.makeText(AdquirirServicioActivity.this, "Se ha registrado con éxito", Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(AdquirirServicioActivity.this, "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(AdquirirServicioActivity.this, "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdquirirServicioActivity.this, error+"Revise su conexión a internet", Toast.LENGTH_LONG).show();

                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros=new HashMap<>();
                parametros.put("Nombre",mostrarNombre.getText().toString());
                parametros.put("Direccion",mostrarDireccion.getText().toString());
                parametros.put("Telefono",mostraraTelefono.getText().toString());
                parametros.put("FechaFumigacion",fecha.getText().toString());
                parametros.put("Hora",Hora.getText().toString());
                parametros.put("Servicio",mostrarservicion.getSelectedItem().toString());
                parametros.put("User_id",Usuario_id);

                return parametros;
            }@Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };
        //request.add(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);}
    private void validacion() {
        if(mostrarNombre.getText().toString().equals("")||mostrarDireccion.getText().toString().equals("")||mostraraTelefono.getText().toString().equals("")
                || fecha.getText().toString().equals("") || Hora.getText().toString().equals("")){
            Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
        }else {
            if (mostraraTelefono.getText().toString().length() < 8 ) {
                Toast.makeText(this, "No es un numero Telefonico", Toast.LENGTH_LONG).show();
            } else {


                cargarWebService();



            }
        }
    }




    private void cargarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        Usuario_id = preferences.getString("id", "");

    }

    private void cargarClienteWeb() {
        String ip = getString(R.string.ip);

        String url = ip + "/api/auth/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response;
                            mostrarNombre.setText(object.getString("name"));
                            mostraraTelefono.setText(object.getString("telefono"));
                            mostrarDireccion.setText(object.getString("recidencia"));


                        } catch (JSONException e) {
                            Toast.makeText(AdquirirServicioActivity.this, "sinoda." + e, Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(AdquirirServicioActivity.this, "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(AdquirirServicioActivity.this, "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdquirirServicioActivity.this, " " + error.toString(), Toast.LENGTH_SHORT).show();
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

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }




    private void obtenerFecha() {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fecha.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                Hora.setEnabled(true);
                Hora.setText("");

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
    private void obtenerHora() {

        Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minutes = c.get(Calendar.MINUTE);


        final int dia_hoy = c.get(Calendar.DAY_OF_MONTH);
        final int mes_hoy = c.get(Calendar.MONTH);
        final int anio_hoy = c.get(Calendar.YEAR);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String fecha_usuario = fecha.getText().toString();
        String fecha_actual = anio_hoy + "-" + (mes_hoy +1) + "-" + dia_hoy;
        try {
            final Date fecha_usuarioDate = formatter.parse(fecha_usuario);
            final Date fecha_actualDate = formatter.parse(fecha_actual);

            final TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    //Se validan la fecha actual con la tomada por el usuario para asi
                    //tomar una validacion distinta, si la fecha actual es igual a la que tomo el usuario
                    //la hora actual debe ser menor a la hora tomada.
                    if(fecha_usuarioDate.equals(fecha_actualDate)){

                        if(hour<hourOfDay){
                            if (hourOfDay<maximo&&hourOfDay>minimo) {
                                String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                                String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                                String AM_PM;
                                if (hourOfDay < 12) {
                                    AM_PM = "a.m.";
                                } else {
                                    AM_PM = "p.m.";
                                }
                                Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");
                            }else {
                                Toast.makeText(AdquirirServicioActivity.this, "El Horario de atencion es de 7:00AM a 7:00PM", Toast.LENGTH_LONG).show();
                                Hora.setText("");
                            }

                        }else{
                            Toast.makeText(AdquirirServicioActivity.this, "La hora seleccionada no es correcta, debe ser mayor a la hora actual", Toast.LENGTH_LONG).show();
                        }


                    }else {
                        if (hourOfDay<maximo&&hourOfDay>minimo) {
                            String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                            String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "a.m.";
                            } else {
                                AM_PM = "p.m.";
                            }
                            Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");


                        } else {
                            Hora.setText("");
                            Toast.makeText(AdquirirServicioActivity.this, "El Horario de atencion es de 7:00AM a 7:00PM ", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }, hora, minuto, false);

            recogerHora.show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}



