package com.example.iirol.harjoitus78.Database;

import android.os.Parcelable;

public abstract class Entity implements Parcelable {

	// CONSTANTS
	public static final String COLUMN_KEY = "key";

	// FIELDS
	private String key;

	// METHODS
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}

}
