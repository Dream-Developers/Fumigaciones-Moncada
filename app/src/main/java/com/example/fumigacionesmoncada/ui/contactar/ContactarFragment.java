package com.example.fumigacionesmoncada.ui.contactar;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fumigacionesmoncada.R;

public class ContactarFragment extends Fragment {

    private ContactarViewModel mViewModel;

    public static ContactarFragment newInstance() {
        return new ContactarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contactar_fragment, container, false);
    }



}
