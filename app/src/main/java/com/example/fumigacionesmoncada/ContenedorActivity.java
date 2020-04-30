package com.example.fumigacionesmoncada;

import android.net.Uri;
import android.os.Bundle;

import com.example.fumigacionesmoncada.Contenedor.InstructionHelpFragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp2Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp3Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp4Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp5Fragment;
import com.example.fumigacionesmoncada.Contenedor.IntructionHelp6Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fumigacionesmoncada.ui.main.SectionsPagerAdapter;

public class ContenedorActivity extends AppCompatActivity implements
        InstructionHelpFragment.OnFragmentInteractionListener,
        IntructionHelp2Fragment.OnFragmentInteractionListener,
        IntructionHelp3Fragment.OnFragmentInteractionListener,
        IntructionHelp4Fragment.OnFragmentInteractionListener,
        IntructionHelp5Fragment.OnFragmentInteractionListener,
        IntructionHelp6Fragment.OnFragmentInteractionListener {


    private LinearLayout linearPuntos;
    private TextView[] puntosSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        linearPuntos = findViewById(R.id.linearPuntos);
        agregaIndicadorPuntos(0);
        viewPager.addOnPageChangeListener(viewListener);
    }

    private void agregaIndicadorPuntos(int position) {
        puntosSlide =new TextView[6];
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}