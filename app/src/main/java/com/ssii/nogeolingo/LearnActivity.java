package com.ssii.nogeolingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class LearnActivity extends AppCompatActivity implements Observer{

    Button unknowButton, knowButton;
    ImageView imagenViewConcept;
    TextView nameConceptTextV;
    FloatingActionButton nextFAButton;
    ProgressBar progressBar;
    android.support.v7.widget.Toolbar toolbar;

    VocabularyDataManager vocabularyDataManager;
    HashMap<String, Concept> concepts;
    HashMap<String, Concept> knownTaughtConcepts;
    HashMap<String, ShownConcept> taughtConcepts;
    HashMap<String, OrderedConcept> orderedConcepts;
    List<OrderedConcept> orderedConceptsList;
    Concept currentConcept;
    Set<String> conceptsKeys;
    String appearanceTime, shownTextTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String currentItem;
    StorageReference gsReference;
    int indexPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        knownTaughtConcepts = new HashMap<>();
        taughtConcepts = new HashMap<>();
        orderedConcepts = new HashMap<>();
        orderedConceptsList = new ArrayList<>();
        initializeComponents();
        loadVocabulary();
    }

    private void loadVocabulary() {
        vocabularyDataManager = VocabularyDataManager.getInstance();
        vocabularyDataManager.addObserver(this);
        Log.d("TEST", "DESPUÉS DE AÑADIR OBSERVADORES");
        vocabularyDataManager.getOrderedConcepts();

    }

    private void initializeComponents() {
        unknowButton = (Button)findViewById(R.id.unknowButton);
        knowButton = (Button)findViewById(R.id.knowButton);
        progressBar = findViewById(R.id.progressBar);

        imagenViewConcept = (ImageView)findViewById(R.id.imageViewConcept);
        nameConceptTextV = (TextView)findViewById(R.id.nameConceptTextV);
        nextFAButton = (FloatingActionButton)findViewById(R.id.nextFloatingButton);

        nameConceptTextV.setVisibility(View.INVISIBLE);
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);

        imagenViewConcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showName();
            }
        });
        nextFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(orderedConceptsList);
                hideButtons();
                chooseConcept();
            }
        });
        unknowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taughtConcepts.put(currentConcept.getName(), new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName()));
                //OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 0, indexPosition);
               // addOrderedConcept(orderedConcept);
                updateConceptPosition(false);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test", "currentConcept = " + currentConcept.getName());
                taughtConcepts.put(currentConcept.getName(), new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName()));
                Log.d("test", "después de añadir taught concept");
                //OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 1, indexPosition);
                //addOrderedConcept(orderedConcept);
                updateConceptPosition(true);
                //conceptsKeys.remove(currentConcept.getName());
               // knownTaughtConcepts.put(currentConcept.getName(), currentConcept);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "on click");
                currentItem = "home";
                saveConceptsInOrder();
            }
        });
        getSupportActionBar().setTitle("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.learn_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_test:
                currentItem = "action_test";
                saveConceptsInOrder();
                return true;
            case R.id.action_help:
                showHelp();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.learn_help_info))
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
        initializeMainActivity();
    }

    private void addOrderedConcept(OrderedConcept orderedConcept) {
        if(!orderedConcepts.containsKey(currentConcept.getName())) {
            orderedConcepts.put(currentConcept.getName(), orderedConcept);
            indexPosition++;
        }
        else {
            if (orderedConcepts.get(currentConcept.getName()).getStrength() == 0 && orderedConcept.getStrength() == 1)
                orderedConcepts.get(currentConcept.getName()).setStrength(1);
        }
    }

   private void chooseConcept() {
       currentConcept = concepts.get(orderedConceptsList.get(0).getName());
       showConcept();
   }

   private void updateConceptPosition(Boolean known) {
       OrderedConcept currentOrderedConcept = orderedConcepts.get(currentConcept.getName());
       Log.d("test", "currentOrderedConcept mostrado: " + currentOrderedConcept.isShown());
       int currentStrenght = currentOrderedConcept.getStrength();
       int currentPosition = currentOrderedConcept.getPosition();
       if(known) {
           currentOrderedConcept.setStrength(currentStrenght+1);

       }
       else {
           currentOrderedConcept.setStrength(0);
       }
       currentOrderedConcept.setPosition(currentPosition + (int)Math.pow(2, currentStrenght+1));
       currentOrderedConcept.setShown(true);
       Log.d("test", "fin de updateConceptPosition");
   }

    private void saveConceptsInOrder() {
        Log.d("TEST", "en save");
        Toast.makeText(this, "Enviando tus progresos", Toast.LENGTH_LONG).show();
        imagenViewConcept.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        vocabularyDataManager.sendTaughtConceptsInOrder(orderedConcepts);
    }

    private void saveTaughtConcepts() {
        vocabularyDataManager.sendTaughtConcepts(taughtConcepts);
    }

    private void initializeMainActivity() {
        Log.d("TEST", "en initializeMain");
        vocabularyDataManager.deleteObserver(this);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void initializeEvaluationActivity() {
        vocabularyDataManager.deleteObserver(this);
        Intent intent = new Intent(this, EvaluationActivity.class);
        finish();

        startActivity(intent);
    }

    private void showConcept() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
        nameConceptTextV.setText(currentConcept.getName());
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
                .into(imagenViewConcept);

    }

    private void showName() {
        Date date = new Date();
        shownTextTime = dateFormat.format(date);
        nameConceptTextV.setVisibility(View.VISIBLE);
       unknowButton.setVisibility(View.VISIBLE);
       knowButton.setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);
        nameConceptTextV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (o.toString()) {
            case "getVocabulary":
                    prepareVocabulary();
                break;
            case "getOrderedConcepts":
                orderedConcepts = VocabularyDataManager.conceptsToEvaluate;
                if(VocabularyDataManager.allConcepts.isEmpty())
                    vocabularyDataManager.getVocabulary();
                else
                    prepareVocabulary();
                break;
            case "sendTaughtConcepts":
                if (currentItem == "home") {
                    Log.d("TEST", "HOME");
                    initializeMainActivity();
                }

                else if (currentItem == "action_test")
                    initializeEvaluationActivity();
                else {
                    Log.d("TEST", "ELSE");
                    initializeMainActivity();
                }

                break;
            case "sendTaughtConceptsInOrder":

                Log.d("test", "se han enviado todos los conceptos ordenados");
                saveTaughtConcepts();

        }

    }

    private void prepareVocabulary() {
        concepts = VocabularyDataManager.allConcepts;
        conceptsKeys = new HashSet<>(concepts.keySet());
        prepareConcepts();
        orderedConceptsList = new ArrayList<>(orderedConcepts.values());
        Collections.sort(orderedConceptsList);
        chooseConcept();
    }

    private void prepareConcepts() {
        int index = 0;
        if (VocabularyDataManager.conceptsToEvaluate.isEmpty()) {
            Log.d("test", "dentro de vocabularydata if");
            for (String concept:conceptsKeys) {
                Log.d("test", "concepto a añadir en orderedCOncepts es: " + concept);
                OrderedConcept orderedConcept = new OrderedConcept(concept, 0, index, false);
                orderedConcepts.put(orderedConcept.getName(), orderedConcept);
                index++;
            }
        }
        else {
            orderedConcepts = VocabularyDataManager.conceptsToEvaluate;

        }
    }


}
