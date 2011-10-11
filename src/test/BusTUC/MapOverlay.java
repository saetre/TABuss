package test.BusTUC;

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
	public static ArrayList <BusStops> foundStopsOutgoing;
	public static ArrayList <BusStops> foundStopsIncoming;
	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		drawable = defaultMarker;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}

	public MapOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		drawable = defaultMarker;
		m_Context = context;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}
	
	public void addItem(OverlayItem item)
	{
		items.add(item);
		populate();
		System.out.println("ADDITEMSIZE: " + items.size());
	}

	
	// Funker ikke nå, må fikses. Will do på tirsdag:)
	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = (OverlayItem)items.get(index);			

		int lat = (int) (item.getPoint().getLatitudeE6()); 
        int longi = (int) (item.getPoint().getLongitudeE6());
       // Object[] keys = BusTUCApp.gpsCords;//tSet.keySet().toArray();
        Browser k_browser = new Browser();
        HashMap realTimeCodes = k_browser.realTimeData();
     //   System.out.println("SIZE: " + realTimeCodes.size());
        DecimalFormat df = new DecimalFormat("##.#####");

        for(int i=0; i<BusTUCApp.gpsCords.length; i++)
        {
     //   System.out.println("FOO: " + BusTUCApp.gpsCords[i][1] + ": " + df.format(Double.parseDouble(BusTUCApp.gpsCords[i][3])) + "   " + df.format((lat/ 1E6))  + "  " +df.format(Double.parseDouble(BusTUCApp.gpsCords[i][2]))+ "  " +df.format(longi/1E6));
        	if(df.format(Double.parseDouble(BusTUCApp.gpsCords[i][3])).equals(""+df.format(lat/1E6)) && df.format(Double.parseDouble(BusTUCApp.gpsCords[i][2])).equals("" +df.format(longi/1E6)))
        	{
        		
        		System.out.println("FOUND PRESSED STOP! " + BusTUCApp.gpsCords[i][1]);        	
        		int line = Integer.parseInt(BusTUCApp.gpsCords[i][0]);
        		int tempIdOutgoing = Integer.parseInt(realTimeCodes.get(line).toString());
        		StringBuffer temp = new StringBuffer(""+tempIdOutgoing);
        		// Replace 0 with 1 -> direction towards the city centre
        		int tempIdIncoming = Integer.parseInt(temp.replace(4, 5, "1").toString());
        		//int tempId = Integer.parseInt(realTimeCodes.get(16011333).toString());
                System.out.println("FOUND REALTIMECODE: " +BusTUCApp.gpsCords[i][0]);
                foundStopsOutgoing = Browser.specificRequestForStop(tempIdOutgoing);
                foundStopsIncoming = Browser.specificRequestForStop(tempIdIncoming);
                
             //   for(int j=0; j< foundStopsOutgoing.size(); j++)
                //{
                //	System.out.println("Foundstop: " + foundStopsOutgoing.get(j).getLine() + "   " + foundStopsOutgoing.get(j).getArrivalTime().getHours() + "."+ foundStopsOutgoing.get(j).getArrivalTime().getMinutes());
               // }
        	}
        }
		Intent intent = new Intent(m_Context, BusList.class);
		m_Context.startActivity(intent);
		//BusList.ID = item.getTitle();
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