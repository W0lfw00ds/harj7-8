package com.example.iirol.harjoitus78;

import android.app.Application;
import android.content.Context;

import com.example.iirol.harjoitus78.Database.Firebase.FirebaseDatabase;
import com.example.iirol.harjoitus78.Database.SQLite.SQLiteDatabase;

public class harjoitus78 extends Application {

	// FIELDS
	private static Context context;

	// METHODS
	public static Context getAppContext() {
		return harjoitus78.context;
	}
	public static FirebaseDatabase getFirebaseDatabase() {
		return FirebaseDatabase.getInstance();
	}
	public static SQLiteDatabase getSQLiteDatabase() {
		return SQLiteDatabase.getInstance(harjoitus78.getAppContext());
	}

	// @Application
	@Override public void onCreate() {
		super.onCreate();
		harjoitus78.context = this.getApplicationContext();
	}

}
