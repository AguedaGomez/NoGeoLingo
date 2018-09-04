package com.ssii.nogeolingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssii.nogeolingo.Objects.Concept;
import com.ssii.nogeolingo.Objects.OrderedConcept;
import com.ssii.nogeolingo.Objects.ShownConcept;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EvaluationActivity extends AppCompatActivity implements Observer {

    final int CONCEPTS_CUANTITY = 7;
    final int FIRST_INDEX = 0;
    final String PROGRESS = "/" + CONCEPTS_CUANTITY;

    Button checkButton;
    ProgressBar progressBar, loadProgressBar;
    ImageView imageView;
    EditText inputNameConcept;
    TextView correctNameText, progressText;
    FloatingActionButton nextFAButton;
    android.support.v7.widget.Toolbar toolbar;

    VocabularyDataManager vocabularyDataManager;
    List<OrderedConcept> orderedConceptList;
    HashMap<String, OrderedConcept> orderedConceptHashMap;
    HashMap<Integer, ShownConcept> evaluatedConcepts;
    int index, currentError;
    Concept currentConcept;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String appearanceTime, shownTextTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    StorageReference gsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        index = 0;
        evaluatedConcepts = new HashMap<>();
        orderedConceptHashMap = new HashMap<>();
        vocabularyDataManager = VocabularyDataManager.getInstance();
        initializeComponents();

        orderedConceptList = new ArrayList<>();
        vocabularyDataManager.addObserver(this);
        if (vocabularyDataManager.allConcepts.isEmpty())
            vocabularyDataManager.getVocabulary();
        else vocabularyDataManager.getOrderedConcepts();
    }

    private void initializeComponents() {
        checkButton = (Button)findViewById(R.id.check);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        inputNameConcept = (EditText) findViewById(R.id.inputNameConcept);
        progressText = findViewById(R.id.progressText);
        correctNameText = findViewById(R.id.correctName);
        correctNameText.setText("");
        nextFAButton = findViewById(R.id.nextFloatingButton);
        nextFAButton.setVisibility(View.INVISIBLE);
        loadProgressBar = findViewById(R.id.loadProgressBar);

        progressText.setText(index + PROGRESS);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "ON CLICK");
                checkAnswer();
            }
        });

        nextFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClick();
            }
        });

        inputNameConcept.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        loadProgressBar.setMax(CONCEPTS_CUANTITY);

        toolbar = findViewById(R.id.mtoolbar);
        toolbar.setNavigationOnClickListener(view -> createAlertDialog());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.evaluation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_help:
                showHelp();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.evaluation_help_info))
                .setTitle(R.string.help_title)
                .setIcon(R.drawable.ic_help_outline_red_24dp)
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

    @Override
    public void onBackPressed() {
       createAlertDialog();
    }

    private void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Si abandonas la evaluación perderás tu progreso. ¿Realmente quieres salir?")
                .setCancelable(false)
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initializeMainActivity();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (o.toString()) {
            case "getOrderedConcepts":
                Log.d("test", orderedConceptList.size() + "");
                addConceptsShown();
                Log.d("test", orderedConceptList.size() + "");
                if (orderedConceptList.size() < CONCEPTS_CUANTITY)
                    createExitDialog();
                else {
                    Collections.sort(orderedConceptList);
                    chooseConcept();
                }

                break;
            case "getVocabulary":
                vocabularyDataManager.getOrderedConcepts();
                break;
            case "sendTaughtConceptsInOrder":

                Log.d("test", "se han enviado todos los conceptos ordenados");
                vocabularyDataManager.sendEvaluatedConcepts(evaluatedConcepts);
                break;
            case "sendEvaluatedConcepts":
                initializeMainActivity();
                break;
                default:
                    break;
        }
    }

    private void addConceptsShown() {
        for(OrderedConcept orderedConcept: VocabularyDataManager.conceptsToEvaluate.values()) {
            if (orderedConcept.isShown()) {
                orderedConceptList.add(orderedConcept);
            }

        }
    }

    private void createExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_evaluation_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_evaluation_positive_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initializeMainActivity();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void nextClick() {
        if (index >= CONCEPTS_CUANTITY) {
            ShownConcept newShownConcept = new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName());
            newShownConcept.setError(currentError);
            Log.d("TEST", "El error es: " + newShownConcept.getError());
            evaluatedConcepts.put(index, newShownConcept);

            //VocabularyDataManager.conceptsToEvaluate.get(currentConcept.getName());
            vocabularyDataManager.sendTaughtConceptsInOrder(orderedConceptHashMap);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.end_evaluation_text)
                    .setCancelable(false)
                    .setPositiveButton(R.string.end_evaluation_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    nextFAButton.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(EvaluationActivity.this, "Guardando tus progresos", Toast.LENGTH_SHORT).show();
                                    //initializeMainActivity();


                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            ShownConcept newShownConcept = new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName());
            newShownConcept.setError(currentError);
            evaluatedConcepts.put(index, newShownConcept);
            chooseConcept();
        }


    }

    private void initializeMainActivity() {
        vocabularyDataManager.deleteObserver(this);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void chooseConcept() {
        nextFAButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        inputNameConcept.setText("");
        correctNameText.setText("");
        enableEditText(true);
        inputNameConcept.setTextColor(Color.DKGRAY);

        String currentName = orderedConceptList.get(FIRST_INDEX).getName();
        Log.d("test", "concepto a enseñar: " + currentName);
        currentConcept = VocabularyDataManager.allConcepts.get(currentName);

        // Show concept image
        gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
       Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);

                        Date date = new Date();
                        appearanceTime = dateFormat.format(date);
                        return false;
                    }
                })
                .into(imageView);

    }

    private void checkAnswer() {
        Log.d("TEST", "checkAnswer");
        boolean correct = false;
        String answer = inputNameConcept.getText().toString();
        if (answer.length()==0) {
            Toast.makeText(this, R.string.toast_text_empty_input_name_evaluation, Toast.LENGTH_SHORT).show();
            return;
        }
        answer = answer.substring(0,1).toUpperCase() + answer.substring(1).toLowerCase();
        if (answer.equals(currentConcept.getName())) {
            Log.d("TEST", "RESPUESTA CORRECTA");
            inputNameConcept.setTextColor(Color.rgb(0,128,0));
            correct = true;
            currentError = 0;
        }
        else {
            correctNameText.setText(currentConcept.getName());
            currentError = 1;
        }
        Date date = new Date();
        shownTextTime = dateFormat.format(date);

        inputNameConcept.setText(answer);
        enableEditText(false);
        updateConceptPosition(correct);
    }

    private void updateConceptPosition(boolean correct) {
        OrderedConcept orderedConcept = orderedConceptList.get(FIRST_INDEX);
        int currentStrenght = orderedConcept.getStrength();
        int currentPosition = orderedConcept.getPosition();
        if (correct)
            orderedConcept.setStrength(currentStrenght+1);
        else
            orderedConcept.setStrength(0);
        currentStrenght = orderedConcept.getStrength();
        int position = currentPosition + (int)Math.pow(2, currentStrenght+1);
        Log.d("TEST", "NUEVA POSICION de "+ orderedConcept.getName() + " es " + position);
        orderedConcept.setPosition(position);
        orderedConceptHashMap.put(currentConcept.getName(), orderedConcept);
        Collections.sort(orderedConceptList);
        index++;
        loadProgressBar.setProgress(index);
        nextFAButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.INVISIBLE);
        progressText.setText(index + PROGRESS);
    }

    private void enableEditText(boolean editable) {
        inputNameConcept.setFocusable(editable);
        inputNameConcept.setClickable(editable);
        inputNameConcept.setCursorVisible(editable);
        inputNameConcept.setFocusableInTouchMode(editable);
    }

}
