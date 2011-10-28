package test.BusTUC.Main;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.BusDeparture;
import test.BusTUC.Stops.ClosestHolder;
import test.BusTUC.Database.Database;
import test.BusTUC.Main.Homescreen.OracleThread;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapOverlay extends ItemizedOverlay
{

	private Context m_Context;
	private List items;
	private Drawable drawable;
	// Change to none-static later if necessary. Problem with parcelable in this case,
	// is  the Date object
	public static ArrayList <BusDeparture> foundStopsList;
	public String foundBusStop;

	int lat,longi, outgoing;

	HashMap realTimeCodes;
	ClosestHolder[] cl;
	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		drawable = defaultMarker;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}

	public MapOverlay(Drawable defaultMarker, Context context, HashMap realTimeCodes, ClosestHolder[] cl) {
		super(boundCenterBottom(defaultMarker));

		//super(defaultMarker);
		drawable = defaultMarker;
		m_Context = context;
		this.realTimeCodes = realTimeCodes;
		this.cl = cl;
		items = new ArrayList();


		// TODO Auto-generated constructor stub
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




	// Funker ikke nå, må fikses. Will do på tirsdag:)
	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = (OverlayItem)items.get(index);			
		lat =  (item.getPoint().getLatitudeE6()); 
		longi = (item.getPoint().getLongitudeE6());
		outgoing = 0;
		for(int i=0; i<cl.length; i++) 
		{
		
			if(cl[i].getPoint().getLongitudeE6() == (longi) && cl[i].getPoint().getLatitudeE6() == (lat))
			{
				System.out.println("FOUND PRESSED STOP! " +cl[i].getBusStopID());
				foundBusStop = cl[i].getStopName();
				int line = cl[i].getBusStopID();
				outgoing = Integer.parseInt(realTimeCodes.get(line).toString());
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
						new RealTimeThread(m_Context).execute();
						
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO nothing, as user canceled
						
					}
				}).show();	
				
				// System.out.println("FOUND REALTIMECODE: " +gpsCords[i][0]);
				break;
			}
			
		}		

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

	class RealTimeThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;    
		Intent intent;
		ProgressDialog myDialog = null;
		public RealTimeThread(Context context)
		{

			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			long time = System.nanoTime();
			foundStopsList = Browser.specificRequestForStop(outgoing);       
			Intent intent = new Intent(m_Context, RealTimeList.class);
			intent.putExtra("tag", foundBusStop);
			m_Context.startActivity(intent);
			Long newTime = System.nanoTime() - time;
			System.out.println("TIME LOOKUP: " +  newTime/1000000000.0);

			return null;
		}

		@Override
		protected void onPreExecute()
		{

			myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			myDialog.dismiss();

		}
	}

}