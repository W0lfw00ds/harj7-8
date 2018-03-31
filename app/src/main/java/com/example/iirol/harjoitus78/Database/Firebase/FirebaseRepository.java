package com.example.iirol.harjoitus78.Database.Firebase;

import com.example.iirol.harjoitus78.Database.DatabaseException;
import com.example.iirol.harjoitus78.Database.Entity;
import com.example.iirol.harjoitus78.Database.Repository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public abstract class FirebaseRepository<T extends Entity> extends Repository<T> {

	// METHODS
	@Override public void add(T entity, final ResultListener resultListener) {

		// Jos entity on jo lisätty
		if (entity.getKey() != null) {

			// Kirja on jo tietokannassa eikä sitä voi lisätä, virhe
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Tieto on jo tietokannassa!"));
			}
			return;
		}

		// Lisää entity
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(entityClass.getSimpleName())
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
	@Override public void add(T entity) {
		this.add(entity, null);
	}

	@Override public void modify(T entity, final ResultListener resultListener) {

		// Jos henkilö ei ole vielä kannassa, insert
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Kirja on jo tietokannassa!"));
			}
			return;
		}

		// Lisää entity
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
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
	@Override public void modify(T entity) {
		this.modify(entity, null);
	}

	@Override public void delete(T entity, final FirebaseRepository.ResultListener resultListener) {

		// Jos henkilö ei edes ole kannassa
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onSuccess();
			}
			return;
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
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
	@Override public void delete(T entity) {
		this.delete(entity, null);
	}

	@Override public void deleteFirst(int count, final ResultListener resultListener) {

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
			.limitToFirst(count)
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
	@Override public void deleteFirst(final ResultListener resultListener) {

		this.deleteFirst(1, resultListener);
	}
	@Override public void deleteFirst(int count) {

		this.deleteFirst(count, null);
	}
	@Override public void deleteFirst() {

		this.deleteFirst(1, null);
	}

	@Override public void clear(final ResultListener resultListener) {

		// Poista kaikki kirjat
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
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
	@Override public void clear() {

		this.clear(null);
	}

	@Override public void getAll(final ResultItemsListener<T> resultItemsListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemsListener == null) {
			throw new NullPointerException("Tietoja ei voi saada ilman 'ResultItemsListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(entityClass.getSimpleName())
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						ArrayList<T> foundEntities = new ArrayList<>();

						// Looppaa lista löytyneistä kirjoista
						for (DataSnapshot kirjaDataSnapshot : dataSnapshot.getChildren()) {
							T foundEntity = kirjaDataSnapshot.getValue(FirebaseRepository.this.entityClass);
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
	@Override public void getByKey(String key, final ResultItemListener<T> resultItemListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemListener == null) {
			throw new NullPointerException("Tietoa ei voi saada ilman 'ResultItemListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
			.child(key)
			.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override public void onDataChange(DataSnapshot data) {

					if (data != null && data.exists()) {
						T entity = data.getValue(FirebaseRepository.this.entityClass);
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
	@Override public void getFirst(final ResultItemListener<T> resultItemListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemListener == null) {
			throw new NullPointerException("Ensimmäistä tietoa ei voi saada ilman 'ResultItemListener<T>'-kuuntelijaa!");
		}

		// Lisää kuuntelija Firebase-tietokannalle "KirjatRepository"-tauluun
		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
			.child(FirebaseDatabase.getUserRootNodeName())
			.child(entityClass.getSimpleName())
			.limitToFirst(1)
			.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override public void onDataChange(DataSnapshot dataSnapshot) {

					// Jos ainakin yksi kirja löytyi, palauta se
					for (DataSnapshot foundEntity : dataSnapshot.getChildren()) {
						T entity = foundEntity.getValue(FirebaseRepository.this.entityClass);
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
	public FirebaseRepository(Class<T> entityClass) {

		super(entityClass);
	}

}
