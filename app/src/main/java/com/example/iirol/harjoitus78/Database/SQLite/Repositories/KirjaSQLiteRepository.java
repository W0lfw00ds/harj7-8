package com.example.iirol.harjoitus78.Database.SQLite.Repositories;

import com.example.iirol.harjoitus78.Database.Entities.Kirja;
import com.example.iirol.harjoitus78.Database.SQLite.SQLiteDatabase;
import com.example.iirol.harjoitus78.Database.SQLite.SQLiteRepository;

public class KirjaSQLiteRepository extends SQLiteRepository<Kirja> {

	// CONSTRUCTORS
	public KirjaSQLiteRepository(SQLiteDatabase sqliteDatabase) {

		super(Kirja.class, sqliteDatabase);
	}

}