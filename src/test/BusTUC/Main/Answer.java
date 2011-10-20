package test.BusTUC.Main;

import test.BusTUC.R;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.Favourite_Act;
import test.BusTUC.Main.Homescreen.OracleThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Answer extends Activity {
	private ListView lv1;
	private Button showButton;
	private String lv_arr[]={"Ruteforslag 1: ","Ruteforslag 2: ","Ruteforslag 3: ","Ruteforslag 4: "};
	
	
	private TextView tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		showButton = (Button)this.findViewById(R.id.showinmap);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);
 		tv = (TextView)findViewById(R.id.suggestions);

		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    String value = extras.getString("test");
		    tv.setText(value);
		}	
	
		
		
		//lv1.setAdapter(new ArrayAdapter<String>(this,R.layout.row, R.id.label,lv_arr));
		//selection=(TextView)findViewById(R.id.selection);
	}
	

	@Override
	public void onBackPressed()
	{
		
		this.finish();
	}

	

}
