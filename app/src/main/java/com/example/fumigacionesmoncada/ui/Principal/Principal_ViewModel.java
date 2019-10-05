package com.example.fumigacionesmoncada.ui.Principal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Principal_ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Principal_ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}