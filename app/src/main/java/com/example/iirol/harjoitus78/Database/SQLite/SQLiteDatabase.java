package com.example.iirol.harjoitus78.Database.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.iirol.harjoitus78.Database.SQLite.Repositories.KirjaSQLiteRepository;

import java.util.ArrayList;

public class SQLiteDatabase extends SQLiteOpenHelper {

	// FIELDS STATIC
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "SQLiteDatabase";
	private ArrayList<SQLiteRepository> repositories;

	// REPOSITORIES
	public KirjaSQLiteRepository KirjaRepository;

	// Singleton
	private static SQLiteDatabase instance;
	public static SQLiteDatabase getInstance(Context context) {
		if (SQLiteDatabase.instance == null) {
			SQLiteDatabase.instance = new SQLiteDatabase(context);
		}
		return SQLiteDatabase.instance;
	}

	// CONSTRUCTORS
	private SQLiteDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.repositories = new ArrayList<>();

		// Repositories
		this.KirjaRepository = new KirjaSQLiteRepository(this);
		this.repositories.add(this.KirjaRepository);
	}

	// @SQLiteOpenHelper
	@Override public void onCreate(android.database.sqlite.SQLiteDatabase writtableDatabase) {

		// Create each table one by one
		for (SQLiteRepository repository : this.repositories) {
			repository.createTableIfNotExists(writtableDatabase);
		}

	}
	@Override public void onUpgrade(android.database.sqlite.SQLiteDatabase writtableDatabase, int oldVersion, int newVersion) {

		// Delete all tables
		for (SQLiteRepository repository : this.repositories) {
			repository.dropTableIfExists(writtableDatabase);
		}

		// Recreate the tables again
		this.onCreate(writtableDatabase);
	}

}
