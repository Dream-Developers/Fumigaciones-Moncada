package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fumigacionesmoncada.R;

public class Aquirir_Servicio_Fragment extends Fragment {

    private Aquirir_Servicio_ViewModel servicioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicioViewModel =
                ViewModelProviders.of(this).get(Aquirir_Servicio_ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ad_servicio, container, false);
        final TextView textView = root.findViewById(R.id.servicio);
        servicioViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}