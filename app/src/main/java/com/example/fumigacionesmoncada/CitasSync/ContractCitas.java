package com.example.fumigacionesmoncada.CitasSync;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class ContractCitas {

    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY
            = "com.example.fumigacionesmoncada.CitasSync.CitasProvider";
    /**
     * Representación de la tabla a consultar
     */
    public static final String CITAS = "citas";
    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + AUTHORITY + CITAS;
    /**
     * Tipo MIME que retorna la consulta de {@link }
     */
    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + CITAS;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CITAS);
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
        uriMatcher.addURI(AUTHORITY, CITAS, ALLROWS);
        uriMatcher.addURI(AUTHORITY, CITAS + "/#", SINGLE_ROW);
    }

    // Valores para la columna ESTADO
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;


    /**
     * Estructura de la tabla
     */
    public static class Columnas implements BaseColumns {

        private Columnas() {
            // Sin instancias
        }

        public final static String NOMBRE = "Nombre";
        public final static String DIRECCION = "Direccion";
        public final static String PRECIO = "Precio";
        public final static String FECHA_FUMIGACION = "FechaFumigacion";
        public final static String FECHA_PROXIMA = "FechaProxima";
        public final static String HORA = "Hora";
        public final static String ID_USUARIO = "id_usuario";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }
}
