package com.example.fumigacionesmoncada.ui.citas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Crear_Citas extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
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




    }




    private void obtenerHora() {
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado +DOS_PUNTOS +"00" );
            }
        }, hora, minuto, false);

        recogerHora.show();

    }


    private void obtenerFecha() {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fecha.setText(   year  + "-" + (month + 1  ) + "-"+ dayOfMonth);
                profecha=(   year  + "-" + (month +1+1+1+1+1+1+1  ) + "-"+ dayOfMonth);

            }
        }, anio, mes, dia);
        datePickerDialog.show();

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date fechai = dateParser.parse(fecha.getText().toString().trim());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechai);
            c.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+30);
            Date newDate = c.getTime();
            String fechaf = dateParser.format(newDate);

            fecha.setText(fechaf);




        }catch (ParseException e){
            e.printStackTrace();
        }


    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        if (error.toString().equals("com.android.volley.ServerError")) {
            Toast.makeText(getApplicationContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

        } else if (error.toString().equals("com.android.volley.TimeoutError")) {
            Toast.makeText(getApplicationContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
        } else {
        }


    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(this,"Se registrado correctamente", Toast.LENGTH_SHORT).show();
        progreso.hide();
        nombre.setText("");
        direccion.setText("");
        precio.setText("");
        fecha.setText("");
        etHora.setText("");

    }
    public void cargarwebservice(){

        try {
            if(nombre.getText().toString().equals("")||direccion.getText().toString().equals("")||precio.getText().toString().equals("")||fecha.getText().toString().equals("")
                    || etHora.getText().toString().equals("")){
                Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
            }else {
                progreso = new ProgressDialog(this);
                progreso.setMessage("Cargando...");
                progreso.show();
                String ip=getString(R.string.ip);
                String url = ip+"/api/cita?Nombre=" + nombre.getText().toString()
                        + "&Direccion=" + direccion.getText().toString()+ "&Precio="+ precio.getText()+ "&FechaFumigacion="
                        + fecha.getText()+"&FechaProxima=" +fecha.getText()+ "&Hora=" + etHora.getText();
                url = url.replace(" ", "%20");
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this);
                request.add(jsonObjectRequest);
            }

        }catch (Exception exe){
            Toast.makeText(this,exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    public void registrar1(View view) {
        cargarwebservice();
    }

    public void fechass(View view) {
        obtenerFecha();
    }
}
