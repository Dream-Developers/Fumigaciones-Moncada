package com.example.fumigacionesmoncada.ui.Mensajes;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fumigacionesmoncada.Adapters.AdapterChatlist;
import com.example.fumigacionesmoncada.ChatActivity;
import com.example.fumigacionesmoncada.ChatApplication;
import com.example.fumigacionesmoncada.ChatsRecent;
import com.example.fumigacionesmoncada.CitasSync.ContractCitas;
import com.example.fumigacionesmoncada.ContactsActivity;
import com.example.fumigacionesmoncada.Models.ModelChat;
import com.example.fumigacionesmoncada.Models.ModelChatlist;
import com.example.fumigacionesmoncada.Models.ModelUsers;
import com.example.fumigacionesmoncada.NavegacionAdministradorActivity;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.SplashActivity;
import com.example.fumigacionesmoncada.User;
import com.example.fumigacionesmoncada.UsersActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.OnItemLongClickListener;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MensajesFragment extends Fragment  implements Application.ActivityLifecycleCallbacks {

    private FloatingActionButton add_chat;
    private GroupAdapter adapter;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUsers> usersList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        //RecyclerView rv = view.findViewById(R.id.recycler_contact);
        //rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycler_contact);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**getActivity().getApplication().registerActivityLifecycleCallbacks(this);
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

               // Toast.makeText(getContext(), "Gracias por darle click, aun no funciona", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                ContactoItem contactoItem = (ContactoItem) item;

                intent.putExtra("uuid", contactoItem.contactos.getUuid());
                intent.putExtra("name", contactoItem.contactos.getUsername());
                intent.putExtra("photo", contactoItem.contactos.getPhotoUrl());

                startActivity(intent);
            }
        });


        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull Item item, @NonNull View view) {
                Toast.makeText(getContext(), "Gracias por darle click, ", Toast.LENGTH_LONG).show();

                return false;
            }
        })*/







        add_chat = view.findViewById(R.id.add_chat);

        add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UsersActivity.class);
                startActivity(intent);
            }
        });


        //updateToken();
        //searchLastMensaje();

        Cursor cursor= obtenerRegistrosFecha();

        if(cursor!=null&&cursor.moveToNext()){
            //Toast.makeText(getContext(), "Si hay registros de hoy", Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(getContext(), "No hay", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void loadChats() {
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUsers users = ds.getValue(ModelUsers.class);
                    for (ModelChatlist chatlist : chatlistList){
                        if (users.getUid() != null && users.getUid().equals(chatlist.getId())){
                            usersList.add(users);
                            break;
                        }
                    }
                    //adapter
                    adapterChatlist = new AdapterChatlist(getContext(), usersList);
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for (int i =0; i<usersList.size(); i++){
                        lastMessage(usersList.get(i).getUid());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver==null){
                        continue;
                    }
                    //pucha mano
                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }

                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Cursor obtenerRegistrosFecha(){
        String fecha_hora = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Uri uri = ContractCitas.CONTENT_URI;
        String selection = ContractCitas.Columnas.FECHA_FUMIGACION+"=?";
        String[] selectionArgas = new String[]{fecha_hora};

        return getContext().getContentResolver().query(uri, null, selection, selectionArgas, null);

    }
    /**private void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .update("token", token);
        }
    }*/

    /**private void searchLastMensaje() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;


        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = null;
                        if (queryDocumentSnapshots != null) {
                            documentChanges = queryDocumentSnapshots.getDocumentChanges();


                            if (documentChanges.size() > 0) {
                                for (DocumentChange doc : documentChanges) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        ChatsRecent contact = doc.getDocument().toObject(ChatsRecent.class);

                                        adapter.add(new ContactoItem(contact));
                                    }
                                }
                            }
                        }
                    }
                });


    }*/

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
       // setOnline(true);
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        //setOnline(false);
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {

    }


    /**private void setOnline(boolean enabled) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .update("online", enabled);
        }

    }

    public  class ContactoItem extends Item<ViewHolder>{

        private final ChatsRecent contactos;

        public ContactoItem(ChatsRecent contactos) {
            this.contactos = contactos;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView contactname = viewHolder.itemView.findViewById(R.id.textView);
            TextView mensaje = viewHolder.itemView.findViewById(R.id.textView2);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);


            contactname.setText(contactos.getUsername());
            mensaje.setText(contactos.getLastMessage());
            Picasso.get()
                    .load(contactos.getPhotoUrl())
                    .into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_contactos_mensajes;
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        chechUserUserStatus();
    }

    private void chechUserUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            //mUID = user.getUid();

        }else {
            //startActivity(new Intent(getActivity(), SplashActivity.class));
            //getActivity().finish();
        }

    }

}