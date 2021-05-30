package com.example.infocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    SharedPreferences sharedPreferences;
    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterBtn;
    TextView msecondLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        mFullName = findViewById(R.id.fullName);
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.buttonRegistar);
        msecondLogin = findViewById(R.id.secondLogin);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        // ve se user já está logged in
        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        String login = sharedPreferences.getString("LOGIN", null);

        if (login != null) {
            startActivity(new Intent(getApplicationContext(), FrontPage.class));
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullName = mFullName.getText().toString();
                String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError(("É necessário preencher este campo!"));
                    return;
                }

                else if(TextUtils.isEmpty(password)){
                    mPassword.setError(("É necessário preencher este campo!"));
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError(("Password deve ter mais de 6 Caracteres!"));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // REGISTA O USER NA FIREBASE
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Utilizador Criado!", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            FirebaseDatabase database  = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("Users").child(userID).child("UserInfo");
                            Map user = new HashMap<>();
                            user.put("Full Name", fullName);
                            user.put("Email", email);
                            user.put("Phone", phone);
                            myRef.setValue(user);
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Erro!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        //SE JA ESTIVER REGISTADO VAI PARA O LOGIN
        msecondLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
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