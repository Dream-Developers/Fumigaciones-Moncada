package com.example.fumigacionesmoncada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.time.LocalDate;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private EditText search_field;
    private ImageButton search_btn;
   
    private GroupAdapter adapter;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView rv = findViewById(R.id.recycler);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("User");

        search_field =  findViewById(R.id.search_field);
        search_btn = findViewById(R.id.search_btn);

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


              search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = search_field.getText().toString();

                firebaseUserSearch(searchText);

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
     * METODO PARA buscar los datos
     * */
    private void firebaseUserSearch(String searchText) {

        Toast.makeText(ContactsActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        //FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(

                //User.class,
              //  R.layout.activity_contacts,
              //  UsersViewHolder.class,
               // firebaseSearchQuery,

        };







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

}
