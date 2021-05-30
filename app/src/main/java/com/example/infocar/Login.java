package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, mForgetTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    String aux=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.buttonLogin);
        mCreateBtn = findViewById(R.id.secondLogin);
        mForgetTextLink = findViewById(R.id.forgotPassword);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("sai");
            aux = value;
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError(("É necessário preencher este campo!"));
                    return;
                }

                else if(TextUtils.isEmpty(password)){
                    mEmail.setError(("É necessário preencher este campo!"));
                    return;
                }

                if(password.length() < 6){
                    mEmail.setError(("Password deve ter mais de 6 Caracteres!"));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the User

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            // Guarda login
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("LOGIN", email);
                            editor.apply();
                            startActivity(new Intent(getApplicationContext(), FrontPage.class));
                        }else{
                            Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mForgetTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Introduza o seu email para receber o link para o reset.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString().trim();
                        fAuth.sendPasswordResetEmail(mail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Login.this, "O link para recuperar a sua Password foi enviado para o seu email!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Erro! Não conseguimos enviar o link de recuperação da Password para o seu email!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close the Dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i("TAG", "onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("TAG", "onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("TAG", "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("TAG", "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("TAG", "onDestroy");
    }
}