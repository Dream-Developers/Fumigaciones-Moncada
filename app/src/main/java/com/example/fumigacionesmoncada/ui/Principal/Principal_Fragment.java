package com.example.fumigacionesmoncada.ui.Principal;

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

public class Principal_Fragment extends Fragment {

    private Principal_ViewModel principalViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        principalViewModel =
                ViewModelProviders.of(this).get(Principal_ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_principal, container, false);
        final TextView textView = root.findViewById(R.id.principal);
        principalViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}