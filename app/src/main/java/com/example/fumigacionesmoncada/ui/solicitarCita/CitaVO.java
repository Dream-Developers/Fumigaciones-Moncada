package com.example.fumigacionesmoncada.ui.solicitarCita;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public abstract class CitaVO {
    private String id, nombre,residencia,telefono,fecha;

    public CitaVO(Context context, int item_lista_citas, ArrayList<CitaVO> lista) {
    }

    public CitaVO() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getResidencia() {
        return residencia;
    }

    public void setResidencia(String residencia) {
        this.residencia = residencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String correo) {
        this.fecha = correo;
    }

    @NonNull
    public abstract View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent);

    public abstract int getCount();

    public abstract long getItemId(int position);

    @Nullable
    public abstract CitaVO getItem(int position);
}


