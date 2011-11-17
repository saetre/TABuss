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
	ListView listView;
    @Override
    public void onCreate(Bundle savedInstance)
    {
    	super.onCreate(savedInstance);

          setContentView(R.layout.list);
          dbHelper = new DatabaseHelper(this);
          Cursor cursor = dbHelper.getAllQueries();
          startManagingCursor(cursor);
          columns = new String[] { DatabaseHelper.destination};
          to = new int[] {R.id.destination_entry};

          mAdapter = new SimpleCursorAdapter(this, R.layout.history, cursor, columns, to);
          this.setListAdapter(mAdapter);
    }
}
