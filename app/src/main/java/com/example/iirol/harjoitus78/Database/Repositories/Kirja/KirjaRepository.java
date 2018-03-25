package com.example.iirol.harjoitus78.Database.Repositories.Kirja;

import com.example.iirol.harjoitus78.Database.Repositories.Repository;

public class KirjaRepository extends Repository<Kirja> {

    // CONSTANTS
    public static final String TABLENAME = "Kirja";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_NIMI = "nimi";
    public static final String COLUMN_PAINOS = "painos";
    public static final String COLUMN_HANKINTAPVM = "hankintapvm";

    // CONSTRUCTORS
    public KirjaRepository() {
		super(Kirja.class);
    }

    // @Repository
	@Override public String getTableName() {
    	return KirjaRepository.TABLENAME;
	}

}