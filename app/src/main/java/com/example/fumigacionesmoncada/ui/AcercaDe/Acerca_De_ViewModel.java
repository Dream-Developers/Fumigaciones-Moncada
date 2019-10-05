package com.example.fumigacionesmoncada.ui.AcercaDe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Acerca_De_ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Acerca_De_ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is send fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}