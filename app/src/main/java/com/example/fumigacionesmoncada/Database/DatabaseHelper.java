package com.example.fumigacionesmoncada.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fumigacionesmoncada.Providers.ContractParaListaUsers;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context contexto;
    public DatabaseHelper(Context context,
                          String name,
                          SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context contexto) {
        super(contexto, "usuarios", null, 1);
        this.contexto = contexto;
    }

    public void onCreate(SQLiteDatabase database) {
        createTableUsuarios(database);
    }

    private void createTableUsuarios(SQLiteDatabase db) {
        String cmd = "CREATE TABLE " + ContractParaListaUsers.USERS + " (" +
                ContractParaListaUsers.Columnas._ID + " INTEGER PRIMARY KEY, " +
                ContractParaListaUsers.Columnas.NAME + " TEXT, " +
                ContractParaListaUsers.Columnas.EMAIL + " TEXT UNIQUE, " +
                ContractParaListaUsers.Columnas.PASSWORD + " TEXT, " +
                ContractParaListaUsers.Columnas.ROL+ " TEXT); ";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try { db.execSQL("drop table " + ContractParaListaUsers.USERS);
        }
        catch (SQLiteException e) { }
        onCreate(db);
    }
}
