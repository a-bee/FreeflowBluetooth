package info.canthub.freeflowbluetooth.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.canthub.freeflowbluetooth.database.MySQLiteHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

 
public class Measurement {
 
	public static final String HOUR_MINUTE = "HH:mm:ss";
    public static final String DAY_MONTH_YEAR = "dd/MM/yyyy";
    
	 // only ok because this app is not multithreaded!
    public static final SimpleDateFormat TIME_AND_DATE_FORMAT = new SimpleDateFormat(String.format("%s, %s", HOUR_MINUTE, DAY_MONTH_YEAR));
    
	private Long id;
    private float value = 0;
    private Date timestamp = new Date();
 
    public Measurement() {
        this.id = -1l;
    }
    
 
    public Measurement(float value, Date timestamp) {
        super();
        this.value = value;
        this.timestamp = timestamp;
    }
    
    
    public static Measurement create(final Cursor cursor) {
        final Measurement measurement = new Measurement();
        if (isUsable(cursor)) {
            measurement.id = getLongValue(cursor, MySQLiteHelper.KEY_ID);
            measurement.value = getFloatValue(cursor, MySQLiteHelper.KEY_VALUE);
            measurement.timestamp = new Date(getLongValue(cursor, MySQLiteHelper.KEY_DATE));
            
        } else {
            Log.e("CREATE", "failed to create measure from cursor");
        }
        return measurement;
    }
    
    
    
    
    private static boolean isUsable(final Cursor cursor) {
        return cursor != null && cursor.getCount() > 0 && !cursor.isAfterLast() && !cursor.isBeforeFirst();
    }
    
    protected static boolean hasColumn(final Cursor cursor, final String columName) {
        return cursor.getColumnIndex(columName) > 0;
    }
    
    public static Long getLongValue(final Cursor cursor, final String columnName) {
        if (hasColumn(cursor, columnName)) {
            return cursor.getLong(cursor.getColumnIndex(columnName));
        } else {
             Log.w("Hmm", "Column not found " + columnName);
            return null;
        }
    }
    
    private static Float getFloatValue(final Cursor cursor, final String columnName) {
        if (hasColumn(cursor, columnName)) {
            return cursor.getFloat(cursor.getColumnIndex(columnName));
        } else {
            // Log.w(TAG, "Column not found " + columnName);
            return null;
        }
    }

 
    //getters & setters
    @Override
    public String toString() {
        return "Measurement [id=" + id + ", value=" + value + ", date=" + timestamp
                + "]";
    }
    
    public float getValue(){
    	return value;
    }
    
    public void setValue(float value){
    	this.value = value;
    }
    
    public Long getId(){
    	return id;
    }
    
    public void setId(Long id){
    	this.id = id;
    }

    
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    public String getTimestampString(){
    	
    	final String formattedDate = TIME_AND_DATE_FORMAT.format(timestamp);
    	return formattedDate;
    }
    

    
    public void updateTime(final int hourOfDay, final int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTimestamp());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        setTimestamp(calendar.getTime());
    }

    public void updateDate(final int year, final int monthOfYear, final int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTimestamp());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setTimestamp(calendar.getTime());
    }

}