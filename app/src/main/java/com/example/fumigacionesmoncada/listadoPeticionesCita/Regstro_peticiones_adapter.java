package com.example.fumigacionesmoncada.listadoPeticionesCita;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.fumigacionesmoncada.R;

import java.util.ArrayList;

import static com.example.fumigacionesmoncada.R.drawable.cancelado_peticion;
import static com.example.fumigacionesmoncada.R.drawable.realizado_icon;
import static com.example.fumigacionesmoncada.R.drawable.rechazdo;


public class Regstro_peticiones_adapter extends ArrayAdapter<Citas_Peticiones> {
    private ListView lista_citas;

    private ArrayList<Citas_Peticiones> usuarios_cita;
    public Regstro_peticiones_adapter(@NonNull Context context, ArrayList<Citas_Peticiones>lista_citas) {
        super(context, R.layout.item_lista_citas_peticiones_clientes,lista_citas);
        this.usuarios_cita = lista_citas;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_citas_peticiones_clientes, null, false);


        TextView fecha = convertView.findViewById(R.id.mostrarfecha);
        TextView hora = convertView.findViewById(R.id.mostrarcita);
        TextView estado = convertView.findViewById(R.id.mosstarestado);
        TextView direccion = convertView.findViewById(R.id.mosstardireccion);
        TextView servicio = convertView.findViewById(R.id.mosstarservicio);
        ImageView logo = convertView.findViewById(R.id.icono);
        LinearLayout relativeLayout1=convertView.findViewById(R.id.relativeLayout1);


        Citas_Peticiones citas = getItem(position);


        direccion.setText(citas.getDireccion());
        fecha.setText(citas.getFecha());
        hora.setText(citas.getHora());
        servicio.setText(citas.getServicio());



        if (citas.getEstado()=="1"){
            logo.setImageResource(R.drawable.pendiente);
            estado.setText("Pendiente");}
        if (citas.getEstado()=="2"){
            logo.setImageResource(realizado_icon);
            estado.setText("Aceptado"); }
        if (citas.getEstado()=="3"){
            logo.setImageResource(rechazdo);
            estado.setText("Cancelado");
        } if (citas.getEstado()=="4"){
            logo.setImageResource(cancelado_peticion);
            estado.setText("Rechazado");
        }


        return convertView;

    }
    @Override
    public int getCount() {
        return usuarios_cita.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Citas_Peticiones getItem(int position) {
        return usuarios_cita.get(position);
    }

    public void filtrar(ArrayList<Citas_Peticiones> permisosgetYset) {
        this.usuarios_cita = new ArrayList<>();
        this.usuarios_cita.addAll(permisosgetYset);
        notifyDataSetChanged();


    }
}
