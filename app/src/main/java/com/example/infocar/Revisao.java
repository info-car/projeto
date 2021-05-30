package com.example.infocar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Revisao extends AppCompatActivity {

    public static final String TAG = "TAG";
    String userId, nome;
    FirebaseAuth fAuth;
    ListView list;
    Button novaRevisao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_revisao);

        list = findViewById(R.id.historicoRevisao);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        novaRevisao = findViewById(R.id.revisaoButton);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DataHolder app = new DataHolder();
        nome = app.getData();
        System.out.println("-----------------------------------------------------------------------------------------" + nome);
        Log.v("TAG", nome);

        novaRevisao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), adicionarRevisao.class));
            }
        });

        //CARREGA HISTORICO DAS REVISOES NA LISTA
        DatabaseReference Ref = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Revisões");
        ArrayList<String> arrayList = new ArrayList<>();
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String[] aux = childDataSnapshot.getKey().split("-");
                    aux[2] = aux[2].replace("+",".");
                    arrayList.add(aux[0] + "       " + aux[1] + "      " + aux[2] + " €" + "      " + aux[3]);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(Revisao.this, android.R.layout.simple_list_item_1, arrayList);
                list.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}