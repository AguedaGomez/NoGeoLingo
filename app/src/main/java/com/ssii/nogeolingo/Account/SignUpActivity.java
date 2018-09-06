package com.ssii.nogeolingo.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ssii.nogeolingo.R;
import com.ssii.nogeolingo.VocabularyDataManager;

public class SignUpActivity extends AppCompatActivity {

    EditText email, password, age;
    Button signUpButton;
    String user_genre;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    VocabularyDataManager vocabularyDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        vocabularyDataManager = VocabularyDataManager.getInstance();
        initializeComponents();
    }

    private void initializeComponents() {
        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        age = findViewById(R.id.ageText);
        signUpButton = findViewById(R.id.signUpbtn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();
                    String user_age = age.getText().toString().trim();

                    progressDialog.setMessage("Realizando registro");
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Te has registrado con éxito", Toast.LENGTH_SHORT).show();
                                vocabularyDataManager.createUser(user_age, user_genre);
                                initializeLogInActivity();
                            } else {

                                Toast.makeText(SignUpActivity.this, "No se ha podido registrar este usuario", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view). isChecked();

        switch (view.getId()) {
            case R.id.femRadioButton:
                if(checked)
                    user_genre = "F";
                    break;
            case R.id.masRadioButton:
                if(checked)
                    user_genre = "M";
                    break;
        }
    }

    private Boolean validate() {
        Boolean result = false;

        String _email = email.getText().toString();
        String _password = password.getText().toString();
        String _age = age.getText().toString();

        if (_email.isEmpty() || _password.isEmpty() || _age.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_text_empty_field), Toast.LENGTH_SHORT).show();

        }
        else if (Integer.valueOf(_age) < 8 || Integer.valueOf(_age) > 70)
            Toast.makeText(this, getString(R.string.toast_text_invalidate_age), Toast.LENGTH_SHORT).show();
        else if (!_email.contains("@") || !_email.contains("."))
            Toast.makeText(this, getString(R.string.toast_text_invalidate_email), Toast.LENGTH_SHORT).show();
        else if (_password.length() < 6)
            Toast.makeText(this, getString(R.string.toast_text_invalidate_password), Toast.LENGTH_SHORT).show();
        else
            result = true;
        return result;
    }

    private void initializeLogInActivity() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}
