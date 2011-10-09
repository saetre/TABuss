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

		int lat = (int) ((int) (item.getPoint().getLatitudeE6())); 
        int longi = (int) (item.getPoint().getLongitudeE6());
       // Object[] keys = BusTUCApp.gpsCords;//tSet.keySet().toArray();
        Browser k_browser = new Browser();
        HashMap realTimeCodes = k_browser.realTimeData();
     //   System.out.println("SIZE: " + realTimeCodes.size());
        DecimalFormat df = new DecimalFormat("##.##");

        for(int i=0; i<BusTUCApp.gpsCords.length; i++)
        {
        System.out.println("FOO: " + df.format(Double.parseDouble(BusTUCApp.gpsCords[i][3])) + "   " + (lat/ 1E6)  + "  " +df.format(Double.parseDouble(BusTUCApp.gpsCords[i][2]))+ "  " +(longi/1E6));
        	if(df.format(Double.parseDouble(BusTUCApp.gpsCords[i][3])).equals(""+df.format(lat/1E6)) && df.format(Double.parseDouble(BusTUCApp.gpsCords[i][2])).equals("" +df.format(longi/1E6)))
        	{
        		
              //	Log.v("Value","Value:"+Double.parseDouble(BusTUCApp.tSet.get(keys[i]).toString()));
        		System.out.println("FOUND PRESSED STOP! " + BusTUCApp.gpsCords[i][1]);
        		Iterator it = realTimeCodes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    String tmp = (String) pairs.getKey();
                  //  System.out.println(" Hashmap: " +pairs.getKey() + " = " + pairs.getValue());
                    if(Integer.parseInt(BusTUCApp.gpsCords[i][0].substring(5, 7)) == Integer.parseInt(tmp.substring(5, 5)))
                    {
                    	System.out.println("FOUND REALTIMECODE: " + tmp);
                    	
                    }
                    else
                    {
                    	System.out.println("NOT FOUND REAL");
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
        		
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