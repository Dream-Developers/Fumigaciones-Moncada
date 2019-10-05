package com.example.fumigacionesmoncada;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ClaseVolley {

    private static ClaseVolley intanciaVolley;
    private RequestQueue request;
    private static Context contexto;

    private ClaseVolley(Context context) {
        contexto = context;
        request = getRequestQueue();
    }


    public static synchronized ClaseVolley getIntanciaVolley(Context context) {
        if (intanciaVolley == null) {
            intanciaVolley = new ClaseVolley(context);
        }

        return intanciaVolley;
    }

    public RequestQueue getRequestQueue() {
        if (request == null) {
            request = Volley.newRequestQueue(contexto.getApplicationContext());
        }

        return request;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }


}
