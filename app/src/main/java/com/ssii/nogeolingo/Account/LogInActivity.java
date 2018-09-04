package com.ssii.nogeolingo.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssii.nogeolingo.MainActivity;
import com.ssii.nogeolingo.R;
import com.ssii.nogeolingo.VocabularyDataManager;

public class LogInActivity extends AppCompatActivity {

    EditText email, password;
    Button logIn;
    TextView signup;


    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        checkUserActive();
    }

    private void checkUserActive() {
        user = auth.getCurrentUser();
        if (user != null) {
            Log.d("TEST", "YA ESTÁ REGISTRADO");
            initializeMainActivity();
        }
        else {
            initializeComponents();
        }
    }

    private void initializeComponents() {
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        signup = findViewById(R.id.SingupText);
        logIn = findViewById(R.id.logInButton);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "ONCLICK LOGIN");
                String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                auth.signInWithEmailAndPassword(user_email, user_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user = auth.getCurrentUser();
                                    initializeMainActivity();
                                } else {
                                    Toast.makeText(LogInActivity.this, "No se pudo iniciar sesión",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogInActivity.this, "No se pudo iniciar sesión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initializeMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        VocabularyDataManager.user_email = user.getEmail();
        startActivity(intent);
    }
}
