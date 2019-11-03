package com.example.fumigacionesmoncada;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {


    private static final String STRING_PREFERENCES = "example.preferencias";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
