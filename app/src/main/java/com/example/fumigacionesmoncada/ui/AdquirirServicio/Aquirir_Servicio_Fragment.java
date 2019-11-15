package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.LoginActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.RegistarUsuarioNuevo;
import com.example.fumigacionesmoncada.ui.citas.Crear_Citas;
import com.example.fumigacionesmoncada.ui.clientes.Detalle_Cliente;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Aquirir_Servicio_Fragment extends Fragment  {
    private int dia, mes, anio;
    private EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    private static final int maximo = 20;
    private static final int minimo = 7;
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);

    final int minuto = c.get(Calendar.MINUTE);
    private Aquirir_Servicio_ViewModel servicioViewModel;
    private EditText mostrarNombre, mostrarDireccion, mostraraTelefono;
    private EditText Hora;
    String tokenUsuario;
    private Button pedir;

    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private String Usuario_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicioViewModel =
                ViewModelProviders.of(this).get(Aquirir_Servicio_ViewModel.class);
        View view = inflater.inflate(R.layout.fragment_ad_servicio, container, false);
        cargarPreferencias();


        mostrarNombre = (EditText) view.findViewById(R.id.nombres);
        mostrarDireccion = (EditText) view.findViewById(R.id.direccion);
        mostraraTelefono = (EditText) view.findViewById(R.id.telefono);
        Hora = (EditText) view.findViewById(R.id.hora);
        fecha = (EditText) view.findViewById(R.id.fecha);
        pedir = view.findViewById(R.id.pedir);
        request = Volley.newRequestQueue(getActivity());
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
        mostrarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "El Nombre no se puede modificar", Toast.LENGTH_SHORT).show();

            }
        });


        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mostrarNombre.getText().toString().equals("") || mostraraTelefono.getText().toString().equals("")
                        || mostraraTelefono.getText().toString().equals("") || fecha.getText().toString().equals("") ||
                        Hora.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo", Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
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
        return view;
    }

    public void aceptar() {
        validacion();
    }

    public void cancelar() {
    }


    private void cargarWebService() {

        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip=getString(R.string.ip);

        String url=ip+"/api/auth/peticioncita";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.hide();
                        Hora.setText("");
                        fecha.setText("");

                        Toast.makeText(getContext(), "Se ha registrado con exito", Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), error+"Revise su conexión a internet", Toast.LENGTH_LONG).show();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);}
    private void validacion() {
        if(mostrarNombre.getText().toString().equals("")||mostrarDireccion.getText().toString().equals("")||mostraraTelefono.getText().toString().equals("")
                || fecha.getText().toString().equals("") || Hora.getText().toString().equals("")){
            Toast.makeText(getContext(),"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
        }else {
                if (mostraraTelefono.getText().toString().length() < 8 ) {
                    Toast.makeText(getContext(), "No es un numero Telefonico", Toast.LENGTH_LONG).show();
                } else {


                            cargarWebService();



                }
            }
        }




    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
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
                            Toast.makeText(getContext(), "sinoda." + e, Toast.LENGTH_LONG).show();
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

    private void obtenerHora() {

        Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minutes = c.get(Calendar.MINUTE);


        final int dia_hoy = c.get(Calendar.DAY_OF_MONTH);
        final int mes_hoy = c.get(Calendar.MONTH);
        final int anio_hoy = c.get(Calendar.YEAR);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String fecha_usuario = anio + "-" + mes + "-" + dia;
        String fecha_actual = anio_hoy + "-" + mes_hoy + "-" + dia_hoy;
        try {
            final Date fecha_usuarioDate = formatter.parse(fecha_usuario);
            final Date fecha_actualDate = formatter.parse(fecha_actual);


            final TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    //Se validan la fecha actual con la tomada por el usuario para asi
                    //tomar una validacion distinta, si la fecha actual es igual a la que tomo el usuario
                    //la hora actual debe ser menor a la hora tomada.

                    if(fecha_usuarioDate.equals(fecha_actualDate)){
                        if(hour<hourOfDay){
                            String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                            String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "a.m.";
                            } else {
                                AM_PM = "p.m.";
                            }
                            Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");

                        }else{
                            Toast.makeText(getContext(), "La hora seleccionada no es correcta, debe ser mayor a la hora actual", Toast.LENGTH_LONG).show();
                        }


                    }else {
                        String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                        String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "a.m.";
                        } else {
                            AM_PM = "p.m.";
                        }
                        Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");

                    }

                }
            }, hora, minuto, false);

            recogerHora.show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void obtenerFecha() {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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

}