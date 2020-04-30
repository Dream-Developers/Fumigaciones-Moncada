package com.example.fumigacionesmoncada.ui.main;

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

import com.example.fumigacionesmoncada.Contenedor.InstructionHelpFragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp2Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp3Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp4Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp5Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp6Fragment;
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
            case 1: fragment=new InstructionHelpFragment(); break;
            case 2: fragment=new IntructionHelp2Fragment(); break;
            case 3: fragment=new IntructionHelp3Fragment(); break;
            case 4: fragment=new IntructionHelp4Fragment(); break;
            case 5: fragment=new IntructionHelp6Fragment(); break;
            case 6: fragment=new IntructionHelp5Fragment(); break;
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
        View root = inflater.inflate(R.layout.fragment_contenedor, container, false);
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