package com.example.fumigacionesmoncada.ui.contactar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fumigacionesmoncada.ChatActivity;
import com.example.fumigacionesmoncada.ChatsRecent;
import com.example.fumigacionesmoncada.LlamadaActivity;
import com.example.fumigacionesmoncada.Mensaje;
import com.example.fumigacionesmoncada.Notification;
import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class ContactarFragment extends Fragment  implements Application.ActivityLifecycleCallbacks{

    /*private GroupAdapter adapter;
    private Button face, tel, inst, web;
    private String urlface;
    private String urlinst;
    private String urlweb;
     String number = "32391344";*/
    private GroupAdapter adapter;
    private User user; //= new User();
    private EditText editChat;
    private User yo;

    FirebaseUser firebaseUser;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactar_, container, false);

        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.fondo2) ;


        ActionBar actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.fondo_chat2) ;

        user = new User(getActivity().getIntent().getStringExtra("uuid"),
                getActivity().getIntent().getStringExtra("name"),
                getActivity().getIntent().getStringExtra("photo"));
        actionBar.setTitle(user.getUsername());



        RecyclerView rv = view.findViewById(R.id.recycler_chat);
        editChat = view.findViewById(R.id.edit_chat);
        Button btnChat = view.findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enviarMensaje();

            }
        });

        adapter = new GroupAdapter<>();
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        yo = documentSnapshot.toObject(User.class);
                        //searchMensajes();
                    }
                });

        /*((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        RecyclerView rv = view.findViewById(R.id.recycler_contactar);

        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        face = view.findViewById(R.id.btn_facebook);
        tel = view.findViewById(R.id.btn_telefono);
        inst = view.findViewById(R.id.btn_instagram);
        web = view.findViewById(R.id.btn_web);


        urlface ="https://www.facebook.com/pages/category/Community-Service/Fumigaciones-Moncada-y-Rapimandados-Danl%C3%AD-103556841016903/";
        urlinst = "https://www.instagram.com/";
        urlweb = "http://moncadasfumigacionesdanli.blogspot.com/";


        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Aun no funciona", Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse(urlface);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /**if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!=
                        PackageManager.PERMISSION_GRANTED)
                        return;*/

               // Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                //startActivity(intent);*/
                /*Intent intent = new Intent(getActivity(), ChatActivity.class);

                //UserItem userItem = (UserItem) item;
                //intent.putExtra("user", userItem.user);


               // intent.putExtra("uuid", ChatsRecent.contactos.getUuid());
                //intent.putExtra("name", ChatsRecent.contactos.getUsername());
                //intent.putExtra("photo", ChatsRecent.contactos.getPhotoUrl());
                startActivity(intent);



            }
        });


        inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlinst);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlweb);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        adapter = new GroupAdapter();
        rv.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                //Toast.makeText(getContext(), "Gracias por darle click, aun no funciona", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                ContactoItem contactoItem = (ContactoItem) item;

                intent.putExtra("uuid", contactoItem.contactos.getUuid());
                intent.putExtra("name", contactoItem.contactos.getUsername());
                intent.putExtra("photo", contactoItem.contactos.getPhotoUrl());

                startActivity(intent);

            }
        });

        searchLastMensaje(); */
        return view;

    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null){
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        }
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

    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

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


    /** private void searchMensajes() {
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

   /** private void enviarMensaje() {
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
                    //.whereEqualTo("uuid", "MFYjO3kZhfRj49pzC3GE9Q9AmlC3")
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


    /*private void searchLastMensaje() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;


        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contacts").whereEqualTo("username", "Jacob")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                        if (documentChanges != null) {
                            for (DocumentChange doc: documentChanges) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    ChatsRecent contact = doc.getDocument().toObject(ChatsRecent.class);

                                    adapter.add(new ContactoItem(contact));
                                }
                            }
                        }
                    }
                });


    }

    public  class ContactoItem extends Item<ViewHolder> {

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
            return R.layout.item_contactar_admin;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }*/
}
