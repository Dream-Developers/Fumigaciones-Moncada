package com.example.fumigacionesmoncada.Database;


import android.content.Context;
import android.database.Cursor;

public final class CrudBaseDatos {

    private static DatabaseHelper baseDatos;


    private static CrudBaseDatos instancia = new CrudBaseDatos();
    private Cursor cursor;

    private CrudBaseDatos() {
    }

    public static CrudBaseDatos obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new DatabaseHelper(contexto);
        }
        return instancia;
    }

}
