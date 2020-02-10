package com.example.fumigacionesmoncada.CitasSync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.citas.Citas;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitasSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = CitasSyncAdapter.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContractCitas.Columnas._ID,
            ContractCitas.Columnas.ID_REMOTA,
            ContractCitas.Columnas.NOMBRE,
            ContractCitas.Columnas.FECHA_PROXIMA,
            ContractCitas.Columnas.DIRECCION,
            ContractCitas.Columnas.FECHA_FUMIGACION,
            ContractCitas.Columnas.PRECIO,
            ContractCitas.Columnas.HORA,
            ContractCitas.Columnas.ID_USUARIO
    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MONTO = 2;
    public static final int COLUMNA_ETIQUETA = 3;
    public static final int COLUMNA_FECHA = 4;
    public static final int COLUMNA_DESCRIPCION = 5;

    public CitasSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public CitasSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static void inicializarSyncAdapter(Context context) {
        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "onPerformSync()...");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {
            realizarSincronizacionLocal(syncResult);
        } else {
            realizarSincronizacionRemota();
        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente.");

        String url = getContext().getString(R.string.ip);
        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        url+"/api/citas",null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGet(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                              //  Log.d(TAG, error.networkResponse.toString());
                            }
                        }
                )
        );
    }

    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) {

        try {
            String estado = response.getString("estado");
            VolleyError error = new VolleyError();
            if (error!=null) {
                Log.d(TAG, "entro");
                actualizarDatosLocales(response, syncResult);
            }else{
                Log.i(TAG, "Error al actualizar Base de datos");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_ID);

                ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                "",
                                null
                                ,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
    }

    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSucios() {
        Uri uri = ContractCitas.CONTENT_URI;
        String selection = ContractCitas.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractCitas.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractCitas.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION, selection, selectionArgs, null);
    }

    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion() {
        Uri uri = ContractCitas.CONTENT_URI;
        String selection = ContractCitas.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContractCitas.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContractCitas.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContractCitas.Columnas.ESTADO, ContractCitas.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción:" + results);
    }

    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param idRemota id remota
     */
    private void finalizarActualizacion(String idRemota, int idLocal) {
        Uri uri = ContractCitas.CONTENT_URI;
        String selection = ContractCitas.Columnas._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContractCitas.Columnas.PENDIENTE_INSERCION, "0");
        v.put(ContractCitas.Columnas.ESTADO, ContractCitas.ESTADO_OK);
        v.put(ContractCitas.Columnas.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);
    }

    /**
     * Procesa los diferentes tipos de respuesta obtenidos del servidor
     *
     * @param response Respuesta en formato Json
     */
    public void procesarRespuestaInsert(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");
            // Obtener identificador del nuevo registro creado en el servidor
            String idRemota = response.getString("id");

            switch (estado) {
                case "1":
                    Log.i(TAG, mensaje);
                    finalizarActualizacion(idRemota, idLocal);
                    break;

                case "2":
                    Log.i(TAG, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {
        JSONArray citasArray = null;

        try {
            citasArray = response.getJSONArray("citas");
            Log.i(TAG, String.valueOf(citasArray));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        //parsear con Gson

        Citas[] res = gson.fromJson(citasArray != null ? citasArray.toString() : null, Citas[].class);
        List<Citas> data = Arrays.asList(res);
        Log.d(TAG, ""+data);

        //Lista para la recoleccion de datos pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        //Tabla hash para recibir las entradas entrantes
        HashMap<Integer, Citas> expenseMap = new HashMap<Integer, Citas>();
        for (Citas e : data) {
            expenseMap.put(e.id,e);
        }


        // Consultar registros remotos actuales
        Uri uri = ContractCitas.CONTENT_URI;
        String select = ContractCitas.Columnas.ID_REMOTA;
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        int id;
        String nombre;

        String direccion;
        String precio;
        String fechaFumigacion;
        String fechaProxima;
        String hora;
        String id_usuario;


        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getInt(c.getColumnIndex(ContractCitas.Columnas.ID_REMOTA));
            nombre = c.getString(c.getColumnIndex(ContractCitas.Columnas.NOMBRE));
            direccion = c.getString(c.getColumnIndex(ContractCitas.Columnas.DIRECCION));
            precio = c.getString(c.getColumnIndex(ContractCitas.Columnas.PRECIO));
            fechaFumigacion = c.getString(c.getColumnIndex(ContractCitas.Columnas.FECHA_FUMIGACION));
            fechaProxima=c.getString(c.getColumnIndex(ContractCitas.Columnas.FECHA_PROXIMA));
            hora=c.getString(c.getColumnIndex(ContractCitas.Columnas.HORA));


            Citas match = expenseMap.get(id);

            //Esta entrada existe
            if (match != null) {
                expenseMap.remove(id);

                Uri existingUri = ContractCitas.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();

                // Comprobar si el gasto necesita ser actualizado
                boolean b = match.Nombre != null && !match.Nombre.equals(nombre);
                boolean b2 = match.FechaFumigacion != null && !match.FechaFumigacion.equals(fechaFumigacion);
                boolean b3 = match.Precio != null && !match.Precio.equals(precio);
                boolean b4 = match.Direccion != null && !match.Direccion.equals(direccion);
                boolean b5 = match.Hora != null && !match.Hora.equals(hora);
                boolean b6 = match.FechaProxima != null && !match.FechaProxima.equals(fechaProxima);


                if (b|| b2 || b3||b4||b5||b6) {

                    Log.d(TAG, "Programando Atualizacion de:" + existingUri);
                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractCitas.Columnas.NOMBRE, match.Nombre)
                            .withValue(ContractCitas.Columnas.DIRECCION, match.Direccion)
                            .withValue(ContractCitas.Columnas.FECHA_FUMIGACION, match.FechaFumigacion)
                            .withValue(ContractCitas.Columnas.HORA, match.Hora)
                            .withValue(ContractCitas.Columnas.PRECIO, match.Precio)
                            .withValue(ContractCitas.Columnas.FECHA_PROXIMA, match.FechaProxima)


                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractCitas.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(id)).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (Citas e : expenseMap.values()) {
            //  image = convertirImgString(e.fotografia);

            Log.i(TAG, "Programando inserción de: " + e.id+e.Nombre);
            ops.add(ContentProviderOperation.newInsert(ContractCitas.CONTENT_URI)
                    .withValue(ContractCitas.Columnas.ID_REMOTA, e.id)
                    .withValue(ContractCitas.Columnas.NOMBRE, e.Nombre)
                    .withValue(ContractCitas.Columnas.DIRECCION, e.Direccion)
                    .withValue(ContractCitas.Columnas.FECHA_PROXIMA, e.FechaProxima)
                    .withValue(ContractCitas.Columnas.FECHA_FUMIGACION, e.FechaFumigacion)
                    .withValue(ContractCitas.Columnas.HORA, e.Hora)
                    .withValue(ContractCitas.Columnas.PRECIO, e.Precio)

                    .build());
            syncResult.stats.numInserts++;
        }
        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContractCitas.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                Log.d(TAG,"No se inserto");
            }
            resolver.notifyChange(
                    ContractCitas.CONTENT_URI,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
    }

    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload) {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_citas_authority), bundle);
    }

    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    public static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Crear cuenta por defecto
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.account_type));

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;

        }
        Log.i(TAG, "Cuenta de usuario obtenida.");
        return newAccount;
    }

}
