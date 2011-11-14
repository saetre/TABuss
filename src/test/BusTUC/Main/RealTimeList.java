package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import test.BusTUC.R;
import test.BusTUC.Stops.BusDeparture;
import test.BusTUC.Stops.ClosestStopOnMap;


import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RealTimeList extends ListActivity
{
	public static String ID;
	Bundle extras;
	TextView text;
	ArrayList <ClosestStopOnMap> holder;
	ArrayList<HashMap<String,Object>> realTimeData, realTimeStop;
	ClosestStopOnMap pressedStop;
	int outgoing; 
	ArrayList <BusDeparture> stops;

	HashMap<String, Object> hm, hm2;
	ListView lv;
	String [] stopNames;
	// Needed as this activity can be accessed from two places
	// Not not want to register list item clicks if accessed from map
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		extras = getIntent().getExtras();
		lv = getListView();
		lv.setBackgroundColor(Color.parseColor("#3C434A"));
		lv.setCacheColorHint(Color.parseColor("#3C434A"));
		lv.setClickable(true);

		text = new TextView(this);
		text.setClickable(false);
		text.setTextSize(30);
		text.setTextColor(Color.parseColor("#A3AB19"));
		text.setTypeface(null,Typeface.BOLD);
		text.setText("Busstopp nær deg");
		lv.addHeaderView(text);
		setFromExtras();

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	public void setFromMenu(int position)
	{
		try
		{
			Intent intent = new Intent(this,RealTimeListFromMenu.class);
			pressedStop = holder.get(position-1);


			if(Homescreen.realTimeCodes == null)
			{
				returnHome();
			}
			else
			{
				outgoing = Integer.parseInt(Homescreen.realTimeCodes.get(pressedStop.getBusStopID()).toString());

				intent.putExtra("stopId", outgoing);
				intent.putExtra("key", pressedStop.getBusStopID());
				intent.putExtra("stopName", pressedStop.getStopName());

				startActivity(intent);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}

	public void returnHome()
	{
		Intent intent = new Intent(this, Homescreen.class);
		this.startActivity(intent);
	}
	public void setFromExtras()
	{
		try
		{

			//this.getListView().setBackgroundColor(Color.parseColor("#FFFFFF"));
			realTimeStop = new ArrayList<HashMap<String,Object>>();
			holder = extras.getParcelableArrayList("test");
			stopNames = new String[holder.size()];
			Collections.sort(holder);
			for(int i=0; i<holder.size(); i++)
			{

				String tmp = ""+holder.get(i).getBusStopID();		
				if(Integer.parseInt((tmp.substring(4,5))) == 1)
				{
					tmp = "mot sentrum";
				} else if(Integer.parseInt((tmp.substring(4,5))) == 0) tmp = "fra sentrum";
				//holder.get(i).setStopName(holder.get(i).getStopName() + " " + tmp);
				//stopNames[i] = holder.get(i).getStopName();
				hm2 = new HashMap<String, Object>();
				hm2.put("name", holder.get(i).getStopName() );
				hm2.put("toFrom", tmp);
				realTimeStop.add(hm2);
			}
			setListAdapter(new SimpleAdapter(this, realTimeStop, R.layout.realtimestop,
					new String[]{"name","toFrom"}, new int[]{R.id.stopName,R.id.toFrom}));
			//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, stopNames));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		// Check from where this activity is started from
		if(position!=0){
			setFromMenu(position);
		}

	}

}
