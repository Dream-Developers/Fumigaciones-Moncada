package com.example.fumigacionesmoncada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;


import java.util.List;



public class ContactsActivity extends AppCompatActivity {

   
    private GroupAdapter adapter;

    private DatabaseReference mUserDatabase;
    EditText search_users;
    private String buscador;
    private static final String TAG = "ContactsActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView rv = findViewById(R.id.recycler);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("User");
       /* searchView = findViewById(R.id.searchView);

        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchContacts(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/


        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));



        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);

                UserItem userItem = (UserItem) item;
                intent.putExtra("uuid", userItem.user.getUuid());
                intent.putExtra("name", userItem.user.getUsername());
                intent.putExtra("photo", userItem.user.getProfileUrl());

                startActivity(intent);
            }
        });

        SearchUsers();



    }

    /**
     * BUSCADOR DE CONTACTOS
     * */
    private void searchContacts(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("User")
                .orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getUuid().equals(fuser.getUid())){
                        adapter.add((Group) user);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * METODO PARA IR A FIREBASE A BUSCAR LOS USUARIOS
     * */
    private void SearchUsers() {
        FirebaseFirestore.getInstance().collection("/users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.e("Teste", e.getMessage(), e);
                            return;
                        }

                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                            adapter.clear();

                        for (DocumentSnapshot doc: docs) {
                            User user = doc.toObject(User.class);
                            String uid = FirebaseAuth.getInstance().getUid();
                            if (user.getUuid().equals(uid))
                                continue;

                            Log.d("Teste", user.getUsername());


                            adapter.add(new UserItem(user));
                        }
                    }
                });
    }






    /**
     * METODO PARA AGREGAR LOS DATOS A CADA ITEM DE LA CLASE
     * */
    private class UserItem extends Item<ViewHolder> {

        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            Log.d("Teste", position + "");
            TextView txtUsername = viewHolder.itemView.findViewById(R.id.textView);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);

            txtUsername.setText(user.getUsername());

            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_contactos;
        }

    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_search_contactos, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) ContactsActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchContacts(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchContacts(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }*/




    // 2. Retrieve your value (which you have done mostly correctly)
    private void getUserName(String uid) {
        mUserDatabase.child(String.format("users/%s/name", uid))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 3. Set the public variable in your class equal to value retrieved
                        buscador = dataSnapshot.getValue(String.class);
                        // 4a. EDIT: now that your fetch succeeded, you can trigger whatever else you need to run in your class that uses `buscador`, and you can be sure `buscador` is not null.
                        sayHiToMe();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    // (part of step 4a)
    public void sayHiToMe() {
        Log.d(TAG, "hi there, " + buscador);
    }

    // 4b. use the variable in a function triggered by the onClickListener of a button.
    public void helloButtonWasPressed() {
        if (buscador != null) {
            Log.d(TAG, "hi there, " + buscador);
        }
    }


    private void firebasesearch(String searchText){

        // String quary = new searchText.toLowerCase(quary);
        //Query firebaseSearchQuery = mUserDatabase.orderByChild("search").startAt(quary).endAt(quary + "uf8ff");


    }





}
