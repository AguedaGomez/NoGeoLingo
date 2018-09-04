package com.ssii.nogeolingo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.ssii.nogeolingo.Account.LogInActivity;

public class MainActivity extends AppCompatActivity {

    Button testButton, learnButton;
    android.support.v7.widget.Toolbar toolbar;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitButtons();

    }

    private void InitButtons() {
        learnButton = findViewById(R.id.learnButton);
        testButton = findViewById(R.id.testButton);
        learnButton.setOnClickListener(view -> initializeLearnActivity());
        testButton.setOnClickListener(view -> initializeEvaluationActivity());

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(VocabularyDataManager.user_email);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_help:
                showInfo();
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void showInfo() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.main_help_info))
                .setTitle(R.string.info_about)
                .setIcon(R.drawable.ic_info_outline_red_24dp)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_main_help_positive_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        initializeLogInActivity();
    }

    private void initializeLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    private void initializeEvaluationActivity() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
    }

    private void initializeLearnActivity() {
        Intent intent = new Intent(this, LearnActivity.class);
        startActivity(intent);
    }


}
