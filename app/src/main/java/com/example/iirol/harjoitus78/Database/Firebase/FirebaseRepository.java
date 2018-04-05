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

	// CONSTRUCTORS
	public FirebaseRepository(Class<T> entityClass) {

		super(entityClass);
	}

	// @Repository<T>
	@Override public void add(T entity, final ResultListener resultListener) {

		if (entity.getKey() != null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("The entity cannot be added in the database because it has already been added before!"));
			}
			return;
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.push() // Generates unique string key
				.setValue(entity, new DatabaseReference.CompletionListener() {

					@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

						if (databaseError != null) {
							if (resultListener != null) {
								resultListener.onError(new DatabaseException("Entity wasn't added to the database!", databaseError));
							}
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

		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity must be added to the database before modifying it!"));
			}
			return;
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.child(entity.getKey()) // Reference contents via the unique string key
				.setValue(entity, new DatabaseReference.CompletionListener() {

					@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

						if (databaseError != null) {
							if (resultListener != null) {
								resultListener.onError(new DatabaseException("Entity wasn't modified in the database!", databaseError));
							}
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

		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onSuccess();
			}
			return;
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.child(entity.getKey())
				.removeValue(new DatabaseReference.CompletionListener() {

					@Override
					public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

						if (databaseError != null) {
							if (resultListener != null) {
								resultListener.onError(new DatabaseException("Entity wasn't deleted from the database!", databaseError));
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
				.child(this.getTableName())
				.limitToFirst(count)
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						// Delete the first n entities found
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
	@Override public void deleteFirst(int count) {

		this.deleteFirst(count, null);
	}
	@Override public void deleteFirst(final ResultListener resultListener) {

		this.deleteFirst(1, resultListener);
	}
	@Override public void deleteFirst() {

		this.deleteFirst(1, null);
	}

	@Override public void clear(final ResultListener resultListener) {

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.removeValue(new DatabaseReference.CompletionListener() {

					@Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

						if (databaseError != null) {
							if (resultListener != null) {
								resultListener.onError(new DatabaseException("Not all entities were deleted!", databaseError));
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

		if (resultItemsListener == null) {
			throw new NullPointerException("Entities cannot be gotten without passed 'ResultItemsListener<T>'-listener!");
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						ArrayList<T> foundEntities = new ArrayList<>();

						// Loop all found entities, convert them to objects and add to list
						for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

							T foundEntity = childDataSnapshot.getValue(FirebaseRepository.this.entityClass);
							foundEntity.setKey(childDataSnapshot.getKey());

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

		if (resultItemListener == null) {
			throw new NullPointerException("Entity cannot be gotten without passed 'ResultItemListener<T>'-listener!");
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
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
	@Override public void getFirst(int count, final ResultItemsListener<T> resultItemsListener) {

		if (resultItemsListener == null) {
			throw new NullPointerException("Entities cannot be gotten without passed 'ResultItemsListener<T>'-listener!");
		}

		com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
				.child(FirebaseDatabase.getUserRootNodeName())
				.child(this.getTableName())
				.limitToFirst(count)
				.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override public void onDataChange(DataSnapshot dataSnapshot) {

						ArrayList<T> foundEntities = new ArrayList<>();

						// Loop all found entities
						for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

							T entity = childDataSnapshot.getValue(FirebaseRepository.this.entityClass);
							entity.setKey(childDataSnapshot.getKey());

							foundEntities.add(entity);
						}

						resultItemsListener.onSuccess(foundEntities);

					}

					@Override public void onCancelled(DatabaseError databaseError) {

						resultItemsListener.onError(new DatabaseException(databaseError));

					}

				});

	}
	@Override public void getFirst(final ResultItemsListener<T> resultItemsListener) {

		this.getFirst(1, resultItemsListener);
	}

}