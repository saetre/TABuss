package test.BusTUC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import test.BusTUC.BusTUCApp.OracleThread;

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

public class BusList extends ListActivity
{
	public static String ID;
	static  String[] stops;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	  super.onCreate(savedInstanceState);
	  ListView lv = getListView();
	  TextView text = new TextView(this);
	  text.setText(ID +"\n" + "Kommende busser:");
	  lv.addHeaderView(text);
	  lv.setTextFilterEnabled(true);
	  StringBuffer nso
	  String [] neededStopsOutgoing = new String [MapOverlay.foundStopsOutgoing.size()];
	  String [] neededStopsIncoming = new String [MapOverlay.foundStopsIncoming.size()];
	  StringBuffer first;
	  StringBuffer second;
	  String minute1;
	  String minute2;
	  neededStopsOutgoing[0] = "Busser fra sentrum: \n";
	  neededStopsIncoming[0] = "Busser til sentrum: \n";
	  
	  // Find outgoing bustops
	  for(int i=1; i<neededStopsOutgoing.length; i++)
	  {		
		// if minte = 0-9, add a zero
		minute1 = "" +MapOverlay.foundStopsOutgoing.get(i).arrivalTime.getMinutes();
		first = new StringBuffer("" + minute1);
		if(first.length() == 1)
		{
			first.insert(0, "0");
		}
		// Append to string array
		neededStopsOutgoing[i]= "Buss " + MapOverlay.foundStopsOutgoing.get(i).getLine() + " går " +MapOverlay.foundStopsOutgoing.get(i).arrivalTime.getHours() + ":" +first;
		  
	  }
	  
	  // Same for incoming busses
	  for(int i=1; i<neededStopsIncoming.length; i++)
	  {		
	 
		minute2 = "" +MapOverlay.foundStopsIncoming.get(i).arrivalTime.getMinutes();
		second = new StringBuffer("" + minute2);
		if(second.length() == 1)
		{
			second.insert(0, "0");
		}
		
		neededStopsIncoming[i]= "Buss " + MapOverlay.foundStopsIncoming.get(i).getLine() + " går " +MapOverlay.foundStopsIncoming.get(i).arrivalTime.getHours() + ":" +second; 
		  
	  }
	  
	  List<String> list = new ArrayList<String>(Arrays.asList(neededStopsIncoming));
      list.addAll(Arrays.asList(neededStopsOutgoing));      
      Object [] combined = list.toArray();
      String [] returnArray = new String[combined.length];
      
      for(int i=0; i<combined.length; i++)
      {
    	  returnArray[i] = (String)combined[i];
      }
      System.out.println("ARRAY SYZE: " + returnArray.length);

	  
	  
	  
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, returnArray));
	}



	
}
