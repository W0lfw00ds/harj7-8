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
    public static String getUserRootNodeName() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("User is not logged in!");
        } else {
            // Firebase UID is used as the root parent for all user's data
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

    }

    // CONSTRUCTORS
    private FirebaseDatabase() {
        this.KirjaRepository = new KirjaFirebaseRepository();
    }

}