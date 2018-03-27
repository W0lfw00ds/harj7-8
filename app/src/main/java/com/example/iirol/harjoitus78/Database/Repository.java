package com.example.iirol.harjoitus78.Database;

import java.util.ArrayList;

public abstract class Repository<T extends Entity> {

	// VARIABLES
	protected final Class<T> entityClass;

	// METHODS
	public abstract void add(T entity, final Repository.ResultListener resultListener);
	public abstract void add(T entity);

	public abstract void modify(T entity, final Repository.ResultListener resultListener);
	public abstract void modify(T entity);

	public abstract void delete(T entity, final Repository.ResultListener resultListener);
	public abstract void delete(T entity);

	public abstract void deleteFirst(int count, final ResultListener resultListener);
	public abstract void deleteFirst(final ResultListener resultListener);
	public abstract void deleteFirst(int count);
	public abstract void deleteFirst();

	public abstract void clear(final ResultListener resultListener);
	public abstract void clear();

	public abstract void getAll(final ResultItemsListener<T> resultItemsListener);
	public abstract void getByKey(String key, final ResultItemListener<T> resultItemListener);
	public abstract void getFirst(final ResultItemListener<T> resultItemListener);

	// CONSTRUCTORS
	public Repository(Class<T> entityClass) {

		if (entityClass == null) {
			throw new NullPointerException("Entity class type is required for sqliteDatabase to work properly!");
		}

		this.entityClass = entityClass;
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
