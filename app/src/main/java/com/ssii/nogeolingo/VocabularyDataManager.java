package com.ssii.nogeolingo;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ssii.nogeolingo.Objects.Concept;
import com.ssii.nogeolingo.Objects.OrderedConcept;
import com.ssii.nogeolingo.Objects.ShownConcept;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Ague on 18/08/2018.
 */

public class VocabularyDataManager extends Observable{
    private static final VocabularyDataManager ourInstance = new VocabularyDataManager();

    public static VocabularyDataManager getInstance() {
        return ourInstance;
    }

    public static HashMap<String, Concept> allConcepts; // All concepts available
    public static HashMap<String, OrderedConcept> conceptsToEvaluate;

    public static String currentPlace;
    public static String user_email;

    private VocabularyDataManager() {
        currentPlace = "Any";
        allConcepts = new HashMap<>();
        conceptsToEvaluate = new HashMap<>();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getVocabulary() {

        db.collection("concepts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TEST", document.getId() + " => " + document.getData());
                                String name = document.getData().get("name").toString();
                                String imageRoute = document.getData().get("image").toString();
                                Concept concept = new Concept(name, imageRoute);
                                allConcepts.put(name, concept);
                            }
                            setChanged();
                            notifyObservers("getVocabulary");

                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getOrderedConcepts() { // Tiene que ser los conceptos con el orden y la fuerza
        conceptsToEvaluate.clear();
        Log.d("TEST", "en getOrderedConcepts");
        db.collection("users/" + user_email + "/actions/taughtConceptsInOrder/" + currentPlace)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TEST", document.getId() + " => " + document.getData());
                                String name = document.getId().toString();
                                int strength = Integer.valueOf(document.getData().get("strength").toString());
                                int position = Integer.valueOf(document.getData().get("position").toString());
                                boolean shown = document.getBoolean("shown");
                                OrderedConcept orderedConcept = new OrderedConcept(name, strength, position, shown);
                                conceptsToEvaluate.put(name, orderedConcept);
                            }
                            setChanged();
                            notifyObservers("getOrderedConcepts");

                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void sendTaughtConcepts(HashMap<String, ShownConcept> concepts) {
        CollectionReference users = db.collection("users").document(user_email).collection("actions").document("taughtConcepts").collection(currentPlace);
        for (ShownConcept sc: concepts.values()) {
            users
                    .add(sc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
        }
        setChanged();
        notifyObservers("sendTaughtConcepts");

    }

    public void sendTaughtConceptsInOrder(HashMap<String, OrderedConcept>concepts) {
        int conceptsToSend = concepts.size();
        final int[] index = {0};
        for (OrderedConcept oc: concepts.values()) {
            Log.d("TEST", "concepto en orden a enviar: " + oc.getName());
            db.collection("users").document(user_email).collection("actions").document("taughtConceptsInOrder").collection(currentPlace).document(oc.getName())
                    .set(oc, SetOptions.merge())//SetMerge
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            index[0] ++;
                            if (index[0] == conceptsToSend) {
                                setChanged();
                                notifyObservers("sendTaughtConceptsInOrder");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TEST", "Ha fallado: " + e.toString());
                        }
                    });
        }


    }

    public void sendEvaluatedConcepts(HashMap<Integer, ShownConcept> concepts) {
        CollectionReference users = db.collection("users").document(user_email).collection("actions").document("evaluatedConcepts").collection(currentPlace);
        for (ShownConcept sc: concepts.values()) {
            users
                    .add(sc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
        }
        setChanged();
        notifyObservers("sendEvaluatedConcepts");
    }

    public void createUser(String age, String genre) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("age", age);
        userData.put("genre", genre);
        String email = auth.getCurrentUser().getEmail();
        db.collection("users").document(email)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TEST", "Se ha creado los datos del usuario en Firestore con éxito");
                    }
                });
    }
}
