package com.example.fumigacionesmoncada.ui.citas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fumigacionesmoncada.R;
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;

import java.util.ArrayList;

public class Citas_Adapter extends ArrayAdapter<Citas> {


    public Citas_Adapter(@NonNull Context context, ArrayList<Citas>lista_citas) {
        super(context, R.layout.item_lista_citas,lista_citas);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_citas,null,false);

        TextView nombre = convertView.findViewById(R.id.nombre_cita);
        TextView direccion = convertView.findViewById(R.id.direccion_cita);
        TextView precio = convertView.findViewById(R.id.precio_cita);
        TextView fecha = convertView.findViewById(R.id.fecha_cita);
        TextView hora = convertView.findViewById(R.id.hora_cita);
        Citas citas = getItem(position);

        nombre.setText(citas.getNombre());
        direccion.setText(citas.getDireccion());
        precio.setText(citas.getPrecio());
        fecha.setText(citas.getFecha());
        hora.setText(citas.getHora());


        return  convertView;

    }
}
