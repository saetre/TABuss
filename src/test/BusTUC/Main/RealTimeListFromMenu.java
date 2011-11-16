package test.BusTUC.Main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import test.BusTUC.R;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.BusDeparture;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.TimeUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RealTimeListFromMenu extends ListActivity
{
	public static String ID;
	int outgoing; 
	ArrayList <BusDeparture> stops;
	ArrayList<HashMap<String,Object>> realTimeData;
	HashMap<String, Object> hm;
	ListView lv;
	// Switch server on or off
	boolean server = true;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		String stopName = extras.getString("stopName");
		int stopId = 0;
		if(!server)stopId = extras.getInt("stopId");
		 outgoing = extras.getInt("key");
		lv = getListView();
		lv.setBackgroundColor(Color.parseColor("#3C434A"));
		lv.setCacheColorHint(Color.parseColor("#3C434A"));
		lv.setClickable(false);
		TextView text = new TextView(this);

		text.setTextSize(30);
		text.setTextColor(Color.parseColor("#A3AB19"));
		text.setTypeface(null,Typeface.BOLD);
		text.setText(stopName +"\n");
		text.setClickable(false);
		lv.addHeaderView(text);
		lv.setTextFilterEnabled(true);

		try
		{
			Date date = new Date();

			text.setText(stopName);


			if(!server)stops = Browser.specificRequestForStop(stopId); 
			else stops = Browser.specificRequestForStopServer(outgoing);

			long currenttimeInMillis = date.getTime();
			long arrivaltimeInMillis, diff;


			realTimeData = new ArrayList<HashMap<String,Object>>();

			for(int i=0; i<stops.size(); i++)
			{
				stops.get(i).getArrivalTime().setDate(date.getDate());
				System.out.println("ARRIVAL TIME: " + stops.get(i).getArrivalTime());
				arrivaltimeInMillis = stops.get(i).getArrivalTime().getTime();
				diff = arrivaltimeInMillis - currenttimeInMillis;
				System.out.println("DIFF: " + arrivaltimeInMillis + "  " + currenttimeInMillis + "  " +diff);
				//Date d2 = new Date(diff);

				// long hours =  //TimeUnit.MILLISECONDS.toHours(diff);
				int m_minutes = (int) Math.ceil((TimeUnit.MILLISECONDS.toSeconds(diff)) / 60);
				int hours = m_minutes / 60;
				int minutes = m_minutes % 60;
				//int minutes = (int) TimeUnit.MILLISECONDS.toSeconds(duration)
				//long minutes = TimeUnit.MILLISECONDS.toMinutes(diff)-(hours*60);
				String nAtm = "";
				if(hours == 0){
					nAtm = minutes + " min";
				}else{
					nAtm = hours + "t " + minutes + "m";
				}

				hm = new HashMap<String, Object>();
				hm.put("busNumber", stops.get(i).getLine());
				hm.put("destination", stops.get(i).getDest());
				hm.put("origin", nAtm);
				hm.put("arrivalText",String.format("%d:%02d", stops.get(i).getArrivalTime().getHours(),stops.get(i).getArrivalTime().getMinutes()));

				realTimeData.add(hm);
			}

			setListAdapter(new SimpleAdapter(this, realTimeData, R.layout.realtimelistrow,
					new String[]{"busNumber","destination","origin","arrivalText"}, new int[]{R.id.routeNumber,R.id.destination, R.id.origin,
					R.id.arrival}));
		}
		catch(Exception e)
		{
			System.out.println("No routes found");
			Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT);
		}

	}




}
