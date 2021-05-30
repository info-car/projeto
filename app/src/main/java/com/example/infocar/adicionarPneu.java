package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class adicionarPneu extends AppCompatActivity{

    EditText marca, modelo, preco;
    FirebaseAuth fAuth;
    String userId, nome;
    Button newPneu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_pneu);

        marca = findViewById(R.id.marcaPneu);
        modelo = findViewById(R.id.modeloPneu);
        preco = findViewById(R.id.precoPneu);
        newPneu = findViewById(R.id.newPneu);
        fAuth = FirebaseAuth.getInstance();
        DataHolder app = new DataHolder();

        if(app.getData() != null) {
            nome = app.getData();
            Log.v("TAG", "VOU ADICIONAR UM PNEU AO" + nome);
        }

        newPneu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String marcaPneu = marca.getText().toString();
                System.out.println("--------------" + marcaPneu);
                Log.v("TAG", "MARCA PNEU" + marcaPneu);
                if (marcaPneu.isEmpty()) {
                    marca.setError("Preencha este campo!");
                    return;
                }
                String ModeloPneu = modelo.getText().toString();
                Log.v("TAG", "MODELO PNEU" + ModeloPneu);
                if (ModeloPneu.isEmpty()) {
                    modelo.setError("Preencha este campo!");
                    return;
                }
                String PrecoPneu = preco.getText().toString();
                Log.v("TAG", "PRECO PNEU" + PrecoPneu);
                if (PrecoPneu.isEmpty()) {
                    preco.setError("Preencha este campo!");
                    return;
                }

                userId = fAuth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String str = PrecoPneu.replace(".","+");
                DatabaseReference myRef = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Pneus").child(marcaPneu + "-" + ModeloPneu + "-" + str);
                Map user = new HashMap<>();
                user.put("Marca", marcaPneu);
                user.put("Modelo", ModeloPneu);
                user.put("Preço", PrecoPneu);
                myRef.setValue(user);
                DatabaseReference reference = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Gastos");
                DatabaseReference reference2 = reference.child("Total Gastos");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map utilizador = new HashMap<>();
                        utilizador.put("Total Gastos", snapshot.getValue(Double.class) + Double.parseDouble(PrecoPneu));
                        reference.setValue(utilizador);
                        Toast.makeText(adicionarPneu.this, "Dados do Pneu adicionados com Sucesso!", Toast.LENGTH_SHORT).show();
                        Log.v("TAG", "PNEU ADICIONADO COM SUCESSO");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.v("TAG", "ERRO A ADICIONAR PNEU");
                    }
                });
                startActivity(new Intent(getApplicationContext(), FrontPage.class));
            }
        });
    }
}
