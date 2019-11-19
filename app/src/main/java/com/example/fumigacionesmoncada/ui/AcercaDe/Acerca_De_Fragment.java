package com.example.fumigacionesmoncada.ui.AcercaDe;

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

public class Acerca_De_Fragment extends Fragment {

    private Acerca_De_ViewModel acercaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        acercaViewModel =
                ViewModelProviders.of(this).get(Acerca_De_ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_acerca_de, container, false);
       // final TextView textView = root.findViewById(R.id.acerca);
        acercaViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
             //   textView.setText(s);
            }
        });
        return root;
    }
}