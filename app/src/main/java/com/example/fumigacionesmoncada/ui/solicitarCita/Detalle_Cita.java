package com.example.fumigacionesmoncada.ui.solicitarCita;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.listadoPeticionesCita.Citas_Peticiones;
import com.example.fumigacionesmoncada.ui.citas.Crear_Citas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import com.example.fumigacionesmoncada.ui.citas.CitaVO;

public class Detalle_Cita extends Activity {
    private TextView nombre, direccion, txt_servicio, fecha, hora;
    private CitaVO citaVO;
    private int btnAceptar = 2;
    private int btnRechazar = 4;
    private String id, estado;
    private String Estado;
    private Button btnAceptado;
    private Button btnRechazado;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_cita);
        nombre = findViewById(R.id.detalle_nombre);
        direccion = findViewById(R.id.detalle_direccion);
        hora = findViewById(R.id.detalle_hora);
        fecha = findViewById(R.id.detalle_fecha);
        txt_servicio = findViewById(R.id.detalle_txt_servicio);
        btnAceptado = findViewById(R.id.btnAceptar);
        btnRechazado = findViewById(R.id.btnRechazar);


        id = getIntent().getStringExtra("id");

        cargarCitaWeb(id);

        btnAceptado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Detalle_Cita.this);
                builder.setTitle("Confirmacion");
                builder.setMessage("Desea aceptar esta cita");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        agregarCitaWebService(id, which);
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
                AlertDialog.Builder builder1 = builder.setNegativeButton("No", null);
                builder1.show();


            }


        });


        btnRechazado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(Detalle_Cita.this);
                builder2.setTitle("Confirmacion");
                builder2.setMessage("Esta seguro que desea Cancelar la Peticion de cita");
                builder2.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Citas_Peticiones citas = null;
                        int position = 1;
                        CancelarCitaWebService(id, position);

                    }


                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    builder2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();

                        }
                    });
                }
                builder2.setNegativeButton("No", null);
                builder2.show();
            }


        });

    }

    private void agregarCitaWebService(final String id, final int position) {
        final ProgressDialog progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando datos...");
        progreso.show();

        try {

            String ip = getString(R.string.ip);
            String url = ip + "/api/peticionesCitas/" + id + "/update";

            JSONObject parametros = new JSONObject();
            parametros.put("Estado_id", btnAceptar);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    try {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Detalle_Cita.this, Crear_Citas.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cargarCitaWeb(id);
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    Toast.makeText(getApplicationContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("volley", "onErrorResponse: " + error.networkResponse);
                }
            }) {

                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("Content-Type", "application/json");
                    parametros.put("X-Requested-With", "XMLHttpRequest");

                    return parametros;
                }
            };
            ClaseVolley.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


        } catch (Exception exe) {
            Toast.makeText(getApplicationContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
        }
        progreso.dismiss();

    }


    private void CancelarCitaWebService(final String id, final int position) {
        final ProgressDialog progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando datos...");
        progreso.show();

        try {

            String ip = getString(R.string.ip);
            String url = ip + "/api/peticionesCitas/" + id + "/update";

            JSONObject parametros = new JSONObject();
            parametros.put("Estado_id", btnRechazar);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progreso.dismiss();
                    try {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();

                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cargarCitaWeb(id);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.dismiss();
                    Toast.makeText(getApplicationContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("volley", "onErrorResponse: " + error.networkResponse);
                }
            }) {

                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("Content-Type", "application/json");
                    parametros.put("X-Requested-With", "XMLHttpRequest");

                    return parametros;
                }
            };
            ClaseVolley.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


        } catch (Exception exe) {
            Toast.makeText(getApplicationContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
        }
        progreso.dismiss();

    }


    private void cargarCitaWeb(final String id) {

        String ip = getString(R.string.ip);
        String url = ip + "/api/peticion/" + id + "/mostrar";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("cita");
                            nombre.setText(object.getString("Nombre"));
                            direccion.setText(object.getString("Direccion"));
                            txt_servicio.setText(object.getString("Servicio"));
                            fecha.setText(object.getString("FechaFumigacion"));
                            hora.setText(object.getString("Hora"));

                            Toast.makeText(getApplicationContext(), "nose", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Detalle_Cita.this, error + "df", Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id", id);
                return parametros;
            }
        };

        ClaseVolley.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);


    }


}


