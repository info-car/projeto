package com.example.infocar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

public class FrontPage extends AppCompatActivity {
    public static final String TAG = "TAG";
    TextView nomeCarro;
    String nome;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ImageView carImage;
    Button  mudarImagem, perfilButton, pneu, revisao, viagem, adicionaCarro, custo, logOut;
    StorageReference storageReference;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //VARIAVEIS PARA ACEDER AOS BOTÕES
        mudarImagem = findViewById(R.id.changeImage);
        perfilButton = findViewById(R.id.perfil);
        carImage = findViewById(R.id.carImage);
        pneu = findViewById(R.id.pneusInfo);
        revisao = findViewById(R.id.revisaoInfo);
        viagem = findViewById(R.id.viagens);
        logOut = findViewById(R.id.logOut);
        list = findViewById(R.id.listaCarros);
        adicionaCarro = findViewById(R.id.adicionaCarro);
        nomeCarro = findViewById(R.id.nomeCarro);
        custo = findViewById(R.id.custo);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        DataHolder app = new DataHolder();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("CarInfo");
            nomeCarro.setText(value);
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Intent intent = new Intent(FrontPage.this, Login.class);
                app.setData(null);
                nomeCarro.setText("O TEU AUTOMÓVEL");
                pneu.setClickable(false);
                revisao.setClickable(false);
                custo.setClickable(false);
                mudarImagem.setClickable(false);
                viagem.setClickable(false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LOGIN", null);
                editor.apply();
                startActivity(intent);
                finish();
            }
        });

        if(app.getData() != null){
            nome = app.getData();
            nomeCarro.setVisibility(View.VISIBLE);
            nomeCarro.setText(nome);
            pneu.setClickable(true);
            revisao.setClickable(true);
            custo.setClickable(true);
            mudarImagem.setClickable(true);
            viagem.setClickable(true);
            Log.v("TAG", "CARRO QUE AQUI CHEGOU FOI O" + nome);

            //VAI BUSCAR IMAGEM DO CARRO A FIREBASE
            StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "CarInfo/" + nome + "/carImage.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Picasso.get().load(uri).into(carImage);
                    Log.v("TAG", "IMAGEM DO CARRO CARREGADA DA FIREBASE COM SUCESSO");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("TAG", "ERRO A CARREGAR IMAGEM DO CARRO DA FIREBASE COM SUCESSO");
                }
            });

            custo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), TotalCusto.class));
                    Log.v("TAG", "CARREGUEI NO CUSTO");
                }
            });

            // ESCOLHE UMA IMAGEM DA GALERIA E GUARA-A NA FIREBASE
            mudarImagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open Gallery
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, 1000);
                    if(app.getData() != null){
                        nome = app.getData();
                        nomeCarro.setText(nome);
                        StorageReference imageProfileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "CarInfo/" + nome + "/carImage.jpg");
                        imageProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               // Picasso.get().load(uri).into(carImage);
                                Log.v("TAG", "IMAGEM CARREGADA E GUARDADA NA FIREBASE COM SUCESSO");
                            }
                        });
                    }
                }
            });
            // CUSTOS VIAGEM
            viagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), ListaViagens.class));
                    Log.v("TAG", "CARREGUEI NA GASOLINA");
                }
            });
            // BUTAO PARA ACEDER AOS PNEUS
            pneu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Pneu.class));
                    Log.v("TAG", "VOU ADICIONAR UM PNEU");

                }
            });
            // BUTAO PARA ACEDER A REVISAO
            revisao.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.v("TAG", "asbsbsa");
                    startActivity(new Intent(getApplicationContext(), Revisao.class));
                }
            });
        }else{
            Log.v("TAG", "NAO CHEGOU NENHUM CARRO AINDA");
        }
        adicionaCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), adicionarCarro.class));
                Log.v("TAG", "VOU ADICIONAR UM CARRO");
            }
        });
        // BUTAO PARA ACEDER AO PERFIL
        perfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
                Log.v("TAG", "CARREGUEI NO PERFIL");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //uploadImageToFirebase(imageUri);
            }
        }
    }

    /*
    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to FireBase storage
        DataHolder app = new DataHolder();
        if(app.getData() != null) {
            nome = app.getData();
            nomeCarro.setText(nome);
            StorageReference fileReference = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "CarInfo/" + nome + "/carImage.jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(carImage);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
     */

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}