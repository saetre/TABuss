package test.BusTUC.Main;


import test.BusTUC.R;
import test.BusTUC.Database.DatabaseHelper;
import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
public class History extends ListActivity {
	public static String ID;
	DatabaseHelper dbHelper;
	Button deletebutton;
	String[] columns;
	int [] to;
	SimpleCursorAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstance)
    {
    	super.onCreate(savedInstance);
    	  getListView().setBackgroundColor(Color.parseColor("#3C434A"));
    	  getListView().setBackgroundColor(Color.parseColor("#3C434A"));
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
          columns = new String[] { DatabaseHelper.origin, DatabaseHelper.destination, DatabaseHelper.time };
          // the XML defined views which the data will be bound to
          to = new int[] {R.id.origin_entry, R.id.destination_entry, R.id.time_entry };

          // create the adapter using the cursor pointing to the desired data as well as the layout information
           mAdapter = new SimpleCursorAdapter(this, R.layout.history, cursor, columns, to);
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
