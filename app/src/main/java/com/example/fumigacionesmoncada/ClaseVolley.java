package com.example.fumigacionesmoncada;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ClaseVolley {

    private static ClaseVolley intanciaVolley;
    private RequestQueue request;
    private static Context contexto;
    private ImageLoader mImageLoader;


    private ClaseVolley(Context context) {
        contexto = context;
        request = getRequestQueue();


        mImageLoader = new ImageLoader(request,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

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

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
