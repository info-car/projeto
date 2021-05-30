package com.example.infocar;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class adicionarCarro extends AppCompatActivity {

    EditText marca, modelo, ano, prestacao;
    FirebaseAuth fAuth;
    Button confirmar;
    String userId, nome;
    String carromarca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_carro);

        marca = findViewById(R.id.MarcaCarro);
        modelo = findViewById(R.id.ModeloCarro);
        ano = findViewById(R.id.AnoCarro);
        confirmar = findViewById(R.id.addCar);
        fAuth = FirebaseAuth.getInstance();
        DataHolder app = new DataHolder();

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carromarca = marca.getText().toString().toUpperCase();
                if (carromarca.isEmpty()) {
                    marca.requestFocus();
                    marca.setError("Preenche este campo");
                    return;
                }
                String carromodelo = modelo.getText().toString().toUpperCase();
                if (carromodelo.isEmpty()) {
                    modelo.requestFocus();
                    modelo.setError("Preenche este campo");
                    return;
                }
                String carroano = ano.getText().toString().toUpperCase();
                if (carroano.isEmpty()) {
                    ano.requestFocus();
                    ano.setError("Preenche este campo");
                    return;
                }

                if (!carromarca.isEmpty() && !carromodelo.isEmpty() && !carroano.isEmpty()) {
                    userId = fAuth.getCurrentUser().getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    carromarca = carromarca.replace(".", " ").replace("#", " ").replace("$", " ").replace("[", " ").replace("]", " ");
                    carromodelo = carromodelo.replace(".", " ").replace("#", " ").replace("$", " ").replace("[", " ").replace("]", " ");

                    DatabaseReference myRef = database.getReference().child("Users").child(userId).child("CarInfo").child(carromarca + " " + carromodelo + " " + carroano);
                    Map user = new HashMap<>();
                    user.put("Marca", carromarca);
                    user.put("Modelo", carromodelo);
                    user.put("Ano", carroano);
                    myRef.setValue(user);
                    DatabaseReference reference = database.getReference().child("Users").child(userId).child("CarInfo").child(carromarca + " " + carromodelo + " " + carroano).child("Gastos");
                    Map utilizador = new HashMap<>();
                    utilizador.put("Total Gastos", 0.0);
                    reference.setValue(utilizador);
                    String nomeCarro = carromarca + " " + carromodelo + " " + carroano;
                    app.setData(nomeCarro);
                    Intent intent = new Intent(getBaseContext(), FrontPage.class);
                    intent.putExtra("CarInfo", nomeCarro);
                    startActivity(intent);
                }
            }
        });
    }
}
