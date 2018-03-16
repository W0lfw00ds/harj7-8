package com.example.iirol.harjoitus78.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.iirol.harjoitus78.Database.Repositories.Repository;
import com.example.iirol.harjoitus78.Database.Repositories.Kirja.KirjaRepository;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    // FIELDS STATIC
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "database";
    private ArrayList<Repository> repositories;

    // FIELDS
    public KirjaRepository KirjaRepository;

    // Singleton
    private static Database instance;
    public static Database getInstance(Context context) {
        if (Database.instance == null) {
            Database.instance = new Database(context);
        }
        return Database.instance;
    }

    // CONSTRUCTORS
    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.repositories = new ArrayList<>();

        this.KirjaRepository = new KirjaRepository(this);
        this.repositories.add(this.KirjaRepository);

        // Repositories
        this.repositories.addAll(repositories);
    }

    // @SQLiteOpenHelper
    @Override public void onCreate(SQLiteDatabase writtableDatabase) {

        // Luo jokaisen repositorien taulut
        for (Repository repository : this.repositories) {
	        writtableDatabase.execSQL(repository.getCreateTableIfNotExistsSQL());
        }

    }
    @Override public void onUpgrade(SQLiteDatabase writtableDatabase, int oldVersion, int newVersion) {

        // Poista kaikki repositorien taulut
        for (Repository repository : this.repositories) {
	        writtableDatabase.execSQL("DROP TABLE IF EXISTS " + repository.getTableName() + ";");
        }

        // Luo taulut sitten uudelleen
        this.onCreate(writtableDatabase);
    }

    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
        db.close();
    }
    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE * FROM " + tableName + ";");
        db.close();
    }

}