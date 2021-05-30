package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaViagens extends AppCompatActivity
{
    Button novaViagem;
    ListView listaViagens;
    String userId;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_viagens);

        novaViagem = findViewById(R.id.novaViagem);
        listaViagens = findViewById(R.id.listaViagens);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        DataHolder app = new DataHolder();
        String nome = app.getData();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter arrayAdapter = new ArrayAdapter(ListaViagens.this, android.R.layout.simple_list_item_1, arrayList);
        DatabaseReference myRef = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Viagens");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference refe = myRef.child(childDataSnapshot.getKey()).child("Total Viagem");
                    refe.addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = childDataSnapshot.getKey() + "  " + snapshot.getValue() + " €";
                            arrayList.add(aux);
                            Log.v("Tag", aux);
                            listaViagens.setAdapter(arrayAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        novaViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NovaViagem.class));
            }
        });
    }
}