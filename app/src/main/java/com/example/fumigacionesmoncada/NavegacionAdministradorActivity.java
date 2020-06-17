package com.example.fumigacionesmoncada;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.fumigacionesmoncada.ContenedorAdmin.ContenedorAdminActivity;
import com.example.fumigacionesmoncada.notifications.Token;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NavegacionAdministradorActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ContentResolver resolver;
    String mUID;
    TextView nameTv, emailTv;
    ImageView avatarIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegacion_administrador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        System.out.println(firebaseAuth.getCurrentUser());
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeaderView = navigationView.getHeaderView(0);

        avatarIv =  navHeaderView.findViewById(R.id.imageView_header);
        nameTv =  navHeaderView.findViewById(R.id.name_header);
        emailTv =  navHeaderView.findViewById(R.id.email_header);
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

        //checkUserStatus();
        //updateToken(FirebaseInstanceId.getInstance().getToken());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //Set datamain
                    nameTv.setText(name);
                    emailTv.setText(email);


                    try {
                        Picasso.get().load(image).into(avatarIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.fondo_chat4).into(avatarIv);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        //menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void red_admin(MenuItem item) {
        alertaCerrarSesionAdmin();
    }

    public void mision_vision_admin(MenuItem item) {
        Intent myIntent1 = new Intent(this, VisionMision.class);
        startActivity(myIntent1);
    }

    public void desarrolladores_admin(MenuItem item) {
        Intent myIntent2 = new Intent(this, Desarrolladores.class);
        startActivity(myIntent2);
    }

    public void ayuda_admin(MenuItem item) {
        Intent myIntent3 = new Intent(this, ContenedorAdminActivity.class);
        startActivity(myIntent3);
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
                .setTitle(getString(R.string.confirmacion))
                .setMessage(getString(R.string.confirmacion2))
                .setIcon(R.drawable.fm)
                .setPositiveButton(R.string.si,
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                firebaseAuth.signOut();
                                checkUserStatus();
                                logout();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

            case R.id.action_ayuda:
                Intent myIntent3 = new Intent(this, ContenedorAdminActivity.class);

                startActivity(myIntent3);
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

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed
            mUID = user.getUid();

             //SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            // SharedPreferences.Editor editor = sp.edit();
             //editor.putString("Current_USERID", mUID);
            // editor.apply();

        }else {
            //user is no signed
            //startActivity(new Intent(NavegacionAdministradorActivity.this, LoginActivity.class));
            //finish();
        }
    }

    /**@Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }*/

    @Override
    protected void onResume() {
        //checkUserStatus();
        super.onResume();
    }


}
