package com.example.infocar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Pneu extends AppCompatActivity{

    public static final String TAG = "TAG";
    String userId, nome;
    FirebaseAuth fAuth;
    ListView list;
    Button adiciona;
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pneu);

        list = findViewById(R.id.pneuHistory);
        fAuth = FirebaseAuth.getInstance();
        adiciona = findViewById(R.id.adicionarPneu);
        userId = fAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ArrayAdapter arrayAdapter = new ArrayAdapter(Pneu.this, android.R.layout.simple_list_item_1, arrayList);
        DataHolder app = new DataHolder();

        if(app.getData() != null) {
            nome = app.getData();
            Log.i("TAG", nome);
        }

        adiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TAG", "VOU ADICIONAR PNEUS AO" + nome);
                startActivity(new Intent(getApplicationContext(), adicionarPneu.class));
            }
        });

        //CARREGA HISTORICO DOS PNEUS NA LISTA
        DatabaseReference Ref = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Pneus");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("TAG", "HISTORICO DE PNEUS DO" + nome + "CARREGADOS DA FIREBASE");
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (childDataSnapshot.getKey() != null) {
                        String[] aux = childDataSnapshot.getKey().split("-");
                        Log.v("TAG", "-" + childDataSnapshot.getKey() );
                        aux[2] = aux[2].replace("+","." );
                        arrayList.add(aux[0] + "       " + aux[1] + "       " + aux[2] + "€");
                    }
                }
                list.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("TAG", "ERRO A CARREGAR HISTÓRICO DA FIREBASE");
            }
        });


    }
}