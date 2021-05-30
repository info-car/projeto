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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class adicionarRevisao extends AppCompatActivity {

    EditText kilometros, preco;
    CheckBox filtroOleo, filtroAr, filtroCombu, filtroHabi, correia, oleo;
    Button addRev;
    FirebaseAuth fAuth;
    String userId, nome, fazer="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_revisao);

        kilometros = (EditText) findViewById(R.id.KmRevisao);
        preco = (EditText) findViewById(R.id.gastouRevisao);
        filtroOleo = (CheckBox) findViewById(R.id.filtroOleo);
        filtroAr = (CheckBox) findViewById(R.id.filtroAr);
        correia = (CheckBox) findViewById(R.id.correia);
        addRev = (Button) findViewById(R.id.addRev);
        filtroCombu = (CheckBox) findViewById(R.id.filtroCombustivel);
        filtroHabi = (CheckBox) findViewById(R.id.filtroHabi);
        oleo = (CheckBox) findViewById(R.id.oil);
        fAuth = FirebaseAuth.getInstance();
        DataHolder app = new DataHolder();
        nome = app.getData();

        addRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Km = kilometros.getText().toString();
                Km.replace(".", ",");
                if (Km.isEmpty()) {
                    kilometros.setError("Preencha este campo!");
                    return;
                }
                if (filtroOleo.isChecked()) {
                    fazer += "Filtro Óleo, ";
                }
                if (filtroAr.isChecked()) {
                    fazer += "Filtro Ar, ";
                }
                if (filtroCombu.isChecked()) {
                    fazer += "Filtro Combustível, ";
                }
                if (filtroHabi.isChecked()) {
                    fazer += "Filtro Habitáculo, ";
                }
                if (oleo.isChecked()) {
                    fazer += "Óleo, ";
                }
                if (correia.isChecked()) {
                    fazer += "Correia, ";
                }
                String gasto = preco.getText().toString();
                gasto.replace(".",",");
                if (gasto.isEmpty()) {
                    preco.setError(("Preencha este campo!"));
                    return;
                }

                userId = fAuth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String[] a = currentDate.split("-");
                String strGasto = gasto.replace(".","+");
                DatabaseReference myRef = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Revisões").child(Km + "-" + fazer + "-" + strGasto + "-" + "(" + a[0] + "|" + a[1] + "|" + a[2] + ")");
                Map user = new HashMap<>();
                user.put("Kilometros", Km);
                user.put("Mudanças", fazer);
                user.put("Preço Revisão", gasto);
                user.put("Data", a[0] + ";" + a[1] + ";" + a[2]);
                myRef.setValue(user);
                DatabaseReference reference = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Gastos");
                DatabaseReference reference2 = reference.child("Total Gastos");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map utilizador = new HashMap<>();
                        utilizador.put("Total Gastos", snapshot.getValue(Double.class) + Double.parseDouble(gasto));
                        reference.setValue(utilizador);
                        Toast.makeText(adicionarRevisao.this, "Dados da Revisão adicionados com Sucesso!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                startActivity(new Intent(getApplicationContext(), FrontPage.class));

            }
        });
    }
}