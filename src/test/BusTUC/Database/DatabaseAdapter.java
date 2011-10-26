package test.BusTUC.Database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
public class DatabaseAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ORIGIN = "origin";
	public static final String KEY_DESTINATION = "destination";
	public static final String KEY_TIME = "time";
	private static final String DATABASE_TABLE = "dass";
	private Context context;
	private SQLiteDatabase database;
	private Database dbHelper;

	public DatabaseAdapter(Context context) {
		this.context = context;
	}

	public DatabaseAdapter open() throws SQLException {
		dbHelper = new Database(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */

	public long createQuery(String origin, String destination, int time) {
		ContentValues initialValues = createContentValues(origin, destination, time);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}


	/**
	 * Update the todo
	 */

	public boolean updateQuery(long rowId, String origin, String destination, int time) {
		ContentValues updateValues = createContentValues(origin, destination, time);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}


	
	/**
	 * Deletes todo
	 */

	public boolean deleteQuery(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}


	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */

	public Cursor fetchAllQueries() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_ORIGIN, KEY_DESTINATION, KEY_TIME }, null, null, null,
				null, null);
	}


	/**
	 * Return a Cursor positioned at the defined todo
	 */

	public Cursor fetchQuery(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_ORIGIN, KEY_DESTINATION, KEY_TIME },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String origin, String destination, int time) {
		ContentValues values = new ContentValues();
		values.put(KEY_ORIGIN, origin);
		values.put(KEY_DESTINATION, destination);
		values.put(KEY_TIME, time);
		return values;
	}
}

