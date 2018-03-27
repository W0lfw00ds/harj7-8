package com.example.iirol.harjoitus78.Database.Firebase;

import com.example.iirol.harjoitus78.Database.Firebase.Repositories.KirjaFirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseDatabase {

    // FIELDS
    public KirjaFirebaseRepository KirjaRepository;

    // Singleton
    private static FirebaseDatabase instance;
    public static FirebaseDatabase getInstance() {

        if (FirebaseDatabase.instance == null) {
            FirebaseDatabase.instance = new FirebaseDatabase();
        }

        return FirebaseDatabase.instance;
    }

    // METHODS
    public static String getUserKey() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("Käyttäjä ei ole kirjautunut!");
        }
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    }

    // CONSTRUCTORS
    private FirebaseDatabase() {
        this.KirjaRepository = new KirjaFirebaseRepository();
    }

}