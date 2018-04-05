package com.example.iirol.harjoitus78.Database;

public class DatabaseException {

	// FIELDS
	private String message;
	private Object databaseError;

	// METHODS
	public String getMessage() {

		// Jos virheviestiä ei ole, mutta tietokantavirhe on olemassa, palauta tietokantavirhe
		if (this.message == null && this.databaseError != null) {
			return this.databaseError.toString();
		} else {
			return this.message;
		}

	}
	public Object getDatabaseError() {

		return this.databaseError;

	}

	// CONSTRUCTORS
	public DatabaseException(String message, Object databaseError) {
		this.message = message;
		this.databaseError = databaseError;
	}
	public DatabaseException(Object databaseError) {
		this.databaseError = databaseError;
	}
	public DatabaseException(String message) {
		this.message = message;
	}

}