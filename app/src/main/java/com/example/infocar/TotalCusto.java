package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TotalCusto extends AppCompatActivity {

    public static final String TAG = "TAG";
    ListView listaTotal;
    TextView total;
    FirebaseAuth fAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_custo);
        Log.v("TAG","ESTOU NO CUSTO");

        listaTotal = findViewById(R.id.listaTotal);
        total = findViewById(R.id.total);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DataHolder app = new DataHolder();
        String nome = app.getData();

        DatabaseReference Ref = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Pneus");
        ArrayList<String> arrayList = new ArrayList<>();
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String[] aux = childDataSnapshot.getKey().split("-");
                    arrayList.add(aux[0] + "       " + aux[1] + "       " + aux[2] + "€");
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(TotalCusto.this, android.R.layout.simple_list_item_1, arrayList);
                listaTotal.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        DatabaseReference ref = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Revisões");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    String[] aux = childDataSnapshot.getKey().split("-");
                    arrayList.add(aux[0] + "       " + aux[1] + "       " + aux[2] + "€");
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(TotalCusto.this, android.R.layout.simple_list_item_1, arrayList);
                listaTotal.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        DatabaseReference reference = database.getReference().child("Users").child(userId).child("CarInfo").child(nome);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.v("TAG","-----------" + snapshot.getValue());
                if(snapshot.getValue() == null){
                    total.setText("0.0 €");
                }
                else {
                    DatabaseReference reference2 = reference.child("Gastos").child("Total Gastos");
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            total.setText(String.valueOf(snapshot.getValue(Double.class)) + "€");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}