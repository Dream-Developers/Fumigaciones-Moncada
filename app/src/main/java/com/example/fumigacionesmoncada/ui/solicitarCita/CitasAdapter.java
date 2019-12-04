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
import com.example.fumigacionesmoncada.ui.clientes.ClientesVO;

import java.util.ArrayList;

public class CitasAdapter extends ArrayAdapter<CitaVO> {
        private ArrayList<CitaVO> cita;

    public CitasAdapter(Context context, ArrayList<CitaVO>lista) {
        super(context, R.layout.item_lista_cita_peticion ,lista);
        this.cita = lista;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_cita_peticion,parent,false);


        TextView nombre = convertView.findViewById(R.id.nombre_cita);
        TextView fecha = convertView.findViewById(R.id.fecha_cita);
        TextView  direccion = convertView.findViewById(R.id.direccion_cita);
        TextView txt_servicio = convertView.findViewById(R.id.txt_servicio);
        TextView hora = convertView.findViewById(R.id.hora_cita);
        CitaVO citaVO = getItem(position);

        nombre.setText(citaVO.getNombre());
        fecha.setText(citaVO.getFecha());
        direccion.setText(citaVO.getDireccion());
        hora.setText(citaVO.getHora());
        txt_servicio.setText(citaVO.getTxt_servicio());



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


