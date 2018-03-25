package com.example.iirol.harjoitus78.Database.Repositories;

import com.example.iirol.harjoitus78.Database.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public abstract class Repository<T extends Entity> {

	// VARIABLES
	private final Class<T> clazz;

	// METHODS
	public abstract String getTableName();

	public void add(T entity, final Repository.ResultListener resultListener) {

		// Jos entity on jo lisätty
		if (entity.getKey() != null) {

			// Kirja on jo tietokannassa eikä sitä voi lisätä, virhe
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Tieto on jo tietokannassa!"));
			}
			return;
		}

		// Lisää entity
		FirebaseDatabase.getInstance().getReference()
				.child(Database.getUserKey())
				.child(this.getTableName())
				.push() // Luo automaattisesti generoidun avaimen (key)
				.setValue(entity, new DatabaseReference.CompletionListener() {

					@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

						if (databaseError != null) {
							if (resultListener != null) {
								resultListener.onError(new DatabaseException("Tietoa ei lisätty!", databaseError));
							}
							System.out.println(databaseError.getMessage());

						} else {
							if (resultListener != null) {
								resultListener.onSuccess();
							}
						}

					}

				});
	}
	public void add(T entity) {
		this.add(entity, null);
	}

	public void modify(T entity, final Repository.ResultListener resultListener) {

		// Jos henkilö ei ole vielä kannassa, insert
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Kirja on jo tietokannassa!"));
			}
			return;
		}

		// Lisää entity
		FirebaseDatabase.getInstance().getReference()
			.child(Database.getUserKey())
			.child(this.getTableName())
			.child(entity.getKey()) // Luo automaattisesti generoidun avaimen (key)
			.setValue(entity, new DatabaseReference.CompletionListener() {

				@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

					if (databaseError != null) {
						if (resultListener != null) {
							resultListener.onError(new DatabaseException("Tietoa ei muokattu!", databaseError));
						}
						System.out.println(databaseError.getMessage());

					} else {
						if (resultListener != null) {
							resultListener.onSuccess();
						}
					}

				}

			});

	}
	public void modify(T entity) {
		this.modify(entity, null);
	}

	public void delete(T entity, final Repository.ResultListener resultListener) {

		// Jos henkilö ei edes ole kannassa
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onSuccess();
			}
			return;
		}

		FirebaseDatabase.getInstance().getReference()
				.child(Database.getUserKey())
				.child(this.getTableName())
				.child(entity.getKey())
				.removeValue(new DatabaseReference.CompletionListener() {

					@Override
					public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

					if (databaseError != null) {
						System.out.println(databaseError.getMessage());
						if (resultListener != null) {
							resultListener.onError(new DatabaseException("Tietoa ei poistettu!", databaseError));
						}

					} else {
						if (resultListener != null) {
							resultListener.onSuccess();
						}

					}
					}

				});
	}
	public void delete(T entity) {
		this.delete(entity, null);
	}

	public void deleteFirst(final ResultListener resultListener) {

		FirebaseDatabase.getInstance().getReference()
				.child(Database.getUserKey())
				.child(this.getTableName())
				.limitToFirst(1)
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						// Poista ensimmäinen kirja
						for (DataSnapshot firstEntity : dataSnapshot.getChildren()) {
							firstEntity.getRef().setValue(null);
							break;
						}

						if (resultListener != null) {
							resultListener.onSuccess();
						}
					}

					@Override public void onCancelled(DatabaseError databaseError) {
						if (resultListener != null) {
							resultListener.onError(new DatabaseException(databaseError));
						}
					}

				});

	}
	public void deleteFirst() {
		this.deleteFirst(null);
	}

	public void clear(final ResultListener resultListener) {

		// Poista kaikki kirjat
		FirebaseDatabase.getInstance().getReference()
			.child(Database.getUserKey())
			.child(this.getTableName())
			.removeValue(new DatabaseReference.CompletionListener() {

				@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

					if (databaseError != null) {
						System.out.println(databaseError.getMessage());
						if (resultListener != null) {
							resultListener.onError(new DatabaseException("Kaikkia tietoja ei poistettu!", databaseError));
						}

					} else {
						if (resultListener != null) {
							resultListener.onSuccess();
						}
					}

				}

			});

	}
	public void clear() {
		this.clear(null);
	}

	public void getAll(final ResultItemsListener<T> resultItemsListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemsListener == null) {
			throw new NullPointerException("Tietoja ei voi saada ilman 'ResultItemsListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		FirebaseDatabase.getInstance().getReference()
				.child(Database.getUserKey())
				.child(this.getTableName())
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						ArrayList<T> foundEntities = new ArrayList<>();

						// Looppaa lista löytyneistä kirjoista
						for (DataSnapshot kirjaDataSnapshot : dataSnapshot.getChildren()) {
							T foundEntity = kirjaDataSnapshot.getValue(Repository.this.clazz);
							foundEntity.setKey(kirjaDataSnapshot.getKey());

							foundEntities.add(foundEntity);
						}

						resultItemsListener.onSuccess(foundEntities);
					}

					@Override public void onCancelled(DatabaseError databaseError) {
						resultItemsListener.onError(new DatabaseException(databaseError));
					}

				});

	}
	public void getByKey(String key, final ResultItemListener<T> resultItemListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemListener == null) {
			throw new NullPointerException("Tietoa ei voi saada ilman 'ResultItemListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		FirebaseDatabase.getInstance().getReference()
			.child(Database.getUserKey())
			.child(this.getTableName())
			.child(key)
			.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override public void onDataChange(DataSnapshot data) {

					if (data != null && data.exists()) {
						T entity = data.getValue(Repository.this.clazz);
						entity.setKey(data.getKey());
						resultItemListener.onSuccess(entity);
					} else {
						resultItemListener.onSuccess(null);
					}

				}

				@Override public void onCancelled(DatabaseError databaseError) {
					resultItemListener.onError(new DatabaseException(databaseError));
				}

			});

	}
	public void getFirst(final ResultItemListener<T> resultItemListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemListener == null) {
			throw new NullPointerException("Ensimmäistä tietoa ei voi saada ilman 'ResultItemListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		FirebaseDatabase.getInstance().getReference()
			.child(Database.getUserKey())
			.child(this.getTableName())
			.limitToFirst(1)
			.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override public void onDataChange(DataSnapshot dataSnapshot) {

					// Jos ainakin yksi kirja löytyi, palauta se
					for (DataSnapshot foundEntity : dataSnapshot.getChildren()) {
						T entity = foundEntity.getValue(Repository.this.clazz);
						entity.setKey(foundEntity.getKey());

						resultItemListener.onSuccess(entity);
						return;
					}

					// Jos yhtään ei löytynyt, palauta null
					resultItemListener.onSuccess(null);
					return;
				}

				@Override public void onCancelled(DatabaseError databaseError) {
					resultItemListener.onError(new DatabaseException(databaseError));
				}

			});
	}

	// CONSTRUCTORS
	public Repository(Class<T> clazz) {
		this.clazz = clazz;
	}

	// INTERFACES
	public interface ErrorListener {
		void onError(DatabaseException databaseException);
	}
	public interface ResultListener extends ErrorListener {
		void onSuccess();
	}
	public interface ResultItemListener<T extends Entity> extends ErrorListener {
		void onSuccess(T entity);
	}
	public interface ResultItemsListener<T extends Entity> extends ErrorListener {
		void onSuccess(ArrayList<T> entities);
	}

}
