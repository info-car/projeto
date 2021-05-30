package com.example.infocar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class adicionarViagem extends AppCompatDialogFragment
{
    TextView Preco;
    CheckBox portagem, combustivel;
    DespesasListener listener;
    String tipo;
    FirebaseAuth fAuth;
    String userId;

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_nova_viagem, null);

        portagem = (CheckBox) view.findViewById(R.id.portagem);
        combustivel = (CheckBox) view.findViewById(R.id.viagens);

        portagem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    portagem.setChecked(true);
                    combustivel.setChecked(false);

                    tipo = "portagem";
                }
            }
        });

        combustivel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    combustivel.setChecked(true);
                    portagem.setChecked(false);

                    tipo = "combustivel";
                }
            }
        });

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}

                }).setPositiveButton("Adicionar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String preco = Preco.getText().toString();
                preco.replace(".", ",");
                if(preco.isEmpty())
                {
                    Preco.requestFocus();
                    Preco.setError("Preencha este campo!");
                    return;
                }

                Log.i("TAG", tipo);
                DataHolder app = new DataHolder();
                String nome = app.getData();
                String origem = app.getOrigem();
                String destino = app.getDestino();
                Map utilizador = new HashMap<>();

                userId = fAuth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                listener.applyText(preco, tipo);

                DatabaseReference reference = database.getReference().child("Users").child(userId).child("CarInfo").child(nome).child("Viagens").child(origem + "-" + destino);
                DatabaseReference reference2 = reference.child("Total Viagem");

                reference2.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        utilizador.put("Total Viagem", snapshot.getValue(Double.class) + Double.parseDouble(preco));
                        reference.setValue(utilizador);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                reference.child(tipo);
                utilizador.put("Despesa", preco);
                reference.updateChildren(utilizador);
            }
        });

        Preco = view.findViewById(R.id.preco);
        portagem = view.findViewById(R.id.portagem);
        combustivel = view.findViewById(R.id.viagens);
        fAuth = FirebaseAuth.getInstance();
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try
        {
            listener = (DespesasListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface DespesasListener
    {
        void applyText(String preco, String tipo);
    }
}