package com.example.fumigacionesmoncada.ui.citas;

public class Citas {
    public int id;
    public String idRemta;
    public String Nombre;
    public String Direccion;
    public String Precio;
    public String FechaFumigacion;

    public String FechaProxima;
    public String Hora;
    public String id_usuario;

    public String getFechaProxima() {
        return FechaProxima;
    }

    public void setFechaProxima(String fechaProxima) {
        this.FechaProxima = fechaProxima;
    }


    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getIdRemta() {
        return idRemta;
    }

    public void setIdRemta(String idRemta) {
        this.idRemta = idRemta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        this.Direccion = direccion;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        this.Precio = precio;
    }

    public String getFechaFumigacion() {
        return FechaFumigacion;
    }

    public void setFechaFumigacion(String fechaFumigacion) {
        this.FechaFumigacion = fechaFumigacion;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        this.Hora = hora;
    }
}
