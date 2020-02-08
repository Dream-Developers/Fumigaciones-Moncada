package com.example.fumigacionesmoncada.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fumigacionesmoncada.CitasSync.ContractCitas;
import com.example.fumigacionesmoncada.Providers.ContractParaListaUsers;
import com.example.fumigacionesmoncada.Utils;

import okhttp3.internal.Util;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context contexto;

    public DatabaseHelper(Context context,
                          String name,
                          SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context contexto) {
        super(contexto, Utils.DB_NAME, null, Utils.DB_VERSION);
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
                ContractParaListaUsers.Columnas.ROL + " TEXT); ";
        db.execSQL(cmd);
        createTableCitas(db);
    }

    private void createTableCitas(SQLiteDatabase db) {
        String cmd = "CREATE TABLE " + ContractCitas.CITAS + " (" +
                ContractCitas.Columnas._ID + " INTEGER PRIMARY KEY, " +
                ContractCitas.Columnas.NOMBRE + " TEXT, " +
                ContractCitas.Columnas.DIRECCION + " TEXT , " +
                ContractCitas.Columnas.FECHA_FUMIGACION + " TEXT, " +
                ContractCitas.Columnas.PRECIO + " TEXT, " +
                ContractCitas.Columnas.FECHA_PROXIMA + " TEXT, " +
                ContractCitas.Columnas.HORA + " TEXT, " +
                ContractCitas.Columnas.ID_USUARIO+" TEXT ,"+

                ContractCitas.Columnas.ID_REMOTA + " TEXT UNIQUE," +
                ContractCitas.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT " + ContractCitas.ESTADO_OK + "," +
                ContractCitas.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        ;
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table " + ContractParaListaUsers.USERS);
            db.execSQL("drop table " + ContractCitas.CITAS);

        } catch (SQLiteException e) {
        }
        onCreate(db);
    }
}
