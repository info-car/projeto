package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovaViagem extends AppCompatActivity  implements adicionarViagem.DespesasListener {
    public static final String TAG = "TAG";
    EditText origem, destino;
    ListView listaDespesas;
    Button adicionarDespesa, save, acabar;
    String userId;
    FirebaseAuth fAuth;
    String Origem, Destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_viagem);

        listaDespesas = findViewById(R.id.NovaViagem);
        adicionarDespesa = findViewById(R.id.adicionarDespesa);
        origem = findViewById(R.id.origem);
        destino = findViewById(R.id.destino);
        acabar = findViewById(R.id.acabar);
        save = findViewById(R.id.save);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DataHolder app = new DataHolder();
        String nome = app.getData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Origem = origem.getText().toString().toUpperCase();
                if(Origem.isEmpty()){
                    origem.setError("Preencha esta campo!");
                    return;
                }
                Origem.replace("."," ").replace("#", " ").replace("$", " ").replace("[", " ").replace("]", " ");

                Destino = destino.getText().toString().toUpperCase();
                if(Destino.isEmpty()){
                    destino.setError("Preencha este campo!");
                    return;
                }
                Destino.replace("."," ").replace("#", " ").replace("$", " ").replace("[", " ").replace("]", " ");

                app.setOrigem(Origem);
                app.setDestino(Destino);
                save.setVisibility(View.INVISIBLE);
                origem.setEnabled(false);
                origem.setClickable(false);
                destino.setEnabled(false);
                destino.setClickable(false);
                destino.setClickable(false);
                adicionarDespesa.setEnabled(true);
                adicionarDespesa.setClickable(true);
                if(Origem!=null && Destino != null) {
                    DatabaseReference ref = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Viagens").child(Origem + "-" + Destino);
                    Map utilizador = new HashMap<>();
                    utilizador.put("Total Viagem", 0.0);
                    ref.setValue(utilizador);
                }
                adicionarDespesa.setVisibility(View.VISIBLE);
                acabar.setVisibility(View.VISIBLE);
            }
        });
        acabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder msgM = new AlertDialog.Builder(NovaViagem.this);
                msgM.setTitle("ATENÇÃO!");
                msgM.setMessage("Se acabar a viagem não poderá introduzir mais despesas, tem a certeza que pretende continuar ?");
                msgM.create();
                msgM.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NovaViagem.this, "Viagem Adicionada com Sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FrontPage.class));
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog= msgM.create();
                alertDialog.show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DataHolder app = new DataHolder();
                String nome = app.getData();
                String Origem = app.getOrigem();
                String Destino = app.getDestino();
                String viagem = Origem + "-" + Destino;
                String a = "users" + userId + "CarInfo" + nome + "Viagens" + viagem;
                db.collection(a).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection(a).document(document.getId()).delete();
                            Log.v("Tag", document.getId());
                        }
                    }
                });
            }
        });

        adicionarDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    @Override
    public void applyText(String preco, String tipo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("preco", preco);
        user.put("tipo", tipo);
        DataHolder app = new DataHolder();
        String nome = app.getData();
        String Origem = app.getOrigem();
        String Destino = app.getDestino();
        String viagem = Origem + "-" + Destino;
        String a = "users" + userId + "CarInfo" + nome + "Viagens" + viagem;

        // Add a new document with a generated ID
        db.collection(a)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection(a)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String preco = document.get("preco").toString();
                                String tipo = document.get("tipo").toString();
                                arrayList.add(tipo.toUpperCase() + "         " + preco + "€" );
                            }
                            ArrayAdapter arrayAdapter = new ArrayAdapter(NovaViagem.this, android.R.layout.simple_list_item_1, arrayList);
                            listaDespesas.setAdapter(arrayAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void openDialog(){
        adicionarViagem dialogo = new adicionarViagem();
        dialogo.show(getSupportFragmentManager(), "example dialog");
    }
}