package test.BusTUC.Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import test.BusTUC.R;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Path.NavigationDataSet;
import test.BusTUC.Path.NavigationSaxHandler;
import test.BusTUC.Path.RouteOverlay;
import test.BusTUC.Stops.BusStop;
import test.BusTUC.Stops.ClosestStopOnMap;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BusTUCApp extends MapActivity 
{    
	MapOverlay mapOverlay;
	ArrayList <ClosestStopOnMap> temp;
	ArrayList <Integer> id;
	MapView mapView; // Google Maps
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
	GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
	GPS k_gps; // Object of the GetGPS class. 
	//   HashMap<Integer,Location> tSetAllStops; // HashMap used for finding closest locations. Adds stops from both sides of the road. For use on map w
	String provider; // Provider 
	HashMap <Integer, Integer> realTimeCodes; 
	MyLocationOverlay myLocation;
	Context context;
	boolean fromExtras = false;
	Location currentLocation;
	/** Called when the activity is first created. */

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mapView = (MapView) findViewById(R.id.mapView); 
		mapView.setStreetView(true);
		LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
		context = this;
		this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		View zoomView = mapView.getZoomControls(); 

		zoomLayout.addView(zoomView, 
				new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT)); 
		mapView.displayZoomControls(true);

		mc = mapView.getController();
		System.out.println("Sjeker lengde: " +Homescreen.gpsCords.length);
		System.out.println("Sjekker browser: " +Homescreen.k_browser.toString());


		try
		{
			realTimeCodes = Homescreen.realTimeCodes;
			currentLocation = Homescreen.currentlocation;
			System.out.println("Realtinmecodessizefirst: " + realTimeCodes.size());
		}
		catch(Exception e)
		{
			Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
			ArrayList <String> err = new ArrayList <String>();
			err.add(e.toString());
			SDCard.generateNoteOnSD("errorBusTUCAPPREALTIME", err, "errors");
			this.finish();

		}       

		final Bundle extras = getIntent().getExtras();
		// add the current location as a GeoPoint
		p = new GeoPoint(
				(int) (currentLocation.getLatitude() * 1E6), 
				(int) (currentLocation.getLongitude() * 1E6));

		myLocation=new MyLocationOverlay(context, mapView)
		{
			@Override
			public void onLocationChanged(Location loc)
			{
				super.onLocationChanged(loc);

				System.out.println("LOCATIONCHANGE IN MAP");
				// Update loc if new loc is more than 10 metres in air dist from last loc
				System.out.println("DIFF LEN: " + loc.distanceTo(currentLocation));
				if(extras == null && loc.distanceTo(currentLocation) > 10)
				{
					try
					{
						Toast.makeText(context, "Oppdaterer kart", Toast.LENGTH_SHORT).show();
						initializeMap(true, loc);
						Toast.makeText(context, "Kart oppdatert", Toast.LENGTH_SHORT).show();
						mc.animateTo(myLocation.getMyLocation());
					}
					catch(Exception e)
					{
						Toast.makeText(context, "Klarte ikke oppdatere kart",Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					//new UpdateMapThread(context, loc).execute();
				}
				else
				{
					mc.animateTo(myLocation.getMyLocation());

				}
				
				currentLocation = loc;

			}
		};
		System.out.println("Enabling location");
		if(!myLocation.isMyLocationEnabled()) System.out.println("LOCATION NOT ENABLED");
		if(!myLocation.enableMyLocation()) System.out.println("COULD NOT ENABLE LOCATION");
		if(!myLocation.isMyLocationEnabled()) System.out.println("LOCATION STILL NOT ENABLED");
		if(!myLocation.enableCompass()) System.out.println("COULD NO ENABLE PROVIDERS");

		// If extras != null -> Activity started based on query answer.
		// Need to extract info
		if(extras !=null) 
		{
			temp = new ArrayList <ClosestStopOnMap>();
			id = new ArrayList <Integer>();
			fromExtras = true;
			ArrayList <Route> foundRoutes = new ArrayList <Route>();
			String value = "";
			double[] dest = new double[2];
			// Extras will now contain an ArrayList<Route>
			foundRoutes = extras.getParcelableArrayList("test");
			int position = extras.getInt("pos");
			ClosestStopOnMap buf = new ClosestStopOnMap();
			//  value = extras.getString("test");
			// Iterate through the closest stop, and match bus stop id


			for(int k=0; k<Homescreen.allStops.size(); k++)
			{
				if(foundRoutes.get(position).getBusStopNumber() == Homescreen.allStops.get(k).stopID)
				{
					System.out.println("FOUND TRANSFER ID: " +Homescreen.allStops.get(k).stopID );
					int latitude =  (int) (Homescreen.allStops.get(k).location.getLatitude() *1E6);
					int longitude = (int) (Homescreen.allStops.get(k).location.getLongitude() *1E6);
					buf = new ClosestStopOnMap(new GeoPoint(latitude,longitude), Homescreen.allStops.get(k).stopID, Homescreen.allStops.get(k).name);
					temp.add(buf);					

					id.add(Homescreen.allStops.get(k).stopID);
					dest[0] = Homescreen.allStops.get(k).location.getLatitude() *1E6;
					dest[1] = Homescreen.allStops.get(k).location.getLongitude()*1E6;
					break;
				}


			} 

			// Initialise mapOverlay, and add items
			initializePress(buf);

			// Draw air dist
			if(!id.isEmpty())
			{
				//	drawPath(id);
			}
		
			drivingPath(dest, Homescreen.currentlocation);

			for(int i=0; i<temp.size(); i++)
			{
				System.out.println("ADDING STOP TO MAP: " + temp.get(i).getStopName());
				Helpers.addStops(temp.get(i),getResources().getDrawable(R.drawable.bus),mapOverlay);
			}

		}  
		// Else only started as a standard map activity
		else initializeMap(false, currentLocation);


		showOverlay();
		mc.animateTo(p);
		mc.setZoom(16);
	

		System.out.println("My loc: " + Homescreen.currentlocation.getLatitude() *1E6 + "  " + Homescreen.currentlocation.getLongitude() *1E6);
		new LocationListenerThread(context).execute();
	}	


	public void drivingPath(double []dest, Location loc)
	{
		System.out.println("dest: " + dest[0] + "  " + dest[1]);
		// Create driving path
		Location lastKnownLocation = loc;
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");//from
		urlString.append( Double.toString(lastKnownLocation.getLatitude() ));
		urlString.append(",");
		urlString.append( Double.toString(lastKnownLocation.getLongitude() ));
		urlString.append("&daddr=");//to
		urlString.append( Double.toString((double)dest[0]/1.0E6 ));
		urlString.append(",");
		urlString.append( Double.toString((double)dest[1]/1.0E6 ));
		urlString.append("&dirflg=w&hl=en&ie=UTF8&z=14&output=kml");

		try{
			// setup the url
			URL url = new URL(urlString.toString());
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();
			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// instantiate our handler
			NavigationSaxHandler navSaxHandler = new NavigationSaxHandler();
			// assign our handler
			xmlreader.setContentHandler(navSaxHandler);
			// get our data via the url class
			InputSource is = new InputSource(url.openStream());
			// perform the synchronous parse           
			xmlreader.parse(is);
			// get the results - should be a fully populated RSSFeed instance, or null on error
			NavigationDataSet ds = navSaxHandler.getParsedData();
			Log.d("MAPAPP",urlString.toString());
			// draw path
			drawPath(ds, Color.parseColor("#add331"), mapView );

			// find boundary by using itemized overlay
			GeoPoint destPoint = new GeoPoint((int)dest[0],(int)dest[1]);
			GeoPoint currentPoint = new GeoPoint( new Double(lastKnownLocation.getLatitude()*1E6).intValue()
					,new Double(lastKnownLocation.getLongitude()*1E6).intValue() );

			Drawable dot = this.getResources().getDrawable(R.drawable.icon);
			MapOverlay bgItemizedOverlay = new MapOverlay(dot,this);
			OverlayItem currentPixel = new OverlayItem(destPoint, null, null );
			OverlayItem destPixel = new OverlayItem(currentPoint, null, null );
			bgItemizedOverlay.addItem(currentPixel);
			bgItemizedOverlay.addItem(destPixel);

			// center and zoom in the map
			/*MapController mc = mapView.getController();
            mc.zoomToSpan(bgItemizedOverlay.getLatSpanE6()*2,bgItemizedOverlay.getLonSpanE6()*2);
            mc.animateTo(new GeoPoint(
                    (currentPoint.getLatitudeE6() + destPoint.getLatitudeE6()) / 2
                    , (currentPoint.getLongitudeE6() + destPoint.getLongitudeE6()) / 2));*/

		} catch(Exception e) {
			Log.d("DirectionMap","Exception parsing kml.");
			e.printStackTrace();
		}
	}

	public void drawPath(NavigationDataSet navSet, int color, MapView mMapView01) {

		//  Log.d(myapp.APP, "map color before: " + color);        

		// color correction for dining, make it darker
		if (color == Color.parseColor("#add331")) color = Color.parseColor("#6C8715");
		//     Log.d(myapp.APP, "map color after: " + color);

		Collection <Overlay> overlaysToAddAgain = new ArrayList <Overlay>();
		for (Iterator<Overlay> iter = mMapView01.getOverlays().iterator(); iter.hasNext();) {
			Object o = iter.next();
			//Log.d(myapp.APP, "overlay type: " + o.getClass().getName());
			if (!RouteOverlay.class.getName().equals(o.getClass().getName())) {
				// mMapView01.getOverlays().remove(o);
				overlaysToAddAgain.add((Overlay)o);
			}
		}
		mMapView01.getOverlays().clear();
		mMapView01.getOverlays().addAll(overlaysToAddAgain);

		String path = navSet.getRoutePlacemark().getCoordinates();
		// Log.d(myapp.APP, "path=" + path);
		if (path != null && path.trim().length() > 0) {
			String[] pairs = path.trim().split(" ");

			//   Log.d(myapp.APP, "pairs.length=" + pairs.length);

			String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height

			//Log.d(myapp.APP, "lnglat =" + lngLat + ", length: " + lngLat.length);

			if (lngLat.length<3) lngLat = pairs[1].split(","); // if first pair is not transferred completely, take seconds pair //TODO 

			try {
				GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
				mMapView01.getOverlays().add(new RouteOverlay(startGP, startGP, 1));
				GeoPoint gp1;
				GeoPoint gp2 = startGP;

				for (int i = 1; i < pairs.length; i++) // the last one would be crash
				{
					lngLat = pairs[i].split(",");

					gp1 = gp2;

					if (lngLat.length >= 2 && gp1.getLatitudeE6() > 0 && gp1.getLongitudeE6() > 0
							&& gp2.getLatitudeE6() > 0 && gp2.getLongitudeE6() > 0) {

						// for GeoPoint, first:latitude, second:longitude
						gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));

						if (gp2.getLatitudeE6() != 22200000) { 
							mMapView01.getOverlays().add(new RouteOverlay(gp1, gp2, 2, color));
							// Log.d(myapp.APP, "draw:" + gp1.getLatitudeE6() + "/" + gp1.getLongitudeE6() + " TO " + gp2.getLatitudeE6() + "/" + gp2.getLongitudeE6());
						}
					}
					// Log.d(myapp.APP,"pair:" + pairs[i]);
				}
				//routeOverlays.add(new RouteOverlay(gp2,gp2, 3));
				mMapView01.getOverlays().add(new RouteOverlay(gp2, gp2, 3));
			} catch (NumberFormatException e) {
				//  Log.e(myapp.APP, "Cannot draw route.", e);
			}
		}
		// mMapView01.getOverlays().addAll(routeOverlays); // use the default color
		mMapView01.setEnabled(true);
	}



	public void drawPath(ArrayList<Integer> id){
		ArrayList <GeoPoint> busStop = findStopInCl(id,temp);
		//	System.out.println("Found point: " + busStop);
		if(p!=null && temp!=null)
		{
			mapView.getOverlays().add(new DirectionPathOverlay(p, busStop));
			System.out.println("LUFTLINE ER POKKER MEG LAGT TIL");
		}
		else System.out.println("INGEN PUNKTER MOTHERFUCKER");
	}

	public ArrayList <GeoPoint> findStopInCl(ArrayList <Integer> id, ArrayList <ClosestStopOnMap> cl){
		ArrayList <GeoPoint> retList = new ArrayList<GeoPoint>();
		for(int i=0; i<cl.size(); i++)
		{
			for(int j=0; j<id.size(); j++)
			{
				if(cl.get(i).getBusStopID() == id.get(j))
				{
					retList.add(cl.get(i).getPoint());
					System.out.println("POINT ADDED TO LIST: " + cl.get(i).getBusStopID());
					break;
				}
			}
		}
		return retList;
	}

	public void onBackPressed()
	{
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		System.out.println("Location exited");
		finish();
	}
	@Override
	protected void onPause(){
		super.onPause();
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		System.out.println("Location paused");

	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocation.enableMyLocation();
		myLocation.enableCompass();
		// Sets the restrictions on the location update. 
		//locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 100, 1, locationListener);

	}   

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}   


	protected void showOverlay() 
	{

		List<Overlay> listOfOverlays = mapView.getOverlays();
		//    listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		listOfOverlays.add(myLocation);
		System.out.println("MYLOC ADDED");

		mapView.postInvalidate();
		System.out.println("NUM OVERLAYS: " + listOfOverlays.size());
	}


	public  void initializeMap(boolean updated, Location loc)
	{
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0)
		{

			for (Iterator <Overlay> iterator = overlays.iterator(); iterator.hasNext();)
			{
				iterator.next();
				iterator.remove();
			}
		}

		//	mapView.getOverlays().clear();
		Drawable tmp = getResources().getDrawable(R.drawable.bus);
		// If fix, and as a part of update mapview
		if(updated && myLocation.getLastFix() != null)
		{
			ClosestStopOnMap []cl = Helpers.getList(Homescreen.gpsCords2, provider, 10,1000, loc);
			mapOverlay = new MapOverlay(tmp, context,realTimeCodes, cl);        

			for(int i=0; i<cl.length; i++)
			{
				System.out.println("ADDING STOP TO MAP IF: " + Homescreen.cl[i].getStopName());
				Helpers.addStops(cl[i],getResources().getDrawable(R.drawable.bus),mapOverlay);
			}
		}
		// If no fix available, use fix from Homescreen
		else
		{
			//mapOverlay = new MapOverlay(tmp, context,realTimeCodes, Homescreen.cl);      
			ClosestStopOnMap []cl = Homescreen.cl;// Helpers.getList(Homescreen.gpsCords2, provider, 10,1000, loc);
			mapOverlay = new MapOverlay(tmp, context,realTimeCodes, cl);        

			for(int i=0; i<cl.length; i++)
			{
				System.out.println("ADDING STOP TO MAP IF: " + Homescreen.cl[i].getStopName());
				Helpers.addStops(cl[i],getResources().getDrawable(R.drawable.bus),mapOverlay);
			}
		}
		System.out.println("MYLOCFOO: " + myLocation.isMyLocationEnabled() + "   " + myLocation.getMyLocation());
		showOverlay();
		GeoPoint navigateTo = new GeoPoint( (int)(loc.getLatitude()*1E6) , (int)(loc.getLongitude() * 1E6));
		mc.animateTo(navigateTo);
		mc.setZoom(16);

		//temp.clear();

	}

	public void initializePress(ClosestStopOnMap stop)
	{
		List <Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0)
		{

			for (Iterator <Overlay> iterator = overlays.iterator(); iterator.hasNext();)
			{
				iterator.next();
				iterator.remove();
			}
		}

		//	mapView.getOverlays().clear();
		Drawable tmp = getResources().getDrawable(R.drawable.bus);
		ClosestStopOnMap [] ret = new ClosestStopOnMap[1];
		ret[0] = stop;
		//temp.clear();
		mapOverlay = new MapOverlay(tmp, context,realTimeCodes, ret);        
		Helpers.addStops(stop, getResources().getDrawable(R.drawable.bus), mapOverlay);


	}



	public class DirectionPathOverlay extends Overlay {


		private GeoPoint gp1;
		private ArrayList <GeoPoint> gp2;

		public DirectionPathOverlay(GeoPoint gp1, ArrayList <GeoPoint> gp2) {
			this.gp1 = gp1;
			this.gp2 = gp2;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection();
			if (shadow == false) {

				Paint paint = new Paint();
				paint.setAntiAlias(true);
				Point point = new Point();
				projection.toPixels(gp1, point);
				paint.setColor(Color.BLUE);
				Point []point2 = new Point[gp2.size()];
				//System.out.println("Size of point: " + point2.length);
				for(int i=0; i<point2.length; i++)
				{
					point2[i] = new Point();
					projection.toPixels(gp2.get(i), point2[i]);    	            
					paint.setStrokeWidth(2);
					canvas.drawLine((float) point.x, (float) point.y, (float) point2[i].x,
							(float) point2[i].y, paint);
				}
			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub

			super.draw(canvas, mapView, shadow);
		}

	}

	
	/*
	 * Display message continuosly if location has not been set to map
	 */
	class LocationListenerThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;    
		Intent intent;
		ProgressDialog myDialog = null;
		public LocationListenerThread(Context context)
		{

			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			try
			{
				boolean locCheck = false;
				while(!locCheck)
				{
					if(myLocation.getMyLocation() != null)
					{
						locCheck = true;
					}
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute()
		{

			try
			{
				if(myLocation.getMyLocation() == null)	Toast.makeText(context, "Venter på lokasjon på kart", Toast.LENGTH_LONG).show();
			}
			catch(Exception e)
			{
				e.printStackTrace();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			Toast.makeText(context, "Lokasjon satt!", Toast.LENGTH_LONG).show();

		}
	}  
}