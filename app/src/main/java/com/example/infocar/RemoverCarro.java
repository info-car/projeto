package com.example.infocar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RemoverCarro extends AppCompatActivity {
    public static final String TAG = "TAG";
    ListView teste;
    Button remove;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remover_carro);

        teste = findViewById(R.id.teste);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        ArrayList<String> array = new ArrayList<>();
        ArrayAdapter arrayAdap = new ArrayAdapter(RemoverCarro.this, android.R.layout.simple_list_item_1, array);
        DataHolder app = new DataHolder();

        Log.v("TAG", "VOU REMOVER UM CARRO");

        //CARREGA CARROS NA LISTA
        DatabaseReference referencia = data.getReference().child("Users").child(userId).child("CarInfo");
        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("TAG", "CarInfo da FIREBASE DESTE USER:");
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,"-" + childDataSnapshot.getKey()); //displays the key for the node
                    if(!childDataSnapshot.getKey().equals("Totais")){
                        array.add(childDataSnapshot.getKey());
                    }
                }
                teste.setAdapter(arrayAdap);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("TAG", "ERRO A OBTER DADOS DO CarInfo DA FIREBASE");
            }
        });

        teste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString() + " FOI ELIMINADO";
                String carro = parent.getItemAtPosition(position).toString();
                Toast.makeText(RemoverCarro.this, item, Toast.LENGTH_SHORT).show();
                array.remove(position);
                DatabaseReference refe = data.getReference().child("Users").child(userId).child("CarInfo");
                arrayAdap.notifyDataSetChanged();
                if(refe.child(carro) != null) {
                    refe.child(carro).removeValue();
                    app.setData(null);
                }
                startActivity(new Intent(getApplicationContext(), FrontPage.class));
            }
        });
    }
}