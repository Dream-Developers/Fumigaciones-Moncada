package com.example.fumigacionesmoncada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user = new User();
    private EditText editChat;
    private User yo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = new User(getIntent().getStringExtra("uuid"),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("photo"));
        getSupportActionBar().setTitle(user.getUsername());


        RecyclerView rv = findViewById(R.id.recycler_chat);
        editChat = findViewById(R.id.edit_chat);
        Button btnChat = findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();

            }
        });

        adapter = new GroupAdapter<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        yo = documentSnapshot.toObject(User.class);
                        searchMensajes();
                    }
                });


    }

    private void searchMensajes() {
        if (yo != null) {

            String fromId = yo.getUuid();
            String toId = user.getUuid();

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestap", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                            if (documentChanges != null) {
                                for (DocumentChange doc: documentChanges) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Mensaje mensaje = doc.getDocument().toObject(Mensaje.class);
                                        adapter.add(new MensajeItem(mensaje));
                                    }
                                }
                            }
                        }
                    });

        }

    }

    private void enviarMensaje() {
        String text = editChat.getText().toString();

        editChat.setText(null);

        final String fromId = FirebaseAuth.getInstance().getUid();
        final String toId = user.getUuid();
        long timestamp = System.currentTimeMillis();

        final Mensaje mensaje = new Mensaje();
        mensaje.setFromID(fromId);
        mensaje.setToID(toId);
        mensaje.setTimestap(timestamp);
        mensaje.setText(text);


        if (!mensaje.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(mensaje)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());

                            ChatsRecent contact = new ChatsRecent();
                            contact.setUuid(toId);
                            contact.setUsername(user.getUsername());
                            contact.setPhotoUrl(user.getProfileUrl());
                            contact.setTimestamp(mensaje.getTimestap());
                            contact.setLastMessage(mensaje.getText());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);

                        }
                    });


            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(mensaje)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());


                            ChatsRecent contact = new ChatsRecent();
                            contact.setUuid(toId);
                            contact.setUsername(user.getUsername());
                            contact.setPhotoUrl(user.getProfileUrl());
                            contact.setTimestamp(mensaje.getTimestap());
                            contact.setLastMessage(mensaje.getText());


                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contact);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);

                        }
                    });

        }


    }

    private class MensajeItem extends Item<ViewHolder>{

        private final Mensaje mensaje;

        //Constructor
        private MensajeItem(Mensaje mensaje) {
            this.mensaje = mensaje;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView  txtMsg = viewHolder.itemView.findViewById(R.id.txt_msg);
            ImageView imgMensaje = viewHolder.itemView.findViewById(R.id.img_message_user);
            txtMsg.setText(mensaje.getText());

            Picasso.get()
                    .load(mensaje.getFromID().equals(FirebaseAuth.getInstance().getUid())
                            ? yo.getProfileUrl()
                            : user.getProfileUrl())
                    .into(imgMensaje);
        }

        @Override
        public int getLayout() {
            return mensaje.getFromID().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.item_to_mensajes
                    : R.layout.item_from_mensaje;
        }
    }
}
