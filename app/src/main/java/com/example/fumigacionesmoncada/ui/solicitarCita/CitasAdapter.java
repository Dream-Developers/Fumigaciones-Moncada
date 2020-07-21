package com.example.fumigacionesmoncada.ui.solicitarCita;

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

public class CitasAdapter extends ArrayAdapter<CitaVO> {
        private ArrayList<CitaVO> cita;

    public CitasAdapter(Context context, ArrayList<CitaVO>lista) {
        super(context, R.layout.item_lista_cita_peticion ,lista);
        this.cita = lista;
    }


    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_cita_peticion,parent,false);


        TextView nombre = convertView.findViewById(R.id.nombre_cita);
        CitaVO citaVO = getItem(position);

        nombre.setText(citaVO.getNombre());


        return  convertView;
    }


    @Override
    public int getCount() {
        return cita.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public CitaVO getItem(int position) {
        return cita.get(position);
    }

    public void filtrar(ArrayList<CitaVO> permisosgetYset) {
        this.cita = new ArrayList<>();
        this.cita.addAll(permisosgetYset);
        notifyDataSetChanged();


    }


}


