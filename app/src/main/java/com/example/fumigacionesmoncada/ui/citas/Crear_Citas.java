package com.example.fumigacionesmoncada.ui.citas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.fumigacionesmoncada.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Crear_Citas extends AppCompatActivity {
    private String profecha;
    private int dia, mes, anio;
    private EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    EditText etHora;
    EditText nombre,direccion,precio;
    TextView col;
    Button registrar;
    String tokenUsuario;
    private static final int maximo = 20;
    private static final int minimo = 07;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear__citas); fecha = findViewById(R.id.registro_Fecha);
        etHora = findViewById(R.id.registro_Hora);
        nombre = findViewById(R.id.registro_nombres);
        direccion = findViewById(R.id.registro_direccion);
        precio = findViewById(R.id.registro_Precio);
        registrar=findViewById(R.id.registrar1);
        request = Volley.newRequestQueue(this);
        // llenarSpinner();

        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerHora();
            }
        });
        etHora.setEnabled(false);

cargarPreferencias();


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

            final TimePickerDialog recogerHora = new TimePickerDialog(Crear_Citas.this, new TimePickerDialog.OnTimeSetListener() {
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
                                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");
                            }else {
                                Toast.makeText(Crear_Citas.this, "El Horario de atencion es de 7:00AM a 7:00PM ", Toast.LENGTH_LONG).show();
                                etHora.setText("");
                            }

                        }else{
                            Toast.makeText(Crear_Citas.this, "La hora seleccionada no es correcta, debe ser mayor a la hora actual", Toast.LENGTH_LONG).show();
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
                            etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");


                        } else {
                            etHora.setText("");
                            Toast.makeText(Crear_Citas.this, "El Horario de atencion es de 7:00AM a 7:00PM", Toast.LENGTH_LONG).show();
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

        progreso=new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip=getString(R.string.ip);

        String url=ip+"/api/auth/cita";

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
                    Toast.makeText(getApplicationContext(), error+"Revise su conexión a internet", Toast.LENGTH_LONG).show();

                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String,String> parametros=new HashMap<>();
                parametros.put("Nombre",nombre.getText().toString());
                parametros.put("Direccion",direccion.getText().toString());
                parametros.put("Precio",precio.getText().toString());
                parametros.put("FechaFumigacion",fecha.getText().toString());
                parametros.put("Hora",etHora.getText().toString());
                parametros.put("FechaProxima",fecha.getText().toString());
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
        if(nombre.getText().toString().equals("")||direccion.getText().toString().equals("")||precio.getText().toString().equals("")||fecha.getText().toString().equals("")
                || etHora.getText().toString().equals("")){
            Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
        }else {

            cargarWebService();

        }

    }
    public void registrar1(View view) {
        validacion();
    }

    public void fechass(View view) {
        obtenerFecha();
    }
}
