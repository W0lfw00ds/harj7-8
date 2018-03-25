package com.example.iirol.harjoitus78.Database;

import com.example.iirol.harjoitus78.Database.Repositories.Kirja.KirjaRepository;
import com.google.firebase.auth.FirebaseAuth;

public class Database {

    // FIELDS
    public com.example.iirol.harjoitus78.Database.Repositories.Kirja.KirjaRepository KirjaRepository;

    // Singleton
    private static Database instance;
    public static Database getInstance() {

        if (Database.instance == null) {
            Database.instance = new Database();
        }

        return Database.instance;
    }

    // METHODS
    public static String getUserKey() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("Käyttäjä ei ole kirjautunut!");
        }
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    }

    // CONSTRUCTORS
    private Database() {
        this.KirjaRepository = new KirjaRepository();
    }

}