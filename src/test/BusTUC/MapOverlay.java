package test.BusTUC;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

class MapOverlay extends ItemizedOverlay
{
	private Context m_Context;
	private List items;
	private Drawable drawable;
	// Change to none-static later if necessary
	public static ArrayList <BusStops> foundStopsList;
	//public static ArrayList <BusStops> foundStopsIncoming;
	//
	public static String foundBusStop;
	HashMap realTimeCodes;
	String [][] gpsCords;
	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		drawable = defaultMarker;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}

	public MapOverlay(Drawable defaultMarker, Context context, HashMap realTimeCodes, String [][] gpsCords) {
		super(defaultMarker);
		drawable = defaultMarker;
		m_Context = context;
		this.realTimeCodes = realTimeCodes;
		this.gpsCords = gpsCords;
		items = new ArrayList();
		

		// TODO Auto-generated constructor stub
	}
	
	public void addItem(OverlayItem item)
	{
		items.add(item);
		populate();
	}

	
	// Funker ikke nå, må fikses. Will do på tirsdag:)
	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = (OverlayItem)items.get(index);			

		int lat = (int) (item.getPoint().getLatitudeE6()); 
        int longi = (int) (item.getPoint().getLongitudeE6());
        Browser k_browser = new Browser();
        DecimalFormat df = new DecimalFormat("##.#####");

        for(int i=0; i<gpsCords.length; i++)
        {
     //   	System.out.println("Search for: " + Double.parseDouble(gpsCords[i][3])).equals(""+df.format(lat/1E6)) && df.format(Double.parseDouble(gpsCords[i][2])).equals("" +df.format(longi/1E6)))
        	
        	// Compare lat long of pressed spot with lat long of bus stops in gpsCords
        	if(df.format(Double.parseDouble(gpsCords[i][4])).equals(""+df.format(lat/1E6)) && df.format(Double.parseDouble(gpsCords[i][3])).equals("" +df.format(longi/1E6)))
        	{
        		
        		System.out.println("FOUND PRESSED STOP! " + gpsCords[i][1]);        
        		foundBusStop = gpsCords[i][2];
        		int line = Integer.parseInt(gpsCords[i][1]);
        		int tempIdOutgoing = Integer.parseInt(realTimeCodes.get(line).toString());
                System.out.println("FOUND REALTIMECODE: " +gpsCords[i][0]);
                // Add to arrayList
                foundStopsList = Browser.specificRequestForStop(tempIdOutgoing);
           	}
        }
		Intent intent = new Intent(m_Context, BusList.class);
		m_Context.startActivity(intent);
				return true;
		
	}
	


	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return (OverlayItem)items.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(drawable);
	}
	
}