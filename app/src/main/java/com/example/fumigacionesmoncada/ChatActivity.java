package com.example.fumigacionesmoncada;

/**import androidx.annotation.NonNull;
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
import android.widget.Toast;

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

import java.util.List;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.Adapters.AdapterChat;
import com.example.fumigacionesmoncada.Models.ModelChat;
import com.example.fumigacionesmoncada.Models.ModelUsers;
import com.example.fumigacionesmoncada.notifications.Data;
import com.example.fumigacionesmoncada.notifications.Sender;
import com.example.fumigacionesmoncada.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    /**private GroupAdapter adapter;
    private User user; //= new User();
    private EditText editChat;
    private User yo;*/

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView userStatusTv, nameTv;
    EditText messageEt;
    ImageButton sendBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;


    String hisUid;
    String myUid;
    String hisImage;

    //APIService apiService;
    private RequestQueue requestQueue;
    private boolean notity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.chat_recyclerview);
        profileIv = findViewById(R.id.proifleIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //prodiedades del recycler
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //String keys =ds.getKey();
                    String name =""+ ds.child("name").getValue();
                    //String image =""+ ds.child("image").getValue();
                     hisImage =""+ ds.child("image").getValue();
                     String typingStatus =""+ ds.child("typingTo").getValue();
                     String onlineStatus =""+ ds.child("onlineStatus").getValue();

                    //estado de typing
                    if (typingStatus.equals(myUid)){
                        userStatusTv.setText(getString(R.string.escribiendo));
                    }
                    else {

                        if (onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }
                        else {
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            userStatusTv.setText(String.format("%s%s", getString(R.string.ultimavez), dateTime));


                        }

                    }
                    //setData
                    nameTv.setText(name);


                    //  System.out.println("key" + keys);
                    try {//load(hisImage)
                        //Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_tool).into(profileIv); //.placeholder(R.drawable.ic_default_img)
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_contactos_white).into(profileIv); //.placeholder(R.drawable.ic_default_img)
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_contactos_white).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.d(TAG, "Esta onda no funciona " + databaseError.toException());
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notity = true;
                String message = messageEt.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, getString(R.string.mensajevacio), Toast.LENGTH_SHORT).show();
                }else {
                    //tex listo
                    sendMessage(message);

                }
                //reset edittex
                messageEt.setText("");
            }

        });


        //cambios en el editext
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /**getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                String msg = editChat.getText().toString();
                if (!msg.equals("")){
                    enviarMensaje();
                } else {
                    Toast.makeText(ChatActivity.this, "No puedes enviar un mensaje vacío", Toast.LENGTH_SHORT).show();
                }
                editChat.setText("");
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
                });*/

            readMessage();
            seenMessage();
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance()
                .getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hashSeenHashMap = new HashMap<>();
                        hashSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hashSeenHashMap);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //aqui
    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat != null) {
                        if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                                chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)) {
                            chatList.add(chat);
                        }
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter al rv
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestap = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);//here
        hashMap.put("timestamp", timestap);
        hashMap.put("isSeen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        //clean
       // messageEt.setText("");


        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUsers user = dataSnapshot.getValue(ModelUsers.class);

                if (notity){
                    sendNotification(hisUid, user.getName(), message);
                }
                notity = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
         .child(myUid)
         .child(hisUid);


         chatRef1.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()){
        chatRef1.child("id").setValue(hisUid);
        }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });

         final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
         .child(hisUid)
         .child(myUid);


         chatRef2.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()){
        chatRef2.child("id").setValue(myUid);
        }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
    }


    private void sendNotification(final String hisUid, final String name, final String message) {

        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name + ": " + message, getString(R.string.nuevomsj), hisUid, R.drawable.logofm);

                    Sender sender = new Sender(data, token.getToken());

                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAjn3kHDM:APA91bE_2vcyrAI3vUtOIlRtgoimcstUD5NYy92b2sbLy3m4vREtkZIkaXvG1Wdni7sCY6TlJJi0zULtnoK4YwiXu8hl8Oi-GEygHdAOqk372PUthIotnM7iTmg8JMyqCRnA0Uy5uW94");

                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    /**apiService.sendNotification(sender)
                     .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                    });*/
                    //de aqui empieza 2 parte
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   private void chechUserUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            myUid = user.getUid();
        }else {

        }

    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbRef.updateChildren(hashMap);

    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbRef.updateChildren(hashMap);

    }

    private void currentUser(String hisUid){
        SharedPreferences.Editor editor = getSharedPreferences("SP_USER", MODE_PRIVATE).edit();
        editor.putString("Current_USERID", hisUid);
        editor.apply();
    }


    @Override
    protected void onStart() {
        chechUserUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        currentUser("None");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
        currentUser(hisUid);
    }

    /**private void searchMensajes() {
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

    }*/

    /**private void enviarMensaje() {
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


                            if (!user.isOnline()) {
                                Notification notification = new Notification();
                                notification.setFromID(mensaje.getFromID());
                                notification.setToID(mensaje.getToID());
                                notification.setTimestap(mensaje.getTimestap());
                                notification.setText(mensaje.getText());
                                notification.setFromName(yo.getUsername());

                                FirebaseFirestore.getInstance().collection("/notifications")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .set(notification);
                            }



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


    }*/

    /**private class MensajeItem extends Item<ViewHolder>{

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
    }*/
}
