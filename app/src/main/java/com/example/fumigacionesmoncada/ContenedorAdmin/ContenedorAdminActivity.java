package com.example.fumigacionesmoncada.ContenedorAdmin;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fumigacionesmoncada.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fumigacionesmoncada.ContenedorAdmin.ui.main.SectionsPagerAdapter;

public class ContenedorAdminActivity extends AppCompatActivity implements
        Ayuda1Fragment.OnFragmentInteractionListener,
        Ayuda2Fragment.OnFragmentInteractionListener,
        Ayuda3Fragment.OnFragmentInteractionListener,
        Ayuda4Fragment.OnFragmentInteractionListener,
        Ayuda5Fragment.OnFragmentInteractionListener,
        Ayuda6Fragment.OnFragmentInteractionListener,
        Ayuda7Fragment.OnFragmentInteractionListener,
        Ayuda8Fragment.OnFragmentInteractionListener {

    private LinearLayout linearPuntos;
    private TextView[] puntosSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_admin);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        linearPuntos = findViewById(R.id.linearPuntos);
        agregaIndicadorPuntos(0);
        viewPager.addOnPageChangeListener(viewListener);

    }

    private void agregaIndicadorPuntos(int position) {
        puntosSlide =new TextView[8];
        linearPuntos.removeAllViews();

        for (int i=0; i< puntosSlide.length; i++){
            puntosSlide[i]=new TextView(this);
            puntosSlide[i].setText(Html.fromHtml("&#8226;"));
            puntosSlide[i].setTextSize(35);
            puntosSlide[i].setTextColor(getResources().getColor(R.color.colorBlancoTransparente));
            linearPuntos.addView(puntosSlide[i]);
        }

        if(puntosSlide.length>0){
            puntosSlide[position].setTextColor(getResources().getColor(R.color.colorBlanco));
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            agregaIndicadorPuntos(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}