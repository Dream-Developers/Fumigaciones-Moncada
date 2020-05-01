package com.example.fumigacionesmoncada.ui.PerfilAdmin;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.fumigacionesmoncada.ClaseVolley;
import com.example.fumigacionesmoncada.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilAdminFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference; //ojo aqui

    DatabaseReference dataRefe;

    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";

    ImageView avatarIv; /*, coverIv;*/
    EditText nameTv, emailTv, phoneTv, address;
    FloatingActionButton fab;
    ProgressDialog pd;

    //Permisos
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PIC_GALLERY_CODE = 300;
    private static final int IMAGE_PIC_CAMERA_CODE = 400;

    String camaraPermmitions[];
    String storagePermmitions[];

    Uri image_uri;
    public Uri imguri;

    String profileOrCoverPhoto;

    //Laravel
    JsonObjectRequest jsonObjectRequest;
    private String tokenUsuario;
    String id_usuario;
    Bitmap bitmap;
    String foto;

    public PerfilAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        //  storageReference = getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        //int arrays of permitions
        camaraPermmitions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermmitions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        dataRefe = FirebaseDatabase.getInstance().getReference().child("Datas");

        nameTv = view.findViewById(R.id.nameTvc);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        fab = view.findViewById(R.id.fab);
        avatarIv = view.findViewById(R.id.avatarIv);
        address = view.findViewById(R.id.direccionP);


        //here
        pd = new ProgressDialog(getActivity());
        cargarPreferencias();
        cargarId();


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //Set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);

                    try {
                        Picasso.get().load(image).into(avatarIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.fondo_chat4).into(avatarIv);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //BotonFlotante
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizar_datos(id_usuario);
            }
        });

        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePictureDialog();
            }
        });

        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), R.string.validacionCorreo, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


    private boolean checkStoragePermitions() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermitions(){
        requestPermissions(storagePermmitions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermitions(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);


        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    private void requestCameraPermitions(){
        requestPermissions(camaraPermmitions, CAMERA_REQUEST_CODE);
        //ActivityCompat.requestPermissions(getActivity(), camaraPermmitions, CAMERA_REQUEST_CODE);
    }

    private void showImagePictureDialog() {

        String options [] = {getString(R.string.tomarFoto), getString(R.string.elegirGaleria)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.elegiropcion);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (!checkCameraPermitions()){
                        requestCameraPermitions();
                    }else {
                        pickFromCamera();
                    }

                }else  if (which == 1){
                    if (!checkStoragePermitions()){
                        requestStoragePermitions();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //Press deny or allow

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{

                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.permisoimg), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{

                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.permiso), Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //After picking image from camera or gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PIC_GALLERY_CODE){
                //here
                image_uri = data.getData();
                avatarIv.setImageURI(image_uri);
                profileOrCoverPhoto = "image";
                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_PIC_CAMERA_CODE){
                avatarIv.setImageURI(image_uri);
                profileOrCoverPhoto = "image";
                uploadProfileCoverPhoto(image_uri);
            }
        }

         /*if (resultCode == IMAGE_PIC_GALLERY_CODE && resultCode == IMAGE_PIC_CAMERA_CODE  && resultCode==RESULT_OK && data !=null && data.getData()!=null ){
            image_uri=data.getData();
            avatarIv.setImageURI(imguri);

        }*/

        /*if (requestCode ==1 && resultCode==RESULT_OK && data !=null && data.getData()!=null ){
            imguri=data.getData();
            avatarIv.setImageURI(imguri);

        }*/
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        pd.show();

        String filePathAndName = storagePath+ ""+ profileOrCoverPhoto +"_"+ user.getUid();

        StorageReference storageReference2 = storageReference.child(filePathAndName);
        storageReference2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();


                        //chequear si ha sido actualizaada o no y el url es recibido
                        if (uriTask.isSuccessful()){
                            //image unload
                            // add update image in users firebase
                            HashMap<String, Object> results = new HashMap<>();

                            results.put(profileOrCoverPhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), getString(R.string.actualizada), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.errorimagen), Toast.LENGTH_SHORT).show();

                                }
                            });


                        }else {
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(), getString(R.string.errorimagen), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),  e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void pickFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraInten = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraInten.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraInten, IMAGE_PIC_CAMERA_CODE);
    }

    private void pickFromGallery(){
        Intent galleryInten = new Intent(Intent.ACTION_PICK);
        galleryInten.setType("image/*");
        startActivityForResult(galleryInten, IMAGE_PIC_GALLERY_CODE);
    }


    public void actualizar_datos(final String id) {

        try {
            if (nameTv.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), R.string.campoNombre, Toast.LENGTH_LONG).show();
            }else if(phoneTv.getText().toString().trim().equals("")){
                Toast.makeText(getContext(), R.string.campoTelefono, Toast.LENGTH_LONG).show();
            }else if(phoneTv.getText().toString().trim().length() < 8){
                Toast.makeText(getContext(), R.string.NumeroInvalido, Toast.LENGTH_LONG).show();
            }else if( address.getText().toString().trim().equals("")){
                Toast.makeText(getContext(), R.string.campoResidencia, Toast.LENGTH_LONG).show();
            } else {
                {

                     pd = new ProgressDialog(getContext());
                     pd.setMessage(getString(R.string.cargandoAdquirir));
                     pd.show();


                    String ip = getString(R.string.ip);
                    String url = ip + "/api/clientes/" + id + "/update";
                    JSONObject parametros = new JSONObject();
                    parametros.put("name", nameTv.getText().toString());
                    parametros.put("recidencia", address.getText().toString());
                    parametros.put("telefono", phoneTv.getText().toString());
                    if (image_uri == null) {
                        parametros.put("foto", null);
                    } else {
                        parametros.put("foto", foto);
                    }


                    jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parametros, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pd.dismiss();
                            uploadTextFirebase();
                            try {
                                Toast.makeText(getContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "" + error.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("volley", "onErrorResponse: " + error.networkResponse);
                        }
                    }) {

                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("Content-Type", "application/json");
                            parametros.put("X-Requested-With", "XMLHttpRequest");
                            parametros.put("Authorization", "Bearer" + " " + tokenUsuario);
                            return parametros;
                        }
                    };
                    ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
                }
            }
        } catch (Exception exe) {
            Toast.makeText(getContext(), exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void uploadTextFirebase() {

        String name = nameTv.getText().toString().trim();
        String phone = phoneTv.getText().toString().trim();

        //validate
        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(phone) ){
           // pd.show();
            HashMap<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("phone", phone);


            databaseReference.child(user.getUid()).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //pd.dismiss();
                            //Toast.makeText(getActivity(), "Actualizado", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //pd.dismiss();
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }else {
            Toast.makeText(getActivity(), getString(R.string.texto), Toast.LENGTH_SHORT).show();//ojo
        }
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        tokenUsuario = preferences.getString("token", "");
        id_usuario = preferences.getString("id", "");
    }

    private void cargarId() {
        String ip = getString(R.string.ip);

        String url = ip + "/api/auth/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response;
                            id_usuario = object.getString("id");
                            address.setText(object.getString("recidencia"));


                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Exception" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    Toast.makeText(getContext(), R.string.presentamosProblemas, Toast.LENGTH_LONG).show();

                } else if (error.toString().equals("com.android.volley.TimeoutError")) {

                    Toast.makeText(getContext(),R.string.reviseConexion, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),R.string.reviseConexion, Toast.LENGTH_SHORT).show();
                }
            }


        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer" + " " + tokenUsuario);


                return params;
            }
        };

        ClaseVolley.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void chechUserUserStatus(){
        FirebaseUser user = firebaseAuth.
                getCurrentUser();
        if (user != null){

        }
        else {
            //Toast.makeText(getContext(), "Nuevo", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
