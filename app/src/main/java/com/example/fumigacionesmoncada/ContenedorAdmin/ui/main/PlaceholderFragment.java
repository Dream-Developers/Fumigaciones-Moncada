package com.example.fumigacionesmoncada.ContenedorAdmin.ui.main;

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

import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda1Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda2Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda3Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda4Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda5Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda6Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda7Fragment;
import com.example.fumigacionesmoncada.ContenedorAdmin.Ayuda8Fragment;
import com.example.fumigacionesmoncada.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static Fragment newInstance(int sectionNumber) {
        Fragment fragment=null;

        switch (sectionNumber){
            case 1: fragment=new Ayuda1Fragment(); break;
            case 2: fragment=new Ayuda2Fragment(); break;
            case 3: fragment=new Ayuda3Fragment(); break;
            case 4: fragment=new Ayuda4Fragment(); break;
            case 5: fragment=new Ayuda5Fragment(); break;
            case 6: fragment=new Ayuda6Fragment(); break;
            case 7:fragment= new Ayuda7Fragment();break;
            case 8:fragment= new Ayuda8Fragment();break;

        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_contenedor_admin, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}