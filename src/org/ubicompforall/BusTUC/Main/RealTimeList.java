/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package org.ubicompforall.BusTUC.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.ubicompforall.BusTUC.R;
import org.ubicompforall.BusTUC.Stops.BusDeparture;
import org.ubicompforall.BusTUC.Stops.ClosestStopOnMap;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RealTimeList extends ListActivity
{
	public static String ID;
	Bundle extras;
	TextView text;
	Button other;
	ArrayList<ClosestStopOnMap> holder;
	ArrayList<HashMap<String, Object>> realTimeData, realTimeStop;
	ClosestStopOnMap pressedStop;
	int outgoing;
	ArrayList<BusDeparture> stops;
	Context context;
	HashMap<String, Object> hm, hm2;
	ListView lv;
	String[] stopNames;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		context = this;
		super.onCreate(savedInstanceState);
		extras = getIntent().getExtras();
		lv = getListView();
		lv.setBackgroundColor(Color.parseColor("#A3AB19"));
		lv.setCacheColorHint(Color.parseColor("#A3AB19"));
		lv.setClickable(true);

		text = new TextView(this);
		text.setClickable(false);
		text.setTextSize(21);
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setTextColor(Color.parseColor("#FFFFFF"));
		text.setText("Busstopp nær deg");
		text.setHeight(50);
		lv.addHeaderView(text);

		other = new Button(this);
		other.setClickable(true);
		other.setTextSize(21);
		other.setBackgroundColor(Color.parseColor("#FFFFFF"));
		other.setTextColor(Color.parseColor("#3C434A"));
		other.setTypeface(null, Typeface.BOLD);
		other.setText("Annet busstopp");
		other.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, OtherBusstop.class);
				startActivity(intent);
			}
		});
		lv.addFooterView(other);
		setFromExtras();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	public void setFromMenu(int position)
	{
		try
		{
			Intent intent = new Intent(this, RealTimeListFromMenu.class);
			pressedStop = holder.get(position - 1);

			intent.putExtra("key", pressedStop.getBusStopID());
			intent.putExtra("stopName", pressedStop.getStopName());

			startActivity(intent);

		} catch (Exception e)
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

			// this.getListView().setBackgroundColor(Color.parseColor("#FFFFFF"));
			realTimeStop = new ArrayList<HashMap<String, Object>>();
			holder = extras.getParcelableArrayList("test");
			stopNames = new String[holder.size()];
			Collections.sort(holder);
			for (int i = 0; i < holder.size(); i++)
			{

				String tmp = "" + holder.get(i).getBusStopID();
				if (Integer.parseInt((tmp.substring(4, 5))) == 1)
				{
					tmp = "mot sentrum";
				} else if (Integer.parseInt((tmp.substring(4, 5))) == 0)
					tmp = "fra sentrum";
				// holder.get(i).setStopName(holder.get(i).getStopName() + " " +
				// tmp);
				// stopNames[i] = holder.get(i).getStopName();
				hm2 = new HashMap<String, Object>();
				hm2.put("name", holder.get(i).getStopName());
				hm2.put("toFrom", tmp);
				realTimeStop.add(hm2);
			}
			setListAdapter(new SimpleAdapter(this, realTimeStop,
					R.layout.realtimestop, new String[]
					{ "name", "toFrom" }, new int[]
					{ R.id.stopName, R.id.toFrom }));
			// setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
			// stopNames));
		} catch (Exception e)
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
		if (position != 0)
		{
			setFromMenu(position);
		}

	}

}
