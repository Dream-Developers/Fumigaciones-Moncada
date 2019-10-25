package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fumigacionesmoncada.R;

public class Aquirir_Servicio_Fragment extends Fragment {

    private Aquirir_Servicio_ViewModel servicioViewModel;
    private EditText mostrarNombre,mostrarDireccion,mostraraTelefono;
    private EditText fecha, Hora;
    String tokenUsuario;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicioViewModel =
                ViewModelProviders.of(this).get(Aquirir_Servicio_ViewModel.class);
        View view = inflater.inflate(R.layout.fragment_ad_servicio, container, false);


cargarPreferencias();
        mostrarNombre = (EditText)view.findViewById(R.id.nombres);
        mostrarDireccion = (EditText)view.findViewById(R.id.direccion);
        mostraraTelefono = (EditText)view.findViewById(R.id.telefono);
        Hora = (EditText)view.findViewById(R.id.hora);
        fecha = (EditText)view.findViewById(R.id.fecha);
        return view;
    }
    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
         tokenUsuario = preferences.getString("token", "");

    }


}