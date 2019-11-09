package com.example.fumigacionesmoncada.ui.Principal;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClaseAdapterImagenDetalle extends    ArrayAdapter<ServiciosVO> {


    public ClaseAdapterImagenDetalle( Context context, List<ServiciosVO> objects) {
        super(context, R.layout.item_iamgen_detalle, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_iamgen_detalle, null, false);

        TextView titulo = convertView.findViewById(R.id.tituloImagenD);
        TextView descripcion = convertView.findViewById(R.id.descripcionImagenD);

        NetworkImageView imagen = convertView.findViewById(R.id.idImagenD);

        ServiciosVO serviciosVO = getItem(position);
        titulo.setText(serviciosVO.getTitulo());
        descripcion.setText(serviciosVO.getDescripcion());

        String ip = getContext().getResources().getString(R.string.ip);
        String url = ip + "/foto/" + serviciosVO.getRutaImagen();
        ImageLoader imageLoader = ClaseVolley.getIntanciaVolley(getContext()).getImageLoader();
        imagen.setImageUrl(url, imageLoader);


        return convertView;
    }
}
