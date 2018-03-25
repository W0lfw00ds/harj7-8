package com.example.iirol.harjoitus78.Database.Repositories;

import com.google.firebase.database.DatabaseError;

public class DatabaseException {

	// VARIABLES
	private String message;
	private DatabaseError databaseError;

	// METHODS
	public String getMessage() {

		// Jos virheviestiä ei ole, mutta tietokantavirhe on olemassa, palauta tietokantavirhe
		if (this.message == null && this.databaseError != null) {
			return this.databaseError.getMessage();
		} else {
			return this.message;
		}

	}
	public DatabaseError getDatabaseError() {

		return this.databaseError;

	}

	// CONSTRUCTORS
	public DatabaseException(String message, DatabaseError databaseError) {
		this.message = message;
		this.databaseError = databaseError;
	}
	public DatabaseException(DatabaseError databaseError) {
		this.databaseError = databaseError;
	}
	public DatabaseException(String message) {
		this.message = message;
	}

}
