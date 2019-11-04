package com.example.fumigacionesmoncada.ui.Mensajes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fumigacionesmoncada.ContactsActivity;
import com.example.fumigacionesmoncada.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MensajesFragment extends Fragment {

    private FloatingActionButton add_chat;
    ListView lista_chats;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);
        add_chat = view.findViewById(R.id.add_chat);


        add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}