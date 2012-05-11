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

package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.HashMap;

import test.BusTUC.Stops.ClosestStopOnMap;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapOverlay extends ItemizedOverlay<OverlayItem>
{

	private Context m_Context;
	private ArrayList<OverlayItem> items;

	private Drawable drawable;
	public String foundBusStop;
	public int foundBusStopNr;
	int lat,longi, outgoing, line;

	HashMap <Integer, Integer> realTimeCodes;
	ClosestStopOnMap[] cl;
	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		drawable = defaultMarker;
		items = new ArrayList<OverlayItem>();

	}

	public MapOverlay(Drawable defaultMarker, Context context, ClosestStopOnMap[] cl) {
		super(boundCenterBottom(defaultMarker));

		drawable = defaultMarker;
		m_Context = context;
		this.cl = cl;
		items = new ArrayList<OverlayItem>();


	}

	public MapOverlay(Drawable defaultMarker, Context context)
	{
		super(boundCenter(defaultMarker));
		drawable = defaultMarker;
		m_Context = context;
	}

	public void addItem(OverlayItem item)
	{
		items.add(item);
		populate();
	}


	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = items.get(index);			
		lat =  (item.getPoint().getLatitudeE6()); 
		longi = (item.getPoint().getLongitudeE6());
		outgoing = 0;
		line = 0;
		for(int i=0; i<cl.length; i++) 
		{
			if(cl[i].getPoint().getLongitudeE6() == (longi) && cl[i].getPoint().getLatitudeE6() == (lat))
			{
				try
				{
					System.out.println("FOUND PRESSED STOP! " +cl[i].getBusStopID());
					foundBusStop = cl[i].getStopName();
					foundBusStopNr = cl[i].getBusStopID();
					line = cl[i].getBusStopID();

					AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
					String tmp = "" + cl[i].getBusStopID();
					// Check which direction buses passing this stop are going
					if(Integer.parseInt((tmp.substring(4,5))) == 1)
					{
						tmp = "til byen";
					} else if(Integer.parseInt((tmp.substring(4,5))) == 0)tmp = "fra byen";
					// If user clicks yes, run thread
					builder.setMessage(cl[i].getStopName() + " " + tmp +"\nVise realtime?").setPositiveButton("Ja", new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(m_Context,RealTimeListFromMenu.class);
						
								intent.putExtra("stopName", foundBusStop);
								intent.putExtra("key", line);
								m_Context.startActivity(intent);
						}

					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// DO nothing, as user canceled
						}
					}).show();	
					break;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		}		

		return true;

	}

	public void returnHome()
	{
		Intent intent = new Intent(m_Context, Homescreen.class);
		m_Context.startActivity(intent);
	}


	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(drawable);
	}

}