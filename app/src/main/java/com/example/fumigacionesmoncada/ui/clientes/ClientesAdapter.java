package com.example.fumigacionesmoncada.ui.clientes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fumigacionesmoncada.R;

import java.util.ArrayList;

public class ClientesAdapter extends ArrayAdapter<ClientesVO> {

    public ClientesAdapter( Context context,  ArrayList<ClientesVO>lista) {
        super(context, R.layout.item_lista_clientes ,lista);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_clientes,null,false);

        TextView nombre = convertView.findViewById(R.id.nombre_cliente);
        TextView telefono = convertView.findViewById(R.id.telefono_cliente);
        ClientesVO clientesVO = getItem(position);

        nombre.setText(clientesVO.getNombre());
        telefono.setText(clientesVO.getTelefono());


        return  convertView;
    }
}
