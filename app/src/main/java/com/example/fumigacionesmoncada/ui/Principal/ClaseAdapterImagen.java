package com.example.fumigacionesmoncada.ui.Principal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;

import java.util.List;


import androidx.recyclerview.widget.RecyclerView;

public class ClaseAdapterImagen extends
        RecyclerView.Adapter<ClaseAdapterImagen.UsuariosHolder>
{


    List<ServiciosVO>  listaUsuarios;
    Context context;


    public ClaseAdapterImagen(List<ServiciosVO> listaimegen, Context context) {
        this. listaUsuarios = listaimegen;
        this.context = context;
    }


    @Override
    public UsuariosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_imagenes,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
                return new UsuariosHolder(vista);
    }



    @Override
    public void onBindViewHolder(UsuariosHolder holder, int position) {
        holder.txtDocumento.setText( listaUsuarios.get(position).getDescripcion());

        if ( listaUsuarios.get(position).getRutaImagen()!=null){
            cargarImagenWebService( listaUsuarios.get(position).getRutaImagen(),holder);

        }else{
            holder.imagen.setImageResource(R.drawable.logo);

        }

    }

    private void cargarImagenWebService(String rutaImagen, final UsuariosHolder holder) {

        String ip=context.getString(R.string.ip);

        String urlImagen=ip+"/imagen/"+rutaImagen;
        urlImagen=urlImagen.replace(" ","%20");

        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.imagen.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error al cargar la imagen",Toast.LENGTH_SHORT).show();
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();


            }
        });
        //request.add(imageRequest);
        ClaseVolley.getIntanciaVolley(context).addToRequestQueue(imageRequest);



    }

    @Override
    public int getItemCount() {
        return  listaUsuarios.size();
    }





    public class UsuariosHolder extends RecyclerView.ViewHolder {

        TextView txtDocumento;
        ImageView imagen;

        public UsuariosHolder(View itemView) {
            super(itemView);
            txtDocumento =  itemView.findViewById(R.id.idDocumento);
            imagen = itemView.findViewById(R.id.idImagen);
        }

    }
}
