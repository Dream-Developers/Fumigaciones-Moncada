package com.example.fumigacionesmoncada;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.fumigacionesmoncada.Adapters.AdapterUsers;
import com.example.fumigacionesmoncada.Models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> userList;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        //getActionBar().setTitle("Test");
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.users_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        //add
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //init user view
        userList = new ArrayList<>();
        getAllUsers();

    }

    private void getAllUsers() {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);


                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        userList.add(modelUser);
                    }

                    adapterUsers = new AdapterUsers(UsersActivity.this, userList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void searchUsers(final String query) {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);


                    if (!modelUser.getUid().equals(fUser.getUid())) {

                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase()))
                        {
                            userList.add(modelUser);
                        }

                    }

                    adapterUsers = new AdapterUsers(UsersActivity.this, userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_activity, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);


        //Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }
                else {
                    getAllUsers();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }else {
                    getAllUsers();
                }

                return false;
            }
        });


        return true;
    }

    public void red_admin(MenuItem item) {
        alertaCerrarSesionAdmin();
    }

    public void alertaCerrarSesionAdmin() {
        new AlertDialog.Builder(UsersActivity.this)
                .setTitle(getString(R.string.confirmacion))
                .setMessage(getString(R.string.cerrarsesion))
                .setIcon(R.drawable.logofm)
                .setPositiveButton(getString(R.string.si),
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                firebaseAuth.signOut();
                                checkUserStatus();
                                logout();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
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
        SharedPrefManager.getInstance(UsersActivity.this).clear();
        Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed
        }else {
            //user is no signed
        }
    }

}