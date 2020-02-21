package com.example.fumigacionesmoncada;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fumigacionesmoncada.ui.contactar.ContactarFragment;

public class ContactarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactar_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ContactarFragment.newInstance())
                    .commitNow();
        }
    }
}
