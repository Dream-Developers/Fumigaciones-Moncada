package com.example.fumigacionesmoncada.Providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.fumigacionesmoncada.Database.DatabaseHelper;

import static com.example.fumigacionesmoncada.Providers.ContractParaListaUsers.ALLROWS;
import static com.example.fumigacionesmoncada.Providers.ContractParaListaUsers.SINGLE_ROW;

public class UsersProvider extends ContentProvider {

    private static final String DATABASE_NAME = "usuarios";

    private static final int DATABASE_VERSION = 8;

    /**
     * Instancia global del Content Resolver
     */
    private ContentResolver resolver;
    /**
     * Instancia del administrador de la base de datos
     */
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        // Inicializando gestor BD
        databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        resolver = getContext().getContentResolver();
        return true;
    }


    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        // Obtener base de datos
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // Comparar Uri
        int match = ContractParaListaUsers.uriMatcher.match(uri);

        Cursor c =null;

        switch (match) {
            case ALLROWS:
                // Consultando todos los registros

                try {
                    c = db.query(ContractParaListaUsers.USERS, projection,
                            selection, selectionArgs,
                            null, null, sortOrder);
                    c.setNotificationUri(
                            resolver,
                            ContractParaListaUsers.CONTENT_URI);
                }catch (Exception e){

                }

                break;
            case SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long idGasto = ContentUris.parseId(uri);
                c = db.query(ContractParaListaUsers.USERS, projection,
                        ContractParaListaUsers.Columnas._ID + " = " + idGasto,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContractParaListaUsers.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (ContractParaListaUsers.uriMatcher.match(uri)) {
            case ALLROWS:
                return ContractParaListaUsers.MULTIPLE_MIME;
            case SINGLE_ROW:
                return ContractParaListaUsers.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de gasto desconocido: " + uri);
                //return null;
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validar la uri
        if (ContractParaListaUsers.uriMatcher.match(uri) != ALLROWS) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        long rowId = db.insert(ContractParaListaUsers.USERS, null, contentValues);
        if (rowId > 0) {
            Uri uri_gasto = ContentUris.withAppendedId(
                    ContractParaListaUsers.CONTENT_URI, rowId);
            resolver.notifyChange(uri_gasto, null, false);
            return uri_gasto;
        }
        throw new SQLException("Falla al insertar fila en : " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int affected = 0;
        int match = ContractParaListaUsers.uriMatcher.match(uri);

        switch (match) {

                // Se borran todas las filas
            case ContractParaListaUsers.ALLROWS:
                affected = db.delete(ContractParaListaUsers.USERS,
                        null,
                        null);
                break;
            case ContractParaListaUsers.SINGLE_ROW:
                affected = db.delete(ContractParaListaUsers.USERS, selection, selectionArgs);
        }
        // Se retorna el numero de filas eliminadas
        return affected;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // No se implemento un update
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
