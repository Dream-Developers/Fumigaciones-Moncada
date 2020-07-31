package com.example.fumigacionesmoncada;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.xwray.groupie.GroupAdapter;

public class VisionMision extends AppCompatActivity {

TextView mision,descripcionmision, vision,descripcionvision;
    private GroupAdapter adapter;
    private Button face, tel, inst, web;
    private String urlface;
    private String urlinst;
    private String urlweb;
    String number = "99480610";

    private int mLineY;
    private int mViewWidth;

   public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
       setContentView(R.layout.vision);
     mision = findViewById(R.id.mision);
     vision = findViewById(R.id.vision);
     descripcionmision = findViewById(R.id.descripcionmision);
     descripcionvision = findViewById(R.id.descripcionvision);



       vision= findViewById(R.id.vision);
       mision= findViewById(R.id.mision);
       face = findViewById(R.id.btn_facebook);
       tel = findViewById(R.id.btn_telefono);
       inst = findViewById(R.id.btn_instagram);
       web = findViewById(R.id.btn_web);

       vision.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               vision.setMaxLines(Integer.MAX_VALUE);
           }
       });
       mision.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mision.setMaxLines(Integer.MAX_VALUE);
           }
       });
       urlface ="https://www.facebook.com/pages/category/Community-Service/Fumigaciones-Moncada-y-Rapimandados-Danl%C3%AD-103556841016903/";
       urlinst = "https://www.instagram.com/";
       urlweb = "http://moncadasfumigacionesdanli.blogspot.com/";


       face.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //Toast.makeText(getContext(), "Aun no funciona", Toast.LENGTH_LONG).show();
               Uri uri = Uri.parse(urlface);
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);
           }
       });


       tel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               /**if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)!=
                PackageManager.PERMISSION_GRANTED)
                return;*/

               Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
               startActivity(intent);
           }
       });


       inst.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri uri = Uri.parse(urlinst);
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);
           }
       });


       web.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri uri = Uri.parse(urlweb);
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);
           }
       });

    }


}
