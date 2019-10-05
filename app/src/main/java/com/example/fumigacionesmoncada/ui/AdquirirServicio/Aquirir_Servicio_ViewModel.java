package com.example.fumigacionesmoncada.ui.AdquirirServicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Aquirir_Servicio_ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Aquirir_Servicio_ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}