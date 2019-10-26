package com.example.fumigacionesmoncada.Providers;

import android.provider.BaseColumns;

public class ContractParaListaUsers {
    public static final String USERS = "users";


    public ContractParaListaUsers() {
    }

    /**
     * Estructura de la tabla
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