package com.example.fumigacionesmoncada.factura;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fumigacionesmoncada.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Factura_Fragment extends Fragment {
    private FloatingActionButton addCliente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_factura, container, false);
        addCliente = view.findViewById(R.id.add_clientes);

        addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),crearFactura.class);
                startActivity(intent);
            }
        });
        return view;
    }



}
