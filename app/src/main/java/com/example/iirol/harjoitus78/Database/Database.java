package com.example.iirol.harjoitus78.Database;

import com.example.iirol.harjoitus78.Database.Repositories.Kirja.KirjaRepository;
import com.google.firebase.auth.FirebaseAuth;

public class Database {

    // FIELDS
    public KirjaRepository KirjaRepository;

    // Singleton
    private static Database instance;
    public static Database getInstance() {

        if (Database.instance == null) {
            Database.instance = new Database();
        }

        return Database.instance;
    }

    // CONSTRUCTORS
    private Database() {
        this.KirjaRepository = new KirjaRepository();
    }

}