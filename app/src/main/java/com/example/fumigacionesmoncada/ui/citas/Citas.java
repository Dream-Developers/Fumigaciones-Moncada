package com.example.fumigacionesmoncada.ui.citas;

public class Citas {
    public int id;
    public String idRemta;
    public String nombre;
    public String direccion;
    public String precio;
    public String fecha;

    public String getFecha_proxima() {
        return fecha_proxima;
    }

    public void setFecha_proxima(String fecha_proxima) {
        this.fecha_proxima = fecha_proxima;
    }

    public String fecha_proxima;
    public String hora;
    public String id_usuario;

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
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
