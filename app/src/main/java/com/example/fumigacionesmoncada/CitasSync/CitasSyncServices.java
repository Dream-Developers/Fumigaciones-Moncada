package com.example.fumigacionesmoncada.CitasSync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CitasSyncServices extends Service {
    // Instancia del sync adapter
    private static CitasSyncAdapter syncAdapter = null;
    // Objeto para prevenir errores entre hilos
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (syncAdapter == null) {
                syncAdapter = new CitasSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Retorna interfaz de comunicación para que el sistema llame al sync adapter
     */
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
