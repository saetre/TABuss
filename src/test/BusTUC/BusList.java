package test.BusTUC;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BusList extends ListActivity
{
	
	public static String ID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  ListView lv = getListView();
	  TextView text = new TextView(this);
	  text.setText(ID +"\n" + "Kommende busser:");
	  lv.addHeaderView(text);
	  lv.setTextFilterEnabled(true);
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, COUNTRIES));
	 
	}
	
	
	 static final String[] COUNTRIES = new String[] {
		    "Rute 5 - Dronningens gate     2 min","Rute 8 - Dronningens gate     1 min",
		    "Rute 63 - Munkegata              10 min", "Rute 1337 - Anfield Road       nå"
		  };


	
}
