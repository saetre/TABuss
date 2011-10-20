package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.List;

import test.BusTUC.R;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.Favourite_Act;
import test.BusTUC.Main.Homescreen.MapThread;
import test.BusTUC.Main.Homescreen.OracleThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

public class Answer extends  ListActivity {
	private ListView lv1;
	private Button showButton;
	private String lv_arr[]={"Ruteforslag 1: ","Ruteforslag 2: ","Ruteforslag 3: ","Ruteforslag 4: "};
	private ArrayAdapter ad;
	private ArrayList <String> value;
	Object o;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		showButton = (Button)this.findViewById(R.id.showinmap);
		super.onCreate(savedInstanceState);
		context = this;
		value = new ArrayList<String>();
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);	
		//setContentView(R.layout.list_item);
 
		Bundle extras = getIntent().getExtras();
		if(extras !=null) 
		{
		   value = extras.getStringArrayList("test");
	
		}	
		ad = new ArrayAdapter<String>(this, R.layout.list_item, value);
		setListAdapter(ad);

	
		
		
		//lv1.setAdapter(new ArrayAdapter<String>(this,R.layout.row, R.id.label,lv_arr));
		//selection=(TextView)findViewById(R.id.selection);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		o = this.getListAdapter().getItem(position);
	    new MapThread(context).execute();

	}

	@Override
	public void onBackPressed()
	{
		
		this.finish();
	}
	
	  // Thread classes //
    // Thread starting the oracle queries
  class MapThread extends AsyncTask<Void, Void, Void>
    {
        private Context context;    
        Intent intent;
        ProgressDialog myDialog = null;
        public MapThread(Context context)
        {
        	
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
        	context.startActivity(intent);
        	return null;
        }
        
        @Override
        protected void onPreExecute()
        {
        	intent = new Intent(context, BusTUCApp.class);
          	intent.putExtra("test", (String)o);
        	myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");

        }

        @Override
       protected void onPostExecute(Void unused)
        {
          	myDialog.dismiss();
          	
        }
    }  
	
	

	

}
