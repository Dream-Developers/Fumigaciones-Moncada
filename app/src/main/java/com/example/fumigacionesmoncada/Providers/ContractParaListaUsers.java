package com.example.fumigacionesmoncada.Providers;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *  Esta clase provee las constantes y URIs necesarias para trabajar con el StudentsProvider
 */

public class ContractParaListaUsers {
    public final static String AUTHORITY = "com.example.fumigacionesmoncada.provider";
    public static final String USERS = "users";
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + USERS);


        /**
            MIME Types
            Para listas se necesita  'vnd.android.cursor.dir/vnd.com.example.fumigacionesmoncada.provider.usuarios'
            Para items se necesita 'vnd.android.cursor.item/vnd.com.example.fumigacionesmoncada.provider.usuarios'
            La primera parte viene esta definida en constantes de ContentResolver
         */

        public final static String MULTIPLE_MIME =
                "vnd.android.cursor.dir/vnd." + AUTHORITY + USERS;


        public final static String SINGLE_MIME =
                "vnd.android.cursor.item/vnd." + AUTHORITY + USERS;

    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS = 1;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW = 2;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, USERS, ALLROWS);
        uriMatcher.addURI(AUTHORITY, USERS + "/#", SINGLE_ROW);
    }

    public ContractParaListaUsers() {
    }

    /**
     * Estructura de la tabla (podria ser distinta)
     */
    public static class Columnas implements BaseColumns {
        private Columnas() {
        }

        public static  final String _ID = BaseColumns._ID;
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String ROL = "rol";
    }
}