package com.example.iirol.harjoitus78.Database.Firebase.Repositories;

import com.example.iirol.harjoitus78.Database.Entities.Kirja;
import com.example.iirol.harjoitus78.Database.Firebase.FirebaseRepository;

public class KirjaFirebaseRepository extends FirebaseRepository<Kirja> {

    // METHODS

    // CONSTRUCTORS
    public KirjaFirebaseRepository() {
		super(Kirja.class);
    }

}