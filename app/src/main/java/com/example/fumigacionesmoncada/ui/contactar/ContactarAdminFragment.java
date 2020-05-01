package com.example.fumigacionesmoncada.ui.contactar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fumigacionesmoncada.Adapters.AdapterContactar;
import com.example.fumigacionesmoncada.AdminPerfilActivity;
import com.example.fumigacionesmoncada.Models.ModelChat;
import com.example.fumigacionesmoncada.Models.ModelUsers;
import com.example.fumigacionesmoncada.R;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactarAdminFragment extends Fragment {
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
    //AdapterChat adapterChat;
    AdapterContactar adapterChat;


    String myUid;
    String hisUid;
    String hisImage;
    String number = "89217523";

    FirebaseUser fUser;

    ModelUsers users;

    private RequestQueue requestQueue;
    private boolean notity = false;

    public ContactarAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contactar_admin, container, false);


        ActionBar actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.fondo_chat2);
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.fondo_chat3) ;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.menu_options_chats);

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        profileIv = view.findViewById(R.id.proifleIv);
        nameTv = view.findViewById(R.id.nameTv);
        userStatusTv = view.findViewById(R.id.userStatusTv);
        messageEt = view.findViewById(R.id.messageEt);
        sendBtn = view.findViewById(R.id.sendBtn);

        recyclerView = view.findViewById(R.id.chat_recyclerview2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference();

        Query userQuery = usersDbRef.child("Users").orderByChild("uid").equalTo("rIRg1DDkEIcsqbtY9WMp2cD2Nrq2");
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //myUid = user.getUid();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String name =""+ ds.child("name").getValue();
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

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_call:
                        // Do onlick on menu action here
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notity = true;
                String message = messageEt.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    Toast.makeText(getContext(), getString(R.string.mensajevacio), Toast.LENGTH_SHORT).show();
                }else {
                    //tex listo
                    sendMessage(message);

                }
                //reset edittex
                messageEt.setText("");
            }

        });

        profileIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminPerfilActivity.class);
                startActivity(intent);
            }

        });

        nameTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminPerfilActivity.class);
                startActivity(intent);
            }

        });


        userStatusTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdminPerfilActivity.class);
                startActivity(intent);
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



        readMessage();
        seenMessage();


        return view;
    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestap = String.valueOf(System.currentTimeMillis());

        //String hisUid = users.getUid();

        //fUser = FirebaseAuth.getInstance().getCurrentUser();
        //hisUid = fUser.getUid();
        hisUid = "rIRg1DDkEIcsqbtY9WMp2cD2Nrq2";
        System.out.println(hisUid);


        FirebaseUser user = firebaseAuth.getCurrentUser();
        myUid = user.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);//here
        hashMap.put("timestamp", timestap);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);

        //messageEt.setText("");

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


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
                    adapterChat = new AdapterContactar(getContext(), chatList, hisImage);
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


    @Override
    public void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        currentUser("None");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        chechUserUserStatus();
        checkOnlineStatus("online");
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOnlineStatus("online");
        currentUser(hisUid);
    }

    private void currentUser(String hisUid){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SP_USER", MODE_PRIVATE).edit();
        editor.putString("Current_USERID", hisUid);
        editor.apply();
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

    private void chechUserUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            myUid = user.getUid();
        }
        else{

        }
    }



}
