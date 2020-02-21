package com.example.fumigacionesmoncada;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


public class Contactar_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GroupAdapter adapter;
    private Button face, tel, inst, web;
    private String urlface;
    private String urlinst;
    private String urlweb;


    public Contactar_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Contactar_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Contactar_Fragment newInstance(String param1, String param2) {
        Contactar_Fragment fragment = new Contactar_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                Intent intent = new Intent(getActivity(), LlamadaActivity.class);
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
