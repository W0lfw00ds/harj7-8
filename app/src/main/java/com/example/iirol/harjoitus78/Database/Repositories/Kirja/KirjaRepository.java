package com.example.iirol.harjoitus78.Database.Repositories.Kirja;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KirjaRepository {

    // FIELDS
    public static final String TABLENAME = "Kirja";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_NIMI = "nimi";
    public static final String COLUMN_PAINOS = "painos";
    public static final String COLUMN_HANKINTAPVM = "hankintapvm";

    // CONSTRUCTORS
    public KirjaRepository() {

    }

    // @Repository
    public String getTableName() {
	    return KirjaRepository.TABLENAME;
    }
    // getAll
    private ArrayList<Kirja> getAllEntities;
	private DatabaseError getAllDatabaseError;
	private void getAllListener() {

		// Luo uusi lista
		KirjaRepository.this.getAllEntities = new ArrayList<>();

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		FirebaseDatabase.getInstance().getReference().child(KirjaRepository.TABLENAME).addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override public void onDataChange(DataSnapshot dataSnapshot) {

					// Tyhjennä vanhat objektit
					KirjaRepository.this.getAllEntities.clear();

					// Lue päivittynyt lista kaikista kirjoista
					for (DataSnapshot data : dataSnapshot.getChildren()) {
						KirjaRepository.this.getAllEntities.add(data.getValue(Kirja.class));
					}

					// Nollaa mahdollinen edellinen tietokantavirhe
					KirjaRepository.this.getAllDatabaseError = null;
				}

				@Override public void onCancelled(DatabaseError databaseError) {
					KirjaRepository.this.getAllDatabaseError = databaseError;
				}
			}
		);

	}
	public ArrayList<Kirja> getAll() {

		// Lazy loading: Jos ensimmäinen kutsu, aja kyselyn listenerin rekisteröinti
		if (KirjaRepository.this.getAllEntities == null) {
			this.getAllListener();
		}

		// Anna kopio oikeasta listasta, koska oikea lista voi muuttua kokoajan
		return (ArrayList<Kirja>)KirjaRepository.this.getAllEntities.clone();

	}
    public void clearTable() {
		// TODO
    }
    public long add(Kirja kirja) {
		// TODO
        return 0;
    }
    public Kirja getByID(int id) {
		// TODO
        return null;
    }
    public Kirja getFirst() {
		// TODO
        return null;
    }
    public boolean modify(Kirja kirja) {
		// TODO

        // Jos henkilö ei ole vielä kannassa, insert
        if (kirja.getId() == null) {
            return (this.add(kirja) > 0);

        // Muuten update
        } else {
        	// TODO
            return false;
        }

    }
    public boolean delete(Kirja kirja) {
		// TODO

        // Jos henkilö ei edes ole kannassa, poistu
        if (kirja.getId() == null) {
            return false;
        }

        // TODO POISTA HERE

        return false;
    }
    public boolean deleteFirst() {

        Kirja ekaKirja = this.getFirst();
        if (ekaKirja != null) {
            return this.delete(ekaKirja);
        } else {
            return false;
        }

    }

}