package com.example.iirol.harjoitus78.Database.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.example.iirol.harjoitus78.Database.DatabaseException;
import com.example.iirol.harjoitus78.Database.Entity;
import com.example.iirol.harjoitus78.Database.Firebase.FirebaseRepository;
import com.example.iirol.harjoitus78.Database.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public abstract class SQLiteRepository<T extends Entity> extends Repository<T> {

	// FIELDS
	protected SQLiteDatabase sqliteDatabase;
	private ArrayList<Field> fields;
	private String[] fieldNames;

	// METHODS
	public ArrayList<Field> getFields() {

		// Singleton
		if (this.fields == null) {

			this.fields = new ArrayList<Field>();

			// List entity's private properties
			for (Field field : Entity.class.getDeclaredFields()) {
				if (Modifier.isPrivate(field.getModifiers())) {
					field.setAccessible(true); // if you want to modify private fields
					this.fields.add(field);
				}
			}

			// List implemented entity's properties
			for (Field field : this.entityClass.getDeclaredFields()) {
				if (Modifier.isPrivate(field.getModifiers())) {
					field.setAccessible(true); // if you want to modify private fields
					this.fields.add(field);
				}
			}
		}

		return this.fields;
	}
	public String[] getFieldNames() {

		if (this.fieldNames == null) {

			ArrayList<String> parsedFieldNames = new ArrayList<>();
			for (Field field : this.getFields()) {
				parsedFieldNames.add(field.getName());
			}

			this.fieldNames = parsedFieldNames.toArray(new String[0]);
		}

		return this.fieldNames;
	}
	public T cursorToEntity(Cursor cursor) {

		T entity;

		try {

			// Create new instance
			entity = this.entityClass.getConstructor().newInstance();

			// Set all fields from the cursor
			for (Field field : this.getFields()) {

				int columnIndex = cursor.getColumnIndex(field.getName());
				Class type = field.getType();

				// If 'key'-field
				if (field.getName().equals(Entity.COLUMN_KEY)) {
					field.set(entity, String.valueOf(cursor.getInt(columnIndex)));
					continue;
				}

				// Other fields
				if (type == String.class) {
					field.set(entity, cursor.getString(columnIndex));
				} else if (type == byte.class || type == Byte.class) {
					field.set(entity, Byte.valueOf(String.valueOf(cursor.getShort(columnIndex))));
				} else if (type == short.class || type == Short.class) {
					field.set(entity, cursor.getShort(columnIndex));
				} else if (type == int.class || type == Integer.class) {
					field.set(entity, cursor.getInt(columnIndex));
				} else if (type == long.class || type == Long.class) {
					field.set(entity, cursor.getLong(columnIndex));
				} else if (type == float.class || type == Float.class) {
					field.set(entity, cursor.getFloat(columnIndex));
				} else if (type == double.class || type == Double.class) {
					field.set(entity, cursor.getDouble(columnIndex));
				} else if (type == boolean.class || type == Boolean.class) {
					field.set(entity, (cursor.getInt(columnIndex) !=0));
				} else {
					throw new IllegalArgumentException("Data from cursor couldn't be set to the instantiated class!");
				}

			}

		} catch (Exception ex){
			throw new IllegalStateException(ex);
		}

		return entity;
	}
	public ContentValues entityToContentValues(T entity, boolean skipKey) {

		// List object's fields and values
		ContentValues contentValues = new ContentValues();
		for (Field field : this.getFields()) {
			try {
				Class type = field.getType();
				String name = field.getName();
				Object value = field.get(entity);

				// If 'key'-field
				if (name.equals(Entity.COLUMN_KEY)) {
					if (skipKey) {
						continue;
					} else {
						contentValues.put(name, Integer.valueOf((String)value));
						continue;
					}
				}

				// Other fields
				if (type == String.class) {
					contentValues.put(name, (String)value);
				} else if (type == byte.class || type == Byte.class) {
					contentValues.put(name, (Byte)value);
				} else if (type == short.class || type == Short.class) {
					contentValues.put(name, (Short)value);
				} else if (type == int.class || type == Integer.class) {
					contentValues.put(name, (Integer)value);
				} else if (type == long.class || type == Long.class) {
					contentValues.put(name, (Long)value);
				} else if (type == float.class || type == Float.class) {
					contentValues.put(name, (Float)value);
				} else if (type == double.class || type == Double.class) {
					contentValues.put(name, (Double)value);
				} else if (type == boolean.class || type == Boolean.class) {
					contentValues.put(name, (Boolean)value);
				} else if (type == byte[].class || type == Byte[].class) {
					contentValues.put(name, (byte[])value);
				} else {
					throw new IllegalArgumentException("The class has fields not supported by SQLite!");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return contentValues;
	}
	public void dropTableIfExists(android.database.sqlite.SQLiteDatabase writtableDatabase) {

		try {
			writtableDatabase.execSQL("DROP TABLE IF EXISTS " + this.entityClass.getSimpleName());
		} catch (SQLException ex) {
			throw ex;
		}

	}
	public void createTableIfNotExists(android.database.sqlite.SQLiteDatabase writtableDatabase) {

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(this.entityClass.getSimpleName());
		sb.append(" (");

		boolean first = true;
		for (Field field : this.getFields()) {

			// Get field name and type
			String fieldName = field.getName();
			Class fieldType = field.getType();

			// If 'key'-field - save as integer
			if (fieldName.equals(Entity.COLUMN_KEY)) {
				fieldType = int.class;
			}

			// Resolve type in SQLite-database
			String SQLiteType = SQLiteRepository.getSQLiteType(fieldType);

			// Separate columns with comma
			if (!first) {
				sb.append(", ");
			}

			// Set column name and type
			sb.append(fieldName);
			sb.append(" ");
			sb.append(SQLiteType);
			sb.append(" ");

			// If 'key' field, mark as 'PRIMARY KEY'
			if (fieldName.equals(Entity.COLUMN_KEY)) {
				sb.append("PRIMARY KEY AUTOINCREMENT");
			} else {
				sb.append("NOT NULL");
			}

			first = false;
		}

		sb.append(");");

		try {
			writtableDatabase.execSQL(sb.toString());
		} catch (SQLException ex) {
			throw ex;
		}

	}
	protected static String getSQLiteType(Class clazz) {
		// Returns corresponding SQLite's type according to passed data's type

		if (clazz == String.class) {
			return "TEXT";
		} else if (clazz == byte.class || clazz == Byte.class) {
			return "INTEGER";
		} else if (clazz == short.class || clazz == Short.class) {
			return "INTEGER";
		} else if (clazz == int.class || clazz == Integer.class) {
			return "INTEGER";
		} else if (clazz == long.class || clazz == Long.class) {
			return "INTEGER";
		} else if (clazz == float.class || clazz == Float.class) {
			return "REAL";
		} else if (clazz == double.class || clazz == Double.class) {
			return "REAL";
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return "INTEGER";
		} else {
			throw new IllegalArgumentException("The class has fields not supported by SQLite!");
		}

	}

	// CONSTRUCTORS
	public SQLiteRepository(Class<T> entityClass, SQLiteDatabase sqliteDatabase) {
		super(entityClass);

		if (sqliteDatabase == null) {
			throw new NullPointerException("SQLiteDatabase reference must be set!");
		}

		this.sqliteDatabase = sqliteDatabase;
	}

	// @Repository<T>
	@Override public void add(T entity, final ResultListener resultListener) {

		// If null, error
		if (entity == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity is 'null' and cannot be added!"));
			}
			return;
		}

		// If entity has already been added, error
		if (entity.getKey() != null) {

			// Kirja on jo tietokannassa eikä sitä voi lisätä, virhe
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity has already been added in the database!"));
			}
			return;
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getWritableDatabase();
		ContentValues values = this.entityToContentValues(entity, true);
		long addedCount = db.insert(
			entityClass.getSimpleName(),
			null,
			values
		);
		db.close();

		if (resultListener != null) {
			if (addedCount <= 0) {
				resultListener.onError(new DatabaseException("Entity wasn't added for unknown reason!"));
			} else {
				resultListener.onSuccess();
			}
		}

	}
	@Override public void add(T entity) {
		this.add(entity, null);
	}

	@Override public void modify(T entity, final ResultListener resultListener) {

		// If null, error
		if (entity == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity is 'null' and cannot be modified!"));
			}
			return;
		}

		// If the entity isn't added to database
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity hasn't been added to database yet!"));
			}
			return;
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getWritableDatabase();
		boolean updated = false;
		ContentValues values = this.entityToContentValues(entity, true);
		updated = db.update(
				entityClass.getSimpleName(),
				values,
				"key=?",
				new String[] { String.valueOf(entity.getKey()) }
		) > 0;
		db.close();

		if (resultListener != null) {
			if (!updated) {
				resultListener.onError(new DatabaseException("Entity wasn't updated for unknown reason!"));
			} else {
				resultListener.onSuccess();
			}
		}

	}
	@Override public void modify(T entity) {
		this.modify(entity, null);
	}

	@Override public void delete(T entity, final FirebaseRepository.ResultListener resultListener) {

		// If null, error
		if (entity == null) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Entity is 'null' and cannot be deleted!"));
			}
			return;
		}

		// If entity hasn't been added to database yet, return
		if (entity.getKey() == null) {
			if (resultListener != null) {
				resultListener.onSuccess();
			}
			return;
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getWritableDatabase();
		boolean deleted = db.delete(
				entityClass.getSimpleName(),
				"key=?",
				new String[] { String.valueOf(entity.getKey()) }
		) > 0;
		db.close();

		if (resultListener != null) {
			if (!deleted) {
				resultListener.onError(new DatabaseException("Entity wasn't deleted for unknown reason!"));
			} else {
				resultListener.onSuccess();
			}
		}

	}
	@Override public void delete(T entity) {
		this.delete(entity, null);
	}

	@Override public void deleteFirst(int count, final ResultListener resultListener) {

		if (count <= 0) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Invalid 'count' '" + count + "'! Use value greater than 0!"));
			}
			return;
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getWritableDatabase();
		try {
			//Delete from table WHERE id IN (SELECT id FROM table limit 100)
			db.execSQL("DELETE FROM " + entityClass.getSimpleName() + " WHERE key IN (SELECT key FROM " + entityClass.getSimpleName() + " limit " + count + ")");
			if (resultListener != null) {
				resultListener.onSuccess();
			}

		} catch (SQLException ex) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Executing 'deleteFirst' failed!", ex));
			}
			return;

		} finally {
			db.close();
		}

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

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getWritableDatabase();
		try {
			db.execSQL("DELETE FROM " + entityClass.getSimpleName());
		} catch (SQLException ex) {
			if (resultListener != null) {
				resultListener.onError(new DatabaseException("Kaikkia tietoja ei poistettu!", ex));
			}
			return;
		} finally {
			db.close();
		}

		if (resultListener != null) {
			resultListener.onSuccess();
		}

	}
	@Override public void clear() {

		this.clear(null);
	}

	@Override public void getAll(final ResultItemsListener<T> resultItemsListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemsListener == null) {
			throw new NullPointerException("Tietoja ei voi saada ilman 'ResultItemsListener<T>'-kuuntelijaa!");
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getReadableDatabase();
		ArrayList<T> entities = new ArrayList<>();

		Cursor cursor = db.query(
			entityClass.getSimpleName(),
			this.getFieldNames(),
			null,
			null,
			null,
			null,
			null,
			null
		);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {

					T parsedEntity = this.cursorToEntity(cursor);
					entities.add(parsedEntity);

					cursor.moveToNext();
				}
			}

			cursor.close();
		}

		resultItemsListener.onSuccess(entities);

	}
	@Override public void getByKey(String key, final ResultItemListener<T> resultItemListener) {

		// Jos listeneriä ei annettu, error
		if (resultItemListener == null) {
			throw new NullPointerException("Tietoa ei voi saada ilman 'ResultItemListener<T>'-kuuntelijaa!");
		}

		// Jos avainta ei annettu
		if (key == null) {
			throw new NullPointerException("'key' used for selection cannot be null!");
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getReadableDatabase();
		Cursor cursor = db.query(
			entityClass.getSimpleName(),
			this.getFieldNames(),
			"key=?",
			new String[] {
				key
			},
			null,
			null,
			null,
			null
		);

		if (cursor != null) {
			cursor.moveToFirst();
			T entity = this.cursorToEntity(cursor);
			cursor.close();
			resultItemListener.onSuccess(entity);

		} else {
			resultItemListener.onSuccess(null);
		}

	}
	@Override public void getFirst(final ResultItemListener<T> resultItemListener) {

		// If there's no listener, error
		if (resultItemListener == null) {
			throw new NullPointerException("Entity cannot be queried without 'ResultItemListener<T>'!");
		}

		android.database.sqlite.SQLiteDatabase db = this.sqliteDatabase.getReadableDatabase();
		Cursor cursor = db.query(
			entityClass.getSimpleName(),
			this.getFieldNames(),
			null,
			null,
			null,
			null,
			"key ASC",
			"1"
		);

		if (cursor != null) {
			cursor.moveToFirst();

			T entity = this.cursorToEntity(cursor);
			cursor.close();
			resultItemListener.onSuccess(entity);
		} else {
			resultItemListener.onSuccess(null);
		}

	}

}
