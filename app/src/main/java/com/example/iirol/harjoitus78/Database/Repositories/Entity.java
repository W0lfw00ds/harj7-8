package com.example.iirol.harjoitus78.Database.Repositories;

public abstract class Entity {

	// Kaikilla olioilla/tiedoilla on avain "key" firebase tietokannassa
	public abstract String getKey();
	public abstract void setKey(String key);

}
