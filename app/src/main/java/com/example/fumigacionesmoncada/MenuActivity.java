package com.example.fumigacionesmoncada;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Object vision_mision;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String mUID;
    TextView nameTv, emailTv;
    ImageView avatarIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHeaderView = navigationView.getHeaderView(0);

        avatarIv =  navHeaderView.findViewById(R.id.imageView_header);
        nameTv =  navHeaderView.findViewById(R.id.name_header);
        emailTv =  navHeaderView.findViewById(R.id.email_header);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        firebaseAuth = FirebaseAuth.getInstance();
        System.out.println(firebaseAuth.getCurrentUser());
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_principal,
                R.id.nav_servicio,
                R.id.nav_acerca,
                R.id.nav_perfil,
                R.id.nav_registro_citas,
                R.id.nav_contactar)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //Set data
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


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        try {
            getMenuInflater().inflate(R.menu.menu_usuarios, menu);
        }catch (Exception e){

        }
        return true;
    }

    public void red_user(MenuItem item) {
        alertaCerrarSesionUsuario();
    }


    public void alertaCerrarSesionUsuario() {
        new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro que quieres cerrar sesión?")
                .setIcon(R.drawable.fm)
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                firebaseAuth.signOut();
                                checkUserStatus();
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

    public void vision_user(Class<VisionMision> view){
        Intent i= new Intent(this, VisionMision.class);

        startActivity(i);
    }

    public void confi_user(Class<Desarrolladores> view){
        Intent intent = new Intent(this, Desarrolladores.class);

        startActivity(intent);
    }

   @Override public boolean onOptionsItemSelected(MenuItem opcion_menu) {
       switch (opcion_menu.getItemId()) {
           case R.id.confi_user:
               Intent myIntent1 = new Intent(this,Desarrolladores.class);

               startActivity(myIntent1);
               return true;

           case R.id.vision_user:
               Intent myIntent2 = new Intent(this,VisionMision.class);

               startActivity(myIntent2);
               return true;

           default:
       }
       return super.onOptionsItemSelected(opcion_menu);
   }



    private void logout() {
        SharedPrefManager.getInstance(MenuActivity.this).clear();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed
            mUID = user.getUid();
            System.out.println(mUID);
        }else {

        }
    }
}
