package com.example.fumigacionesmoncada.ui.contactar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fumigacionesmoncada.ChatActivity;
import com.example.fumigacionesmoncada.ChatsRecent;
import com.example.fumigacionesmoncada.LlamadaActivity;
import com.example.fumigacionesmoncada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class ContactarFragment extends Fragment {

    private GroupAdapter adapter;
    private Button face, tel, inst, web;
    private String urlface;
    private String urlinst;
    private String urlweb;
     String number = "32391344";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactar_, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

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

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
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

        searchLastMensaje();
        return view;

    }


    private void searchLastMensaje() {

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


}
