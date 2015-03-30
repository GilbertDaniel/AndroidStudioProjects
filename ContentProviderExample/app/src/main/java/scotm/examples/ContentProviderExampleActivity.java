package scotm.examples;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ContentProviderExampleActivity extends ListActivity {
	
	private static final String TAG = "ImageContent";

	private ListView listView;
    private EditText inputEditText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateLisView(0);
       // tryCalendarIntent();
        setContentView(R.layout.main);
        inputEditText = (EditText) findViewById(R.id.minSize);
    }
    
    private void tryCalendarIntent() {
    	Calendar beginTime = Calendar.getInstance();
    	beginTime.set(2012, Calendar.NOVEMBER, 9, 8, 00);
    	Calendar endTime = Calendar.getInstance();
    	endTime.set(2012, Calendar.NOVEMBER, 9, 19, 00);
    	Intent intent = new Intent(Intent.ACTION_INSERT)
    	        .setData(Events.CONTENT_URI)
    	        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 
    	        			beginTime.getTimeInMillis())
    	        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, 
    	        			endTime.getTimeInMillis())
    	        .putExtra(Events.TITLE, "ALPHA RELEASE")
    	        .putExtra(Events.DESCRIPTION, "Major assignment " +
    	        						"is due in CS371m!!!!")
    	        .putExtra(Intent.EXTRA_EMAIL, "scottm@cs.utexas.edu");
    	startActivity(intent);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "in onResume");
        showImageDataInLog();
    }

	private void populateLisView(int size) {
		listView = getListView(); 
		
		String[] columns = {MediaStore.Images.Media.DATE_TAKEN, 
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.ORIENTATION,
				MediaStore.Images.Media._ID};
		
		int[] textViewIds = {R.id.date_taken, 
				R.id.size, R.id.orientation};
		
		String selectionClause = MediaStore.Images.Media.SIZE + " > ?";
		
		String[] selectionArgs = {Integer.toString(size)};
		
		Cursor imageData = getContentResolver().query(
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    columns,
			    selectionClause,        
			    selectionArgs,   
			    MediaStore.Images.Media.SIZE);
		
		
		ListAdapter adapter = new MyAdapter(this,
				R.layout.list_item_view, 
				imageData, columns, textViewIds);
		
		Log.d(TAG, "count: " + adapter.getCount());
		
		setListAdapter(adapter); 
	}

    public void filterBySize(View v) {
        int size = Integer.parseInt(inputEditText.getText().toString());
        populateLisView(size);
    }
	
	private static class MyAdapter 
		extends SimpleCursorAdapter {
		
		static String format = "MM/dd/yyyy hh:mm a";
		
		private MyAdapter(Context c, int layout, 
				Cursor cur, String[] from, int[] to) {
			super(c,layout, cur, from, to);
		}
		
		public void setViewText(TextView v, String text) {
		    if (v.getId() == R.id.date_taken) {
		        text = getDate(Long.parseLong(text), format);
		    }
		    v.setText(text);
		}

	}

	private void showImageDataInLog() {
		Cursor cursor = getContentResolver().query(
				/* The content URI of the image table*/
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    /* String[] projection, The columns to return for each row 
			     * if null, get them all*/
			    null,    
			    /*  String selection criteria, return rows that match this
			     * if null return all rows  */
			    null,        
			    /* String[] selectionArgs. ?s from selection
			     * ?s replaced by this parameter.*/
			    null,   
			    /* String sortOrder, how to sort row, null unsorted */
			    null);                        
		
		Log.d(TAG, "Image count: " + cursor.getCount());
		Log.d(TAG, "Columns: "  + cursor.getColumnCount());
		String[] columns = cursor.getColumnNames();
		
		Log.d(TAG, "Columns: " + Arrays.toString(columns));
		
		String[] projection = {MediaStore.Images.Media.DATE_TAKEN, 
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.LATITUDE,
				MediaStore.Images.Media.LONGITUDE,
				MediaStore.Images.Media._ID};
		
		cursor = getContentResolver().query(
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    projection,
			    null,        
			    null,   
			    MediaStore.Images.Media.DATE_TAKEN);
		
		// get column indices, refactor to array of ints using projection String[]
		int size 
			= cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
		int dateTaken 
			= cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
		int bucketDisplayName 
			= cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		int latitude
            = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
		Log.d(TAG, "column num for latitude: " + latitude);
		int longitude
            = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
		Log.d(TAG, "column num for longitude: " + longitude);
		
		int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
		    String imageData = "size: " + cursor.getInt(size) + ", ";
			String sDate = format.format(cursor.getLong(dateTaken));
			imageData += "date taken: " + sDate + ", ";
			imageData += "bucket display name: " + cursor.getString(bucketDisplayName) + ", ";
			imageData += "lattitude: " + cursor.getDouble(latitude) + ", ";
			imageData += "longitude: " + cursor.getDouble(longitude) + ", ";
			imageData += "_id: " + cursor.getInt(id);
			Log.d(TAG, imageData);
			cursor.moveToNext();
		}
		
		cursor.moveToFirst();
		int idFirst = cursor.getInt(id);
		trySingleRowWithID(idFirst);
	}
	
	private void trySingleRowWithID(int id) {
	    Log.d(TAG, "Getting single row with id");
	    // know id on 
	    Uri singleUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  id);
	    Cursor cursor = getContentResolver().query(singleUri, null, null, null, null);
	    Log.d(TAG, "column count: " + cursor.getColumnCount());
	    Log.d(TAG, "row count: " + cursor.getCount());
    }

    // from http://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
	public static String getDate(long milliSeconds, String dateFormat) {

	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}

}