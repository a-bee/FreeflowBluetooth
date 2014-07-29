package info.canthub.freeflowbluetooth.database;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import info.canthub.freeflowbluetooth.model.Measurement;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MySQLiteHelper {
 
	
	
	private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "mDb";
 
    public static final String MEASUREMENT_TABLE = "measurements";
    private static final String MEASURE_CREATE = "create table measurements (" +
    		"_id integer primary key not null, " 
    		+ "value real not null, "
            + "measure_date datetime default current_timestamp);";
    //Measurement Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_VALUE = "value";
    public static final String KEY_DATE = "measure_date";
    private static final String[] COLUMNS = {KEY_ID,KEY_VALUE,KEY_DATE};
 
    //private SQLiteDatabase mDb;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
    private boolean open = false;
    private Context context;
    
    
    public SQLiteDatabase get_mDb(){
    	return this.mDb;
    }
    
    public static class DatabaseHelper extends SQLiteOpenHelper {
	    
	    DatabaseHelper(final Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	    }
	   
	    @Override
	    public void onCreate(SQLiteDatabase db) {  
	    	Log.e("OPEN", "create");
	    	db.execSQL(MEASURE_CREATE);
	    }	    
	 
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	    	Log.e("OPEN", "upgrade");
	        db.execSQL("DROP TABLE IF EXISTS measurements");
	        this.onCreate(db);
	    }	   
	    
    }
   
    
    /**
     * Constructor - takes the context to allow the database to be opened/created
     * 
     * @param ctx
     *            the Context within which to work
     */
    public MySQLiteHelper(final Context ctx) {
        this.context = ctx;
        open();
    }
    
    /**
     * Open the notes database. If it cannot be opened, try to create a new instance of the database. If it cannot be
     * created, throw an exception to signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public MySQLiteHelper open() throws SQLException {
        Log.d("OPEN", "open database");
        try {
            if (this.mDbHelper == null) {
                this.mDbHelper = new DatabaseHelper(this.context);
            }
            this.mDb = this.mDbHelper.getWritableDatabase();
            
            if (this.mDb.isReadOnly() || this.mDb.isDbLockedByOtherThreads() || this.mDb.isDbLockedByCurrentThread()) {
                this.mDb.close();
                Log.w("OPEN", "could not open database: locked or readonly");
            } else {
                this.open = true;
            }
        } catch (final Exception e) {
            Log.e("OPEN", "failed to open database", e);
        }
        return this;
    }
    
    
    
    public void close() {
        Log.d("TAG", "close");
        try {
            if (this.open) {
                this.mDb.close();
            }
            this.mDbHelper.close();
            this.open = false;
        } catch (final Exception e) {
            Log.e("TAG", "close failed", e);
        }
    }
    
    
    
    public void destroyDatabase(){
    	Log.d("TAG", "Destroy");
    	context.deleteDatabase(DATABASE_NAME);
    }
    
      
    
    public long createMeasure(final Measurement measurement) {
        Log.d("TAG", "createMeasure " + measurement);
        final ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VALUE, measurement.getValue());
//        initialValues.put(KEY_ID, measurement.getId());
//        initialValues.put(KEY_DATE, measurement.getTimestamp() != null ? measurement.getTimestamp().getTime() : System.currentTimeMillis());
        return this.mDb.insert(MEASUREMENT_TABLE, null, initialValues);
    }
    
    
    
    /** 
     * Adds measurement to the SQLite database
     * **/
    public void addMeasurement(float value){
    	open();
    	
    	Measurement measure = new Measurement(value, new Date());
        Log.d("addMeasurement", measure.toString());
 
        // create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_VALUE, measure.getValue()); // get value
        values.put(KEY_DATE, measure.getTimestamp() != null ? measure.getTimestamp().getTime() : System.currentTimeMillis());
 
        // insert: key/value -> keys = column names/ values = column values
        this.mDb.insert(MEASUREMENT_TABLE, null, values); 
        this.mDb.close(); 
    }
    
}