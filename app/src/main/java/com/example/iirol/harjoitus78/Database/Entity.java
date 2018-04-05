package com.example.iirol.harjoitus78.Database;

import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public abstract class Entity implements Parcelable {

	// CONSTANTS
	public static final String FIELD_KEY = "key";

	// FIELDS
	@Exclude private String key;

	// METHODS
	@Exclude public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}

}