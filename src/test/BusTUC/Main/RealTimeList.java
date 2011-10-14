package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import test.BusTUC.R;
import test.BusTUC.Main.BusTUCApp.OracleThread;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RealTimeList extends ListActivity
{
	public static String ID;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	  super.onCreate(savedInstanceState);
	  ListView lv = getListView();
	  TextView text = new TextView(this);
	  text.setText(MapOverlay.foundBusStop +"\n");
	  lv.addHeaderView(text);
	  lv.setTextFilterEnabled(true);
	  try
	  {
		  String [] neededStopsOutgoing = new String [MapOverlay.foundStopsList.size()];
		  StringBuffer buf;
		  String minute1;
		  neededStopsOutgoing[0] =  "Kommende busser:\n";
		  
		  // Find outgoing bustops
		  for(int i=1; i<neededStopsOutgoing.length; i++)
		  {		
			// if minte = 0-9, add a zero
			minute1 = "" +MapOverlay.foundStopsList.get(i).arrivalTime.getMinutes();
			buf = new StringBuffer("" + minute1);
			if(buf.length() == 1)
			{
				buf.insert(0, "0");
			}
			// Append to string array
			neededStopsOutgoing[i]= "Buss " + MapOverlay.foundStopsList.get(i).getLine() + " går " +MapOverlay.foundStopsList.get(i).arrivalTime.getHours() + ":" +buf + " til " +MapOverlay.foundStopsList.get(i).getDest() ;
			  
		  }
		  // Show in list
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, neededStopsOutgoing));
		  }
	  catch(Exception e)
	  {
		  System.out.println("No routes found");
		  Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT);
	  }
	}



	
}
