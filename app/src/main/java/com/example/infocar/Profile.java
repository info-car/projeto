package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023;
    public static final String TAG = "TAG";
    TextView fullName, email, phone, nomeCarro;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button  resetPassLocal, changeProfileImage, remove;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;
    ListView lista;
    private static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("TAG", "ESTOU NA PÁGINA INICIAL");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullName = (TextView) findViewById(R.id.NomePerfil);
        email = (TextView) findViewById(R.id.EmailPerfil);
        phone = (TextView )findViewById(R.id.TelePerfil);
        resetPassLocal = findViewById(R.id.ResetPassword);
        profileImage = findViewById(R.id.profileImage);
        remove = findViewById(R.id.remove);
        changeProfileImage = findViewById(R.id.changeProfile);
        lista = (ListView) findViewById(R.id.listaCarros);
        nomeCarro = findViewById(R.id.nomeCarro);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter arrayAdapter = new ArrayAdapter(Profile.this, android.R.layout.simple_list_item_1, arrayList);
        DataHolder app = new DataHolder();


        //CARREGA FOTO DE PERFIL
        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Picasso.get().load(uri).into(profileImage);
                Log.v("TAG", "CARREGUEI FOTO DE PERFIL");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("TAG", "ERRO A CARREGAR FOTO DE PERFIL OU NÃO HÁ FOTO DE PERFIL");
            }
        });

        //CARREGA CARROS NA LISTA
        DatabaseReference Ref = database.getReference().child("Users").child(userId).child("CarInfo");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("TAG", "CarInfo da FIREBASE DESTE USER:");
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,"-" + childDataSnapshot.getKey()); //displays the key for the node
                    if(!childDataSnapshot.getKey().equals("Totais")){
                        arrayList.add(childDataSnapshot.getKey());
                    }
                }
                lista.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("TAG", "ERRO A OBTER DADOS DO CarInfo DA FIREBASE");
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nome = arrayList.get(position);
                Log.v(TAG,"Selecionei da lista -> " + nome);
                Intent FrontPage = new Intent(getApplicationContext(), FrontPage.class);
                DataHolder app = new DataHolder();
                app.setData(nome);
                startActivity(FrontPage);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"CARREGUEI NO REMOVE CARRO");
                startActivity(new Intent(getApplicationContext(), RemoverCarro.class));
            }
        });

        DatabaseReference myRef = database.getReference().child("Users").child(userId).child("UserInfo");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String Nome = dataSnapshot.child("Full Name").getValue(String.class);
                Log.d(TAG, "Value is: " + Nome);
                String telemovel = dataSnapshot.child("Phone").getValue(String.class);
                Log.d(TAG, "Value is: " + telemovel);
                String EMAIL = dataSnapshot.child("Email").getValue(String.class);
                Log.d(TAG, "Value is: " + EMAIL);

                fullName.setText(Nome);
                phone.setText(telemovel);
                email.setText(EMAIL);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Mudar Password ?");
                passwordResetDialog.setMessage("Introduza a nova Password com mais de 6 caracteres");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Password Alterada!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Erro a alterar a Password !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close Dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

               // uploadImageToFirebase(imageUri);
            }
        }
    }
    /*
    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to FireBase storage
        StorageReference fileReference = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

     */
}