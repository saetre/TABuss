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
 * - To compile:	javac -d . -cp "httpcomponents-client-4.2-beta1/lib/httpmime-4.2-beta1.jar:android.jar:." -encoding UTF-8 src/test/BusTUC/*.java
 * - To run:		java  -cp  .:httpcomponents-client-4.2-beta1/lib/httpmime-4.2-beta1.jar test/BusTUC/Main/BusTUCApp
 * 
 */

package org.ubicompforall.BusTUC.Main;

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

import org.ubicompforall.BusTUC.Main.Homescreen;
import org.ubicompforall.BusTUC.R;
import org.ubicompforall.BusTUC.Favourites.SDCard;
import org.ubicompforall.BusTUC.GPS.GPS;
import org.ubicompforall.BusTUC.Path.NavigationDataSet;
import org.ubicompforall.BusTUC.Path.NavigationSaxHandler;
import org.ubicompforall.BusTUC.Path.RouteOverlay;
import org.ubicompforall.BusTUC.Queries.Browser;
import org.ubicompforall.BusTUC.Stops.BusStop;
import org.ubicompforall.BusTUC.Stops.ClosestStopOnMap;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class BusTUCApp extends MapActivity
{

	public static final int DEBUG = 1;
	public static final String BusTUC_Label = "BusTUC";

	MapOverlay mapOverlay;
	ArrayList<ClosestStopOnMap> temp;
	ArrayList<Integer> id;
	MapView mapView; // Google Maps
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
	GeoPoint p, p2; // p is current location, p2 is closest bus stop.
	GPS k_gps; // Object of the GetGPS class.

	String provider; // Provider
	HashMap<Integer, Integer> realTimeCodes;
	MyLocationOverlay myLocation;
	Context context;
	boolean fromExtras = false;
	boolean server = true;
	Location currentLocation;
	Browser k_browser;

	/** Called when the activity is first created. */

	// STATIC METHODS

	/*
	 * Debug method to include the filename, line-number and method of the
	 * caller
	 */
	public static void debug(int d, String msg)
	{
		if (DEBUG >= d)
		{
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 2;
			while (st[stackLevel].getMethodName().equals("debug")
					|| st[stackLevel].getMethodName().equals("access$0"))
			{
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			Log.d(BusTUC_Label,
					e.getMethodName() + ": " + msg + " at (" + e.getFileName()
							+ ":" + e.getLineNumber() + ")");
		} // if DEBUG verbosity level is high enough
	} // debug

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setStreetView(true);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
		context = this;
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		View zoomView = mapView.getZoomControls();

		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		mapView.displayZoomControls(true);

		mc = mapView.getController();

		try
		{
			currentLocation = Homescreen.currentlocation;

			if (currentLocation == null)
			{
				try
				{
					Toast.makeText(context,
							"Mangler informasjon, returnerer til hjemmeskjerm",
							Toast.LENGTH_LONG).show();
					returnHome();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} catch (Exception e)
		{
			Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
			ArrayList<String> err = new ArrayList<String>();
			err.add(e.toString());
			SDCard.generateNoteOnSD("errorBusTUCAPPREALTIME", err, "errors");
			this.finish();

		}

		final Bundle extras = getIntent().getExtras();
		// add the current location as a GeoPoint
		p = new GeoPoint((int) (currentLocation.getLatitude() * 1E6),
				(int) (currentLocation.getLongitude() * 1E6));

		myLocation = new MyLocationOverlay(context, mapView)
		{
			@Override
			public void onLocationChanged(Location loc)
			{
				super.onLocationChanged(loc);

				System.out.println("LOCATIONCHANGE IN MAP");
				// Update loc if new loc is more than 10 metres in air dist from
				// last loc
				System.out.println("DIFF LEN: "
						+ loc.distanceTo(currentLocation));
				if (extras == null)
				{
					try
					{
						// Toast.makeText(context, "Oppdaterer kart",
						// Toast.LENGTH_SHORT).show();
						initializeMap(true, loc);
						// Toast.makeText(context, "Kart oppdatert",
						// Toast.LENGTH_SHORT).show();
						mc.animateTo(myLocation.getMyLocation());
					} catch (Exception e)
					{
						Toast.makeText(context, "Klarte ikke oppdatere kart",
								Toast.LENGTH_LONG).show();

						e.printStackTrace();
					}
					// new UpdateMapThread(context, loc).execute();
				} else
				{
					mc.animateTo(myLocation.getMyLocation());
					mapView.postInvalidate();

				}

				currentLocation = loc;

			}
		};
		System.out.println("Enabling location");
		if (!myLocation.isMyLocationEnabled())
			System.out.println("LOCATION NOT ENABLED");
		if (!myLocation.enableMyLocation())
			System.out.println("COULD NOT ENABLE LOCATION");
		if (!myLocation.isMyLocationEnabled())
			System.out.println("LOCATION STILL NOT ENABLED");
		if (!myLocation.enableCompass())
			System.out.println("COULD NO ENABLE PROVIDERS");

		// If extras != null -> Activity started based on query answer.
		// Need to extract info
		if (extras != null)
		{
			temp = new ArrayList<ClosestStopOnMap>();
			id = new ArrayList<Integer>();
			fromExtras = true;
			ArrayList<Route> foundRoutes = new ArrayList<Route>();
			String value = "";
			double[] dest = new double[2];
			// Extras will now contain an ArrayList<Route>
			foundRoutes = extras.getParcelableArrayList("test");
			int position = extras.getInt("pos");
			ClosestStopOnMap buf = new ClosestStopOnMap();
			// value = extras.getString("test");
			// Iterate through the closest stop, and match bus stop id
			ArrayList<BusStop> allStops = Homescreen.allStops;
			if (allStops == null)
			{
				returnHome();
			} else
			{
				try
				{
					for (int k = 0; k < Homescreen.allStops.size(); k++)
					{
						if (foundRoutes.get(position).getBusStopNumber() == Homescreen.allStops
								.get(k).stopID)
						{
							System.out.println("FOUND TRANSFER ID: "
									+ allStops.get(k).stopID);
							int latitude = (int) (allStops.get(k).location
									.getLatitude() * 1E6);
							int longitude = (int) (allStops.get(k).location
									.getLongitude() * 1E6);
							buf = new ClosestStopOnMap(new GeoPoint(latitude,
									longitude), allStops.get(k).stopID,
									allStops.get(k).name);
							temp.add(buf);

							id.add(Homescreen.allStops.get(k).stopID);
							dest[0] = allStops.get(k).location.getLatitude() * 1E6;
							dest[1] = allStops.get(k).location.getLongitude() * 1E6;
							break;
						}

					}

					// Initialise mapOverlay, and add items
					initializePress(buf);

					// Draw air dist. Commented now, as
					// Walking dist covers our needs. Uncomment to add.
					if (!id.isEmpty())
					{
						// drawPath(id);
					}

					drivingPath(dest, Homescreen.currentlocation);

					for (int i = 0; i < temp.size(); i++)
					{
						System.out.println("ADDING STOP TO MAP: "
								+ temp.get(i).getStopName());
						Helpers.addStops(temp.get(i), getResources()
								.getDrawable(R.drawable.bus), mapOverlay);
					}

				}

				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}
		// Else only started as a standard map activity
		else
			initializeMap(false, currentLocation);

		mc.animateTo(p);
		mc.setZoom(16);

		new LocationListenerThread(context).execute();
	}

	public void drivingPath(double[] dest, Location loc)
	{
		System.out.println("dest: " + dest[0] + "  " + dest[1]);
		// Create driving path
		Location lastKnownLocation = loc;
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append(Double.toString(lastKnownLocation.getLatitude()));
		urlString.append(",");
		urlString.append(Double.toString(lastKnownLocation.getLongitude()));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString(dest[0] / 1.0E6));
		urlString.append(",");
		urlString.append(Double.toString(dest[1] / 1.0E6));
		urlString.append("&dirflg=w&hl=en&ie=UTF8&z=14&output=kml");

		try
		{
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
			// get the results - should be a fully populated RSSFeed instance,
			// or null on error
			NavigationDataSet ds = navSaxHandler.getParsedData();
			Log.d("MAPAPP", urlString.toString());
			// draw path
			drawPath(ds, Color.parseColor("#add331"), mapView);

		} catch (Exception e)
		{
			// e.printStackTrace();
		}
	}

	public void drawPath(NavigationDataSet navSet, int color, MapView mMapView01)
	{

		if (color == Color.parseColor("#add331"))
			color = Color.parseColor("#6C8715");

		Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
		for (Iterator<Overlay> iter = mMapView01.getOverlays().iterator(); iter
				.hasNext();)
		{
			Object o = iter.next();
			if (!RouteOverlay.class.getName().equals(o.getClass().getName()))
			{
				overlaysToAddAgain.add((Overlay) o);
			}
		}
		mMapView01.getOverlays().clear();
		mMapView01.getOverlays().addAll(overlaysToAddAgain);

		String path = navSet.getRoutePlacemark().getCoordinates();
		if (path != null && path.trim().length() > 0)
		{
			String[] pairs = path.trim().split(" ");
			String[] lngLat = pairs[0].split(","); 
			if (lngLat.length < 3)
				lngLat = pairs[1].split(","); 

			try
			{
				GeoPoint startGP = new GeoPoint(
						(int) (Double.parseDouble(lngLat[1]) * 1E6),
						(int) (Double.parseDouble(lngLat[0]) * 1E6));
				mMapView01.getOverlays().add(
						new RouteOverlay(startGP, startGP, 1));
				GeoPoint gp1;
				GeoPoint gp2 = startGP;

				for (int i = 1; i < pairs.length; i++) // the last one would be
														// crash
				{
					lngLat = pairs[i].split(",");

					gp1 = gp2;

					if (lngLat.length >= 2 && gp1.getLatitudeE6() > 0
							&& gp1.getLongitudeE6() > 0
							&& gp2.getLatitudeE6() > 0
							&& gp2.getLongitudeE6() > 0)
					{

						// for GeoPoint, first:latitude, second:longitude
						gp2 = new GeoPoint(
								(int) (Double.parseDouble(lngLat[1]) * 1E6),
								(int) (Double.parseDouble(lngLat[0]) * 1E6));

						if (gp2.getLatitudeE6() != 22200000)
						{
							mMapView01.getOverlays().add(
									new RouteOverlay(gp1, gp2, 2, color));
						
						}
					}
				}
				mMapView01.getOverlays().add(new RouteOverlay(gp2, gp2, 3));
			} catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		mMapView01.setEnabled(true);
	}

	public void drawPath(ArrayList<Integer> id)
	{
		ArrayList<GeoPoint> busStop = findStopInCl(id, temp);
		if (p != null && temp != null)
		{
			mapView.getOverlays().add(new DirectionPathOverlay(p, busStop));
			System.out.println("LUFTLINE ER POKKER MEG LAGT TIL");
		} else
			System.out.println("INGEN PUNKTER MOTHERFUCKER");
	}

	public ArrayList<GeoPoint> findStopInCl(ArrayList<Integer> id,
			ArrayList<ClosestStopOnMap> cl)
	{
		ArrayList<GeoPoint> retList = new ArrayList<GeoPoint>();
		for (int i = 0; i < cl.size(); i++)
		{
			for (int j = 0; j < id.size(); j++)
			{
				if (cl.get(i).getBusStopID() == id.get(j))
				{
					retList.add(cl.get(i).getPoint());
					System.out.println("POINT ADDED TO LIST: "
							+ cl.get(i).getBusStopID());
					break;
				}
			}
		}
		return retList;
	}

	@Override
	public void onBackPressed()
	{
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		System.out.println("Location exited");
		finish();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		myLocation.disableCompass();
		myLocation.disableMyLocation();
		System.out.println("Location paused");

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		myLocation.enableMyLocation();
		myLocation.enableCompass();
		// Sets the restrictions on the location update.
		// locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
		// 100, 1, locationListener);

	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	protected void showOverlay()
	{

		List<Overlay> listOfOverlays = mapView.getOverlays();
		// listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		listOfOverlays.add(myLocation);
		System.out.println("MYLOC ADDED");

		mapView.postInvalidate();
		System.out.println("NUM OVERLAYS: " + listOfOverlays.size());
	}

	public void initializeMap(boolean updated, Location loc)
	{
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0)
		{

			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();)
			{
				iterator.next();
				iterator.remove();
			}
		}

		// mapView.getOverlays().clear();
		Drawable tmp = getResources().getDrawable(R.drawable.bus);
		// If fix, and as a part of update mapview
		try
		{
			if (updated && myLocation.getLastFix() != null)
			{
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				String foo2 = preferences.getString("num2", "");
				int numStopsOnMap = Integer.parseInt(foo2);
				String foo3 = preferences.getString("num3", "");
				int radius = Integer.parseInt(foo3);
				ClosestStopOnMap[] cl = Helpers.getList(Homescreen.gpsCords2,
						provider, numStopsOnMap, radius, loc);
				if (cl == null)
				{
					returnHome();
				}

				else
				{
					mapOverlay = new MapOverlay(tmp, context, cl);

					for (int i = 0; i < cl.length; i++)
					{
						System.out.println("ADDING STOP TO MAP IF: "
								+ Homescreen.cl[i].getStopName());
						Helpers.addStops(cl[i],
								getResources().getDrawable(R.drawable.bus),
								mapOverlay);
					}
				}
			}
			// If no fix available, use fix from Homescreen
			else
			{
				// mapOverlay = new MapOverlay(tmp, context,realTimeCodes,
				// Homescreen.cl);
				ClosestStopOnMap[] cl = Homescreen.cl;// Helpers.getList(Homescreen.gpsCords2,
														// provider, 10,1000,
														// loc);
				if (cl == null)
				{
					returnHome();
				} else
				{
					mapOverlay = new MapOverlay(tmp, context, cl);

					for (int i = 0; i < cl.length; i++)
					{
						System.out.println("ADDING STOP TO MAP IF: "
								+ Homescreen.cl[i].getStopName());
						Helpers.addStops(cl[i],
								getResources().getDrawable(R.drawable.bus),
								mapOverlay);
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("MYLOCFOO: " + myLocation.isMyLocationEnabled()
				+ "   " + myLocation.getMyLocation());
		showOverlay();
		GeoPoint navigateTo = new GeoPoint((int) (loc.getLatitude() * 1E6),
				(int) (loc.getLongitude() * 1E6));
		mc.animateTo(navigateTo);
		mc.setZoom(16);

		// temp.clear();

	}

	public void returnHome()
	{
		Intent intent = new Intent(context, Homescreen.class);
		context.startActivity(intent);
	}

	public void initializePress(ClosestStopOnMap stop)
	{
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0)
		{

			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();)
			{
				iterator.next();
				iterator.remove();
			}
		}

		// mapView.getOverlays().clear();
		Drawable tmp = getResources().getDrawable(R.drawable.bus);
		ClosestStopOnMap[] ret = new ClosestStopOnMap[1];
		ret[0] = stop;
		// temp.clear();
		mapOverlay = new MapOverlay(tmp, context, ret);
		Helpers.addStops(stop, getResources().getDrawable(R.drawable.bus),
				mapOverlay);
		showOverlay();

	}

	public class DirectionPathOverlay extends Overlay
	{

		private GeoPoint gp1;
		private ArrayList<GeoPoint> gp2;

		public DirectionPathOverlay(GeoPoint gp1, ArrayList<GeoPoint> gp2)
		{
			this.gp1 = gp1;
			this.gp2 = gp2;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when)
		{
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection();
			if (shadow == false)
			{

				Paint paint = new Paint();
				paint.setAntiAlias(true);
				Point point = new Point();
				projection.toPixels(gp1, point);
				paint.setColor(Color.BLUE);
				Point[] point2 = new Point[gp2.size()];
				// System.out.println("Size of point: " + point2.length);
				for (int i = 0; i < point2.length; i++)
				{
					point2[i] = new Point();
					projection.toPixels(gp2.get(i), point2[i]);
					paint.setStrokeWidth(2);
					canvas.drawLine(point.x, point.y, point2[i].x, point2[i].y,
							paint);
				}
			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow)
		{
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
				while (!locCheck)
				{
					if (myLocation.getMyLocation() != null)
					{
						locCheck = true;
					}
				}

			} catch (Exception e)
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
				if (myLocation.getMyLocation() == null)
					Toast.makeText(context, "Venter på lokasjon på kart",
							Toast.LENGTH_SHORT).show();
			} catch (Exception e)
			{
				e.printStackTrace();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			Toast.makeText(context, "Lokasjon satt!", Toast.LENGTH_SHORT)
					.show();

		}
	}
}