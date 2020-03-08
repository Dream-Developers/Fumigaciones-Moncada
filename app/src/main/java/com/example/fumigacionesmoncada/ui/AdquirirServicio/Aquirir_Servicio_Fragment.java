package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Aquirir_Servicio_Fragment extends Fragment {
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
    private Spinner mostrarservicion;
    String tokenUsuario;
    private Button pedir;

    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private String Usuario_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        servicioViewModel =
                ViewModelProviders.of(this).get(Aquirir_Servicio_ViewModel.class);
        View view = inflater.inflate(R.layout.fragment_ad_servicio, container, false);
        cargarPreferencias();


        mostrarNombre = (EditText) view.findViewById(R.id.nombres);
        mostrarservicion = (Spinner) view.findViewById(R.id.servicio);
        mostrarDireccion = (EditText) view.findViewById(R.id.direccion);
        mostraraTelefono = (EditText) view.findViewById(R.id.telefono);
        Hora = (EditText) view.findViewById(R.id.hora);
        fecha = (EditText) view.findViewById(R.id.fechaFumigacion);
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


        String[] opciones = {getString(R.string.insectos), getString(R.string.roedores)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, opciones);
        mostrarservicion.setAdapter(adapter);
        mostrarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), R.string.nombreNoModificar, Toast.LENGTH_SHORT).show();

            }
        });


        pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mostrarNombre.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.nombreNoModificar, Toast.LENGTH_LONG).show();
                } else if (mostrarDireccion.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.campoResidencia, Toast.LENGTH_LONG).show();
                } else if (mostraraTelefono.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.campoTelefono, Toast.LENGTH_LONG).show();
                } else if (mostraraTelefono.getText().toString().trim().length() < 8) {
                    Toast.makeText(getContext(), R.string.NumeroInvalido, Toast.LENGTH_LONG).show();
                } else if (fecha.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.campoFecha, Toast.LENGTH_LONG).show();
                } else if (Hora.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.campoHora, Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle(R.string.importante);
                    dialogo1.setMessage(R.string.verificacionDeDatos + "\n" +
                            getString(R.string.nombreAdquirir) + " " + mostrarNombre.getText().toString() + "\n" +
                            getString(R.string.direccionAdquirir) + " " + mostrarDireccion.getText().toString() + "\n" +
                            getString(R.string.telefonoAdquirir) + " " + mostraraTelefono.getText().toString() + "\n"

                    );
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            aceptar();
                        }
                    });
                    dialogo1.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
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

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip = getString(R.string.ip);

        String url = ip + "/api/peticioncita";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progreso.hide();
                        Hora.setText("");
                        fecha.setText("");

                        Toast.makeText(getContext(), R.string.registroAdquirir, Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                Log.i("errorVolley", String.valueOf(error.getStackTrace()));
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), R.string.presentamosProblemas, Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), R.string.reviseConexion, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.reviseConexion, Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new HashMap<>();
                parametros.put("Nombre", mostrarNombre.getText().toString());
                parametros.put("Direccion", mostrarDireccion.getText().toString());
                parametros.put("Telefono", mostraraTelefono.getText().toString());
                parametros.put("FechaFumigacion", fecha.getText().toString());
                parametros.put("Hora", Hora.getText().toString());
                parametros.put("Servicio", mostrarservicion.getSelectedItem().toString());
                parametros.put("User_id", Usuario_id);

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void validacion() {
        if (mostrarNombre.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), R.string.nombreNoModificar, Toast.LENGTH_LONG).show();
        } else if (mostrarDireccion.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), R.string.campoResidencia, Toast.LENGTH_LONG).show();
        } else if (mostraraTelefono.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), R.string.campoTelefono, Toast.LENGTH_LONG).show();
        } else if (mostraraTelefono.getText().toString().trim().length() < 8) {
            Toast.makeText(getContext(), R.string.NumeroInvalido, Toast.LENGTH_LONG).show();
        } else if (fecha.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), R.string.campoFecha, Toast.LENGTH_LONG).show();
        } else if (Hora.getText().toString().trim().equals("")) {
            Toast.makeText(getContext(), R.string.campoHora, Toast.LENGTH_LONG).show();
            } else {


                cargarWebService();


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
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), R.string.presentamosProblemas, Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(getContext(), R.string.reviseConexion, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),R.string.reviseConexion , Toast.LENGTH_SHORT).show();
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

            final TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                                Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");
                            } else {
                                Toast.makeText(getContext(), R.string.horarioDeAtencion, Toast.LENGTH_LONG).show();
                                Hora.setText("");
                            }

                        } else {
                            Toast.makeText(getContext(),R.string.horaAlerta, Toast.LENGTH_LONG).show();
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
                            Hora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + DOS_PUNTOS + "00");


                        } else {
                            Hora.setText("");
                            Toast.makeText(getContext(), R.string.horarioDeAtencion, Toast.LENGTH_LONG).show();
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