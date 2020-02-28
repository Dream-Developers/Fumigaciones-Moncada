package com.example.fumigacionesmoncada.ui.citas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fumigacionesmoncada.R;

import java.util.ArrayList;

public class Citas_Adapter extends ArrayAdapter<Citas> {

    private ListView lista_citas;

    private ArrayList<Citas> usuarios;
    public Citas_Adapter(@NonNull Context context, ArrayList<Citas>lista_citas) {
        super(context, R.layout.item_lista_citas,lista_citas);
        this.usuarios = lista_citas;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_citas, null, false);

        TextView nombre = convertView.findViewById(R.id.nombre_cita);
        TextView precio = convertView.findViewById(R.id.precio_cita);


        TextView fecha = convertView.findViewById(R.id.fecha_cita);
        TextView hora = convertView.findViewById(R.id.hora_cita);
       
        Citas citas = getItem(position);


        nombre.setText(citas.getNombre());

        precio.setText(citas.getPrecio());
        fecha.setText(citas.getFechaFumigacion());
        hora.setText(citas.getHora());


        return convertView;

    }
    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Citas getItem(int position) {
        return usuarios.get(position);
    }

    public void filtrar(ArrayList<Citas> permisosgetYset) {
        this.usuarios = new ArrayList<>();
        this.usuarios.addAll(permisosgetYset);
        notifyDataSetChanged();


    }

}
