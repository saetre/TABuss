package test.BusTUC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import test.BusTUC.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
 
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class BusTUCApp extends MapActivity 
{    
	MapOverlay mapOverlay;
	MapView mapView; // Google Maps
	public static String[][] gpsCords;  // Array containing bus stops
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
    GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
    GPS k_gps; // Object of the GetGPS class. 
    Location currentlocation, busLoc; // Location objects
    // Static because of access from BusList
    HashMap<Integer,Location> tSet; // HashMap used for finding closest locations
    LocationManager locationManager; // Location Manager
    HashMap<Integer,HashMap<Integer,Location>> locationsArray;
    String provider; // Provider 
    TextView myLocationText; 
    LocationListener locationListener;
    Browser k_browser;
    HashMap realTimeCodes; 
    GeoPoint[] closestBusStops; 
    Button button;
    // adds edittext box
    EditText editTe;
    StringBuffer presentation; // String which contain answer from bussTUC
    private Route [] routes; // Routes returned from bussTUC
    private Route [] finalRoutes; // Routes after real-time processing

    
    // Accelometer
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    // Devices built in sensor for detecting movement
    // Have a few bugs, need to fix later
    private final SensorEventListener mSensorListener = new SensorEventListener() 
    {

        public void onSensorChanged(SensorEvent se) 
        {
          float x = se.values[0];
          float y = se.values[1];
          float z = se.values[2];
          // Formulas found online
          mAccelLast = mAccelCurrent;
          mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
          float delta = mAccelCurrent - mAccelLast;
          mAccel = mAccel * 0.9f + delta; // perform low-cut filter
          // End of formulas found online
          
          // If movement has happened. Originally set to 5, but adjusted to a high number, to avoid uncontrolled detections
          if(mAccel > 100)
          {
        	  System.out.println("MOVEMENT!!!!!!!!!!!!!!!!!!");
        	  // Set to sentrum for testing
        	  // Plan is to let the shaking trigger a favourite set to "home"
        	editTe.setText("sentrum");
        	mAccelCurrent = 5;
        	sendToOracle();
          }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
        	
        }

		
      };

    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapView); 
        myLocationText = (TextView)findViewById(R.id.myLocationText);
        myLocationText.setText("");
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
        // Gets the coordinates from the bus XML file
        String[] gpsCoordinates = getResources().getStringArray(R.array.coords2); 
        
       /* for(int i=0; i< gpsCoordinates.length; i++)
        {
        	System.out.println("COORDINATES: " + gpsCoordinates[i]);
        }*/
        //GPS k_gps = new GPS(myImageFileEndings);
        // Formats the bus coordinates
        gpsCords = GPS.formatCoordinates(gpsCoordinates);
        for(int i=0; i< gpsCords.length; i++)
        {
        	for(int j=0; j<gpsCords[i].length; j++)
        	{
        		
        		// 0 - Busstoppnr
        		// 1 - navn
        		// 2 - lat
        		// 3 - long
        		//System.out.println("COORDINATES2 " + gpsCords[i][j]); 
        	}
        }
        // Accelometer properties
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        
        View zoomView = mapView.getZoomControls(); 
 
        zoomLayout.addView(zoomView, 
            new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);
        
        mc = mapView.getController();
        // Creates a locationManager. 
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        
        provider = locationManager.getBestProvider(criteria, true);
        k_browser = new Browser(); 
        realTimeCodes = k_browser.realTimeData();
        Iterator it = realTimeCodes.entrySet().iterator();
        System.out.println("Realtinmecodessizefirst: " + realTimeCodes.size());
      /*  while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
           // System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }*/

        Log.v("provider","provider:"+ provider);
       
        // Creates a locationListener
        locationListener = new LocationListener() {
            
        	// This method runs whenever the criteria for change is met. 
            public void onLocationChanged(Location location) {
            	currentlocation = location; 
            	initialize();
        		Log.v("currentLoc","PROV:LOC=" + currentlocation.getLatitude()+":"+currentlocation.getLongitude());
               // finds the closest bus stop
        //		busLoc = closestLoc(gpsCords);
        		// creates a HashMap containing all the location objects
                locationsArray = getLocations(gpsCords);
                //System.out.println("REALTIMEX: " + realTimeCodes.size());
                Log.v("sort","returnedHmap:"+locationsArray.size());	
                // creates a HashMap with all the relevant bus stops
                tSet = m_partialSort(locationsArray,5,500,false);
                int numberofStops = tSet.size();
                closestBusStops = new GeoPoint[numberofStops];
                Log.v("sort","returnedtSet"+tSet.size());	
                // adds the closest bus stop as a GeoPoint
                int busCounter = 0; 
                Object[] keys = tSet.keySet().toArray();
        		Arrays.sort(keys);
                for(int i = 0;i<numberofStops;i++)
                {
                   closestBusStops[i] = new GeoPoint(
                		   (int)	(tSet.get(keys[i]).getLatitude() * 1E6),
                		   (int)	(tSet.get(keys[i]).getLongitude() * 1E6));
                   addStops(closestBusStops[i]);
                		                	  
                }
                // add the current location as a GeoPoint
                p = new GeoPoint(
                        (int) (currentlocation.getLatitude() * 1E6), 
                        (int) (currentlocation.getLongitude() * 1E6));
                //Log.v("GEOPOINT","GEO2" + p.getLatitudeE6() + ":" + p.getLongitudeE6());
                addUser(p);
                showOverlay();
                /*
                List<Overlay> listOfOverlays = mapView.getOverlays();
                listOfOverlays.clear();
                listOfOverlays.add(mapOverlay);*/
                mc.animateTo(p);
                mc.setZoom(16);  
                
            }

			@Override
			public void onProviderDisabled(String provider) {
				Log.v("PROV","PROV:DISABLED");
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.v("PROV","PROV:ENABLED");
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.v("PROV","PROV:STATUSCHANGE");
				
			}
        };
        
       
       // adds button
       button = (Button) findViewById(R.id.Button);
       // adds edittext box
       editTe = (EditText) findViewById(R.id.eText);
       // binds listener to the button
       button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
    	    	new OracleThread(getApplicationContext()).execute();
            }
        });
    }
    
   
    

    public void computeRealTime()
    {
    	Calculate calculator = new Calculate();
    	int tempId = 0; 
      //  Log.v("routesl","length:"+routes.length);
       presentation = new StringBuffer();
        
        boolean noTransfer = true; 
        //System.out.println("Length of finalRoutes: " + finalRoutes.length);
        for(int i = 0;i<finalRoutes.length;i++)
        {
      	 // System.out.println("FANT BUSSTOPP: " +finalRoutes[i].getBusStopNumber());
 //       	System.out.println("Realtimecodes: " + realTimeCodes.size());
      	  tempId = Integer.parseInt(realTimeCodes.get(finalRoutes[i].getBusStopNumber()).toString());
      	  int wantedLine = finalRoutes[i].getBusNumber();
      	  BusStops nextBus = k_browser.specificRequest(tempId,wantedLine); 
      	  if(nextBus.getArrivalTime() == null)
      	  {
      		   
      	  }
      	  else{
      		  finalRoutes[i].setArrivalTime(nextBus.getArrivalTime().getHours()+""+String.format("%02d",nextBus.getArrivalTime().getMinutes())+"");
      	  }
      	  int k_totalTime = calculator.calculateTotalTime(finalRoutes[i].getArrivalTime(), finalRoutes[i].getTravelTime());
      	  finalRoutes[i].setTotalTime(k_totalTime);
        }
        calculator.printOutRoutes("AFTERREALTIME",finalRoutes, true);
        Route[] printRoute = calculator.sortByTotalTime(finalRoutes);
        for(int i = 0;i<finalRoutes.length;i++)
        {
      //	 int intBusStopNumber = printRoute[i].getBusStopNumber(); 
     // 	 String strBusStopNumber = String.valueOf(intBusStopNumber);
     // 	 int newBSN = Integer.parseInt(strBusStopNumber.substring(strBusStopNumber.length()-3));
      	 
      	 if(noTransfer)
      	 {
   //   		Object[] keys = locationsArray.get(newBSN).keySet().toArray();
        	    presentation.append((i+1)+": Ta Buss "+printRoute[i].getBusNumber()+" fra "+printRoute[i].getBusStopName()+" ("+printRoute[i].getWalkingDistance()+" meter)"+" klokken "+printRoute[i].getArrivalTime()+". Du vil n� "+printRoute[i].getDestination()+" ca "+printRoute[i].getTravelTime()+ " minutter senere.\n");
        	    if(routes[i].isTransfer())
        	    {
        	    	noTransfer = false;
        	    }
      	 }
      	 else 
      	 {
         	  presentation.append((i+1)+": Ta Buss "+printRoute[i].getBusNumber()+" fra "+printRoute[i].getBusStopName()+" klokken "+printRoute[i].getArrivalTime()+". Du vil n� "+printRoute[i].getDestination()+" ca "+printRoute[i].getTravelTime()+ " minutter senere.\n");
      	 }
         
        }
        Object[] keys = tSet.keySet().toArray();
        for(Object key : keys)
        {
        	Log.v("Keys","Key:"+Double.parseDouble(key.toString()));
        	Log.v("Value","Value:"+tSet.get(key).getProvider());
        }
                          
		  //Log.v("wantedbus","wantedbus:"+wantedBusStop);
  //	  int newId = Integer.parseInt(realTimeCodes.get(wantedBusStop).toString());
      //  myLocationText.setText(presentation.toString());
        
    
    	
    }
    public boolean sendToOracle()
    {
    	// Perform action on clicks
    	if(!tSet.isEmpty())
  	     {
    	 // System.out.println("K-browserobj " + k_browser.toString() + "realtimelength: " + realTimeCodes.size()); 
          String[] html_page = k_browser.getRequest(tSet,editTe.getText().toString(),false);   
          //System.out.println("TEKST: " + editTe.getText().toString() );
          //System.out.println("HTML LENGTH: " + html_page.length); 
          StringBuilder str = new StringBuilder(); 
          // Parses the returned html
          for(int i = 0;i<html_page.length;i++) 
          {
	          Log.v("CONTENT"+i, html_page[i]); 
	          if(!html_page[i].contains("</body>"))
	          {
	          str.append(html_page[i] + "\n"); 
	          }
	          // Simple error handling. If the object contains "error", return false
	          if(html_page[0].equalsIgnoreCase("error"))
	          {
	        	 // Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
	        	  System.out.println("NOT FOUND ERROR FOO");
	        	  return false;
	          }
          }
          
          int indexOf = str.lastIndexOf("}");
          String jsonSubString = str.substring(0, indexOf+1); 
          jsonSubString = jsonSubString.replaceAll("\\}", "},");
          jsonSubString = jsonSubString.substring(0, jsonSubString.length()-1);
          Log.v("manipulatedString","New JSON:"+jsonSubString);
          int wantedBusStop = 0;  
          Calculate calculator = new Calculate(); 
          
          
          // Create routes based on jsonSubString
          routes = calculator.createRoutes(jsonSubString);
  
          for(int i = 0;i<routes.length;i++)
          {
        	 int intBusStopNumber = routes[i].getBusStopNumber(); 
         	 String strBusStopNumber = String.valueOf(intBusStopNumber);
         	//	System.out.println("strBusStopNumber: " + strBusStopNumber);
         	 int newBSN = Integer.parseInt(strBusStopNumber.substring(strBusStopNumber.length()-3));
         	//System.out.println("newBSN: " + newBSN);
         	 
         	 
         	 if(locationsArray.containsKey(newBSN))
         	 {
         		Object[] keys = locationsArray.get(newBSN).keySet().toArray();
         		routes[i].setWalkingDistance(Integer.parseInt(keys[0].toString()));
         	 }
         	 else
         	 {         		 
          		routes[i].setWalkingDistance(-1); 
         	 }
          }
          calculator.printOutRoutes("BEFORE",routes, false);
          finalRoutes = calculator.suggestRoutes(routes);
          calculator.printOutRoutes("AFTER",finalRoutes, false);
          return true;
  	     }
        return true;

    }
    @SuppressWarnings("static-access")
	@Override
	protected void onResume() {
		super.onResume();
	    // Sets the restrictions on the location update. 
		locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 100, 1, locationListener);
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

	}
    
    @Override
    protected void onStop()
    {
      mSensorManager.unregisterListener(mSensorListener);
      super.onStop();
    }


    // Creates the HashMap for the locations. 
    public HashMap<Integer,HashMap<Integer,Location>> getLocations(String[][] k_gpsCords)
    {
    	String tempCords[][] = k_gpsCords; 
        int clength = tempCords.length; 
        Log.v("CORDL", "C:"+clength); 
        
        HashMap<Integer,HashMap<Integer,Location>> newMap = new HashMap<Integer,HashMap<Integer,Location>>();
        Location closestLocation[] = new Location[clength]; 
        HashMap<Integer,Integer> counter = new HashMap<Integer,Integer>();
        for(int i = 0;i<clength;i++)
        {
     	   closestLocation[i] = new Location(provider);
     	   closestLocation[i].setProvider(tempCords[i][1]); 
     	   closestLocation[i].setLatitude(Double.parseDouble(tempCords[i][3])); 
     	   closestLocation[i].setLongitude(Double.parseDouble(tempCords[i][2]));
     	   int distance = (int)closestLocation[i].distanceTo(currentlocation);
     	   HashMap<Integer, Location> hMap = new HashMap<Integer,Location>(); 
     	   hMap.put(distance, closestLocation[i]);
     	   String busStopId = tempCords[i][0]; 
     	   int newID = Integer.parseInt(busStopId.substring(busStopId.length()-3));
   //  	   Log.v("newId","newID:"+newID);    	   
     	   if(counter.containsKey(newID))
     	   {
     		   counter.put(newID, counter.get(newID)+1);
     		   Log.v("SAME","SAMEID:"+newID);
     	   }else { counter.put(newID,1); }
     	   newMap.put(newID, hMap);
             
        } 
        return newMap; 
    }
    // Finds either N closest bus stops or the bus stops within a radius M
    public HashMap<Integer,Location> m_partialSort(HashMap<Integer,HashMap<Integer,Location>> lHMap, int i, int m, boolean maxLoc)
    {
    	HashMap<Integer,HashMap<Integer,Location>> newMap = lHMap;
    	Log.v("sort","lHMap:"+lHMap.size());
    	HashMap<Integer,Location> finalMap = new HashMap<Integer,Location>();
    	TreeSet<Integer> minValues = new TreeSet<Integer>();
    	Object[] keys = newMap.keySet().toArray();
    	for(int y = 0;y<newMap.size();y++)
    	{
    		int currentValue = Integer.parseInt(newMap.get(keys[y]).keySet().toArray()[0].toString());
    		if(minValues.size() < i && maxLoc && currentValue < m)
    		{
    		   minValues.add(currentValue);
    		   finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
    		}
    		else if(!maxLoc && currentValue < m && i != 0)
    		{
    			System.out.println("MAXLOC = FALSE ");
    			minValues.add(currentValue); 
    			finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue)); 
    		}
    		else if(maxLoc)
    		{
    			if(currentValue < minValues.last())
    			{
    				minValues.remove(minValues.last());
    				finalMap.remove(minValues.last());
    				minValues.add(currentValue); 
    				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
    			}
    		}
    	}
    	if(i != 0 && m != 0)
    	{
    		Object[] newkeys = finalMap.keySet().toArray();
    		Arrays.sort(newkeys);
    		for(int k = finalMap.size()-1;k>=i;k--)
    		{
    			finalMap.remove(newkeys[k]);
    		}
    	}
    	Iterator<Integer> iter = minValues.iterator();
    	while(iter.hasNext())
    	{
    	   int next = iter.next();
    	   Log.v("sort","TreeValue"+next);	
    	 //  Log.v("finalmap","finalmap="+finalMap.get(next).getProvider()+":"+next);
    	}
    	System.out.println("FINAL MAP: " + finalMap.size()); 
    	return finalMap; 
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    // Method used to find the coordinates to the current location
    public String[] showLocation(Location location) {
			
    	String[] currentLocation = new String[2]; 
 
			if (location != null) {
				String lat = Double.toString(location.getLatitude());
				String lng = Double.toString(location.getLongitude());
				Log.v("LAT", "latitude:"+lat);
				Log.v("Longitude", "longitude:"+lng);
				currentLocation[0] = lat;
				currentLocation[1] = lng;
				Log.v("GPS", currentLocation[0]+":"+currentLocation[1]);
			} else {
				currentLocation[0] = "-1";
				currentLocation[1] = "-1";
				Log.v("LOC","Location not available.");
			}
			return currentLocation;
	} 
    protected void addStops(GeoPoint loc) 
    {
    	Drawable icon = getResources().getDrawable(R.drawable.s_busstop2);
    	icon.setBounds(0,0, 15, 15);
    	OverlayItem item = new OverlayItem(loc, "", null);	
    	
		item.setMarker(icon);
		mapOverlay.addItem(item);
		System.out.println("ADDING STOP: " + mapOverlay.size());
        
    }
    
    protected void addUser(GeoPoint loc) 
    {
    	Drawable icon = getResources().getDrawable(R.drawable.pp);
    	OverlayItem item = new OverlayItem(loc, "", null);	
    	icon.setBounds(0,0, 32, 37);
		item.setMarker(icon);
		mapOverlay.addItem(item);
		System.out.println("NUM ITEMS:  " + mapOverlay.size());
       
    }
    
    protected void showOverlay() 
    {
    	
        List<Overlay> listOfOverlays = mapView.getOverlays();
    //    listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);
        mapView.postInvalidate();
        System.out.println("NUM OVERLAYS: " + listOfOverlays.size());
    }
    
    public void initialize()
    {
    	List overlays = mapView.getOverlays();
    	 
    	// first remove old overlay
		if (overlays.size() > 0)
		{

			for (Iterator iterator = overlays.iterator(); iterator.hasNext();)
			{
				iterator.next();
				iterator.remove();
			}
		}

    //	mapView.getOverlays().clear();
        Drawable tmp = getResources().getDrawable(R.drawable.s_busstop2);
        mapOverlay = new MapOverlay(tmp, this);        
       
        //
    }
    
    
    // Menu properties
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.favoritt:
        	Intent intent = new Intent(this, Favourite_Act.class);
            startActivityForResult(intent, 0); // 0, not used
            return true;
        // Add other menu items
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    // Result returned from child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	//	System.out.println("ACTIVITY RESULT RECIEVED!!! ");
    	if(!data.getStringExtra("test").isEmpty())
    	{
    		String item = data.getStringExtra("test");
	    	editTe.setText(item);
	    	//System.out.println("SET TO: " + item);
	    	Toast.makeText(this, editTe.getText().toString(), Toast.LENGTH_LONG).show();
	    	new OracleThread(getApplicationContext()).execute();
    	}
    }
    
    
    
    // Thread classes //
    // Thread starting the oracle queries
    class OracleThread extends AsyncTask<Void, Void, Void>
    {
        private Context context;    
        boolean check = false;

        public OracleThread(Context context)
        {
        	
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
        	long time = System.nanoTime();
        	if(sendToOracle()) check = true;        	
        	Long newTime = System.nanoTime() - time;
			System.out.println("TIME ORACLE: " +  newTime/1000000000.0);
        	
            return null;
        }
        
        @Override
        protected void onPreExecute()
        {
        	editTe.setEnabled(false);
        	button.setEnabled(false);
        }

        @Override
       protected void onPostExecute(Void unused)
        {
        	// Start real-time computing
        	if(check)
        	{
        		long time = System.nanoTime();
        		computeRealTime();
            	Long newTime = System.nanoTime() - time;
    			System.out.println("TIME RealTime: " +  newTime/1000000000.0);
        		myLocationText.setText(presentation.toString());
        	}
        	else
        	{
        		editTe.setEnabled(true);
             	button.setEnabled(true);
             	
        	}

        }
    }
   
    // The class which draws on the map
    /*class MapOverlay extends com.google.android.maps.Overlay
    {	    	new OracleThread(getApplicationContext()).execute();


        @Override
        public boolean draw(Canvas canvas, MapView mapView,boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);     
            Point startPts = new Point(0,0);
         //   Point busPts = new Point(0,0);
            try
            {
            Bitmap bmpBus = BitmapFactory.decodeResource(
                        getResources(), R.drawable.s_busstop2);  
            // translate the GeoPoint to screen pixels
            if(closestBusStops != null)
            {
            	int counter = closestBusStops.length;
            	Point[] bussPts = new Point[counter];
            	for(int i = 0;i<counter;i++)
            	{
            		bussPts[i] = new Point(0,0);
            		mapView.getProjection().toPixels(closestBusStops[i], bussPts[i]);
            		canvas.drawBitmap(bmpBus, bussPts[i].x, bussPts[i].y-50, null); 
            	}
            }
            startPts = new Point();
            // Adds the current location
            mapView.getProjection().toPixels(p, startPts);
      //      busPts = new Point();
            // Adds the closest bus stop
     //       mapView.getProjection().toPixels(p2, busPts);
            } catch (Exception e)
            { Log.v("DRAW", "MapView getProjection:"+e.toString()); }
 
            // add the marker
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pp);            
            canvas.drawBitmap(bmp, startPts.x, startPts.y-50, null);   
                
            return true;
        }
       
    }	*/
  
}