package com.example.fumigacionesmoncada;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ClaseImagen {


    private String descripcion;
    private Bitmap imagen;
    private String nombre;
    private String rutaImagen;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion= descripcion;



        try {
            byte[] byteCode= Base64.decode(descripcion,Base64.DEFAULT);


            int alto=100;//alto en pixeles
            int ancho=150;//ancho en pixeles

            Bitmap foto= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagen=Bitmap.createScaledBitmap(foto,alto,ancho,true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}
