package com.example.fumigacionesmoncada;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fumigacionesmoncada.CitasSync.CitasSyncAdapter;
import com.example.fumigacionesmoncada.CitasSync.ContractCitas;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NavegacionAdministradorActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ContentResolver resolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegacion_administrador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chat,
                R.id.nav_clientes,
                R.id.nav_imagen,
                R.id.nav_serviciosadministrador,
                R.id.nav_citas,
                R.id.nav_solicitarCita,
                R.id.nav_acercad,
                R.id.nav_factura)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        resolver=getContentResolver();

        CitasSyncAdapter.inicializarSyncAdapter(this);
        CitasSyncAdapter.obtenerCuentaASincronizar(this);
        CitasSyncAdapter.sincronizarAhora(this,false);

        Cursor cursor= obtenerRegistrosFecha();
        if(cursor.moveToFirst()){
            Log.i("principal","Si hay registros");

        }else{
            Log.i("principal","no hay registros");
            Toast.makeText(this, "No hay", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void red_admin(MenuItem item) {
        //logout();
        alertaCerrarSesionAdmin();
    }

    public Cursor obtenerRegistrosFecha(){
        String fecha_hora = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Toast.makeText(this, ""+fecha_hora, Toast.LENGTH_SHORT).show();
        Uri uri = ContractCitas.CONTENT_URI;
        String selection = ContractCitas.Columnas.FECHA_FUMIGACION+"=?";
        String[] selectionArgas = new String[]{fecha_hora};

        return resolver.query(uri, null, selection, selectionArgas, null);

    }

    public void alertaCerrarSesionAdmin() {
        new AlertDialog.Builder(NavegacionAdministradorActivity.this)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro que quieres cerrar sesión?")
                .setIcon(R.drawable.logofm)
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                logout();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void vision_admin(Class<VisionMision> view){
        Intent i= new Intent(this, VisionMision.class);

        startActivity(i);
    }

    public void confi_admin(Class<Desarrolladores> view){
        Intent intent = new Intent(this, Desarrolladores.class);

        startActivity(intent);
    }

    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        switch (opcion_menu.getItemId()) {
            case R.id.visionau:
                Intent myIntent1 = new Intent(this,VisionMision.class);

                startActivity(myIntent1);
                return true;

            case R.id.confinau:
                Intent myIntent2 = new Intent(this,Desarrolladores.class);

                startActivity(myIntent2);
                return true;

            default:
        }
        return super.onOptionsItemSelected(opcion_menu);
    }

    private void logout() {
        SharedPrefManager.getInstance(NavegacionAdministradorActivity.this).clear();
        Intent intent = new Intent(NavegacionAdministradorActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
