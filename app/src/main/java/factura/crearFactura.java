package factura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.fumigacionesmoncada.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class crearFactura extends AppCompatActivity {
    private String profecha;
    private int dia, mes, anio;
    EditText fecha;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    EditText etHora;
    EditText nombre,detalle,total;
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

        setContentView(R.layout.activity_crear_factura);
        fecha = findViewById(R.id.reg_Fecha);
        nombre = findViewById(R.id.registro_nombres);
        detalle = findViewById(R.id.registrodetalle);
        registrar = findViewById(R.id.registrar12);

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

        String url=ip+"/api/factura";

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
                parametros.put("Detalle",detalle.getText().toString());
                parametros.put("Total",total.getText().toString());
                parametros.put("Fecha",fecha.getText().toString());
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
        if(nombre.getText().toString().equals("")||total.getText().toString().equals("")||detalle.getText().toString().equals("")||fecha.getText().toString().equals("")
                || fecha.getText().toString().equals("")){
            Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
        }else {

            cargarWebService();

        }

    }

}


