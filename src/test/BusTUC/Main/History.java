package test.BusTUC.Main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import test.BusTUC.R;
import test.BusTUC.Database.DatabaseHelper;
import test.BusTUC.Main.Homescreen.OracleThread;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
public class History extends ListActivity {
	public static String ID;
	DatabaseHelper dbHelper;
	Button deletebutton;
    @Override
    public void onCreate(Bundle savedInstance) {
    	super.onCreate(savedInstance);
          setContentView(R.layout.list);
          deletebutton = (Button)findViewById(R.id.deleteHistory);
          // some code
          dbHelper = new DatabaseHelper(this);
          Cursor cursor = dbHelper.getAllQueries();
          startManagingCursor(cursor);
          String [] column = cursor.getColumnNames();
          for(String c:column){
        	  System.out.println("COLUMN: " + c);
          }
          Toast.makeText(this, "FOUND " + cursor.getCount() + " Columsn", Toast.LENGTH_LONG).show();
          // the desired columns to be bound
          String[] columns = new String[] { DatabaseHelper.origin, DatabaseHelper.destination, DatabaseHelper.time };
          // the XML defined views which the data will be bound to
          int[] to = new int[] {R.id.origin_entry, R.id.destination_entry, R.id.time_entry };

          // create the adapter using the cursor pointing to the desired data as well as the layout information
          SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.history, cursor, columns, to);
          
          // set this adapter as your ListActivity's adapter
          this.setListAdapter(mAdapter);
          //cursor.close();
          deletebutton.setOnClickListener(new OnClickListener() 
  			{
  				@Override
  				public void onClick(View v) 
  				{	
  					dbHelper.clearDatabase();	  
  				}
  			});
    }	
}
