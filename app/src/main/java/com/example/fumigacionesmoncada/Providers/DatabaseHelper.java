package com.example.fumigacionesmoncada.Providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "clientesdatos.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        createTableEmpleados(database);
    }

    private void createTableEmpleados(SQLiteDatabase db) {
        String cmd = "CREATE TABLE " + ContractParaListaUsers.USERS + " (" +
                ContractParaListaUsers.Columnas._ID + " INTEGER PRIMARY KEY, " +
                ContractParaListaUsers.Columnas.EMAIL + " TEXT UNIQUE, " +
                ContractParaListaUsers.Columnas.PASSWORD + " TEXT, " +
                ContractParaListaUsers.Columnas.ROL+ " TEXT); ";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
