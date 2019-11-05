package com.example.fumigacionesmoncada;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;


public class RegistarUsuarioNuevo extends AppCompatActivity implements
        Response.Listener<JSONObject>, Response.ErrorListener{
    EditText nombre,contraseña,correo,confcontra, telefono , apellidos;
    TextView col;
    Button registrar;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ProgressBar pro;

    //Firebase
    private Button mBtnSelectedPhoto;
    private ImageView mImgPhoto;
    private Uri mSelectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario_nuevo);
        confcontra =  findViewById(R.id.comprobacion_contraseña);
        nombre =  findViewById(R.id.registro_nombres);
        correo = findViewById(R.id.registro_correo);
        telefono = findViewById(R.id.registro_telefono);
        apellidos = findViewById(R.id.registro_apellidos);
        contraseña = findViewById(R.id.registro_contraseña);
        registrar =  findViewById(R.id.registrar);
        request = Volley.newRequestQueue(this);


        mBtnSelectedPhoto = findViewById(R.id.btn_selected_photo);
        mImgPhoto = findViewById(R.id.img_photo);

        mBtnSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarwebservice();
                createUser();
            }
        });


        nombre.setSingleLine(false);
        nombre.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        correo.setSingleLine(false);
        correo.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        apellidos.setSingleLine(false);
        apellidos.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        telefono.setSingleLine(false);
        telefono.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);

    }
    public void cargarwebservice(){

        try {
            if(nombre.getText().toString().equals("")||correo.getText().toString().equals("")||confcontra.getText().toString().equals("")||contraseña.getText().toString().equals("")
                    || telefono.getText().toString().equals("") || apellidos.getText().toString().equals("")){
                Toast.makeText(this,"Al menos un campo vacio, todos los campos son obligatorio, Por favor Completelo",Toast.LENGTH_LONG).show();
            }else {
                if (contraseña.getText().toString().length() < 8 || confcontra.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "La contraseñia no debe ser menor a ocho caracteres", Toast.LENGTH_LONG).show();
                } else {
                    if (telefono.getText().toString().length() < 8 ) {
                        Toast.makeText(getApplicationContext(), "No es un numero Telefonico", Toast.LENGTH_LONG).show();
                    } else {
                        if (contraseña.getText().toString().equals(confcontra.getText().toString())) {
                            if (!validarEmail(correo.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Correo no valido", Toast.LENGTH_LONG).show();
                            } else {

                                progreso = new ProgressDialog(this);
                                progreso.setMessage("Cargando...");
                                progreso.show();
                                String ip = getString(R.string.ip);
                                String url = ip + "/api/auth/signup?name=" + nombre.getText().toString()
                                        + "&recidencia=" + apellidos.getText().toString() + "&telefono="
                                        + telefono.getText().toString() + "&email=" + correo.getText().toString() + "&password=" + contraseña.getText().toString() + "&password_confirmation=" + confcontra.getText().toString();
                                url = url.replace(" ", "%20");
                                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this);
                                request.add(jsonObjectRequest);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "nueva password con confirmar password no coinciden", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }catch (Exception exe){
            Toast.makeText(this,exe.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

        @Override
        public void onErrorResponse (VolleyError error){
            progreso.hide();
            if (error.toString().equals("com.android.volley.ServerError")) {
                Toast.makeText(getApplicationContext(), "Presentamos problemas intentelo mas tarde.", Toast.LENGTH_LONG).show();

            } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                Toast.makeText(getApplicationContext(), "Revise su conexión a internet", Toast.LENGTH_LONG).show();
            } else {
            }
          //  Toast.makeText(getApplicationContext(),"No se pudo registrar , Hubo un error al conectar por favor verifica la conexión a internet o intente nuevamente o su correo ya esta registrado , Error : "+ error.toString(), Toast.LENGTH_LONG).show();

           // Log.i("ERROR", error.toString());
        }

        @Override
        public void onResponse (JSONObject response){

            Toast.makeText(this,"Se registrado correctamente", Toast.LENGTH_SHORT).show();
            progreso.hide();
            nombre.setText("");
            correo.setText("");
            confcontra.setText("");
            contraseña.setText("");
            telefono.setText("");
            apellidos.setText("");
        }


    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    /**
     * Metodos para la authentificacion de firebase
     *
     * **/




    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            mSelectedUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mImgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                mBtnSelectedPhoto.setAlpha(0);
            } catch (IOException e) {
            }
        }

    }

    private void createUser() {
        String name = nombre.getText().toString();
        String email = correo.getText().toString();
        String contra = contraseña.getText().toString();

        if (name == null || name.isEmpty() || email == null || email.isEmpty() || contra == null || contra.isEmpty()) {
            Toast.makeText(this, "Nombre y contrasena requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contra)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Teste", task.getResult().getUser().getUid());

                            saveUserInFirebase();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste", e.getMessage());
                    }
                });
    }

    private void saveUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Teste", uri.toString());

                                String uid = FirebaseAuth.getInstance().getUid();
                                String username = correo.getText().toString();
                                String profileUrl = uri.toString();

                                User user = new User(uid, username, profileUrl);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(RegistarUsuarioNuevo.this, MainActivity.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Teste", e.getMessage());
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Teste", e.getMessage(), e);
                    }
                });

    }


}

