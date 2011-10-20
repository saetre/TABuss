package test.BusTUC.Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import test.BusTUC.R;
import test.BusTUC.Calc.Calculate;
import test.BusTUC.Calc.Sort;
import test.BusTUC.Favourites.Favourite_Act;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Queries.Browser;

import test.BusTUC.Stops.ClosestHolder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
 
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	String[][] gpsCords;  // Array containing bus stops
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
    GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
    GPS k_gps; // Object of the GetGPS class. 
    Location currentlocation, busLoc; // Location objects
    // Static because of access from BusList
    HashMap<Integer,Location> tSetExclude; // HashMap used for finding closest locations. Ignores stops at both sides of the road
    HashMap<Integer,Location> tSetAllStops; // HashMap used for finding closest locations. Adds stops from both sides of the road. For use on map w
    LocationManager locationManager; // Location Manager
    HashMap<Integer,HashMap<Integer,Location>> locationsArray;
    String provider; // Provider 
    //TextView myLocationText; 
    LocationListener locationListener;
    Browser k_browser; 
    HashMap realTimeCodes; 
    ClosestHolder [] cl; // Object containing geopoint of closest stops. 
    //Button button;
    // adds edittext box
   // EditText editTe;
    StringBuffer presentation; // String which contain answer from bussTUC
    private Route [] routes; // Routes returned from bussTUC
    private Route [] finalRoutes; // Routes after real-time processing
    
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapView); 
     //   myLocationText = (TextView)findViewById(R.id.myLocationText);
       // myLocationText.setText("");
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
        // Gets the coordinates from the bus XML file
        String[] gpsCoordinates = getResources().getStringArray(R.array.coords3); 
        
        
		// Test database stuff
        /*Database myDb = new Database(this);
        SQLiteDatabase db = myDb.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT destination FROM test", new String[]{""+1});
        System.out.println("Found in db: " + cursor.getCount());*/
        // End database stuff
        
        
        // Formats the bus coordinates
    	// 1 - navn
		// 2 - lat
		// 3 - long
        gpsCords = GPS.formatCoordinates(gpsCoordinates);
       
        		
        // 0 - Busstoppnr
        	
        //System.out.println("COORDINATES2 " + gpsCords[i][j]); 
    
        View zoomView = mapView.getZoomControls(); 
 
        zoomLayout.addView(zoomView, 
            new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);
        
        mc = mapView.getController();
        // Creates a locationManager. 
        // If no connection, quit
        try
        {
	        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	        provider = locationManager.getBestProvider(criteria, true);
	        k_browser = new Browser(); 
	        // Load real-time codes
	        realTimeCodes = k_browser.realTimeData();
	        System.out.println("Realtinmecodessizefirst: " + realTimeCodes.size());
	            Log.v("provider","provider:"+ provider);
        }
        catch(Exception e)
        {
        	//Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
            System.exit(0);
        	
        }       
        
       
       
        // Creates a locationListener
        locationListener = new LocationListener() {
            
        	// This method runs whenever the criteria for change is met. 
            public void onLocationChanged(Location location) {
            	currentlocation = location; 
            	
        		Log.v("currentLoc","PROV:LOC=" + currentlocation.getLatitude()+":"+currentlocation.getLongitude());
               // finds the closest bus stop
        //		busLoc = closestLoc(gpsCords);
        		// creates a HashMap containing all the location objects
                locationsArray = Helpers.getLocations(gpsCords,provider, currentlocation);
                //System.out.println("REALTIMEX: " + realTimeCodes.size());
                Log.v("sort","returnedHmap:"+locationsArray.size());	
                // creates a HashMap with all the relevant bus stops
                Sort sort = new Sort();
                tSetExclude = sort.m_partialSort(locationsArray,5,500,false, false);
                tSetAllStops = sort.m_partialSort(locationsArray,10,1000,false, true);
                int numberofStops = tSetAllStops.size();
                cl = new ClosestHolder[numberofStops];
                
                Log.v("sort","returnedtSet"+tSetExclude.size());	
                // adds the closest bus stop as a GeoPoint
                int busCounter = 0; 
                Object[] keys = tSetAllStops.keySet().toArray();
        		Arrays.sort(keys);
                for(int i = 0;i<numberofStops;i++)
                {
                  cl[i] = new ClosestHolder(new GeoPoint(
               		   (int)	(tSetAllStops.get(keys[i]).getLatitude() * 1E6),
               		   (int)	(tSetAllStops.get(keys[i]).getLongitude() * 1E6)),
               		   (int) tSetAllStops.get(keys[i]).getAltitude(),
               		   tSetAllStops.get(keys[i]).getProvider());
                  
                 // System.out.println("ADDING: " +(int) tSet.get(keys[i]).getAltitude());   
                		                	  
                }
                initialize();
                for(int i=0; i<cl.length; i++)
                {
                	Helpers.addStops(cl[i],getResources().getDrawable(R.drawable.s_busstop2),mapOverlay);
                }
                
                // add the current location as a GeoPoint
                p = new GeoPoint(
                        (int) (currentlocation.getLatitude() * 1E6), 
                        (int) (currentlocation.getLongitude() * 1E6));

               Helpers.addUser(p,mapOverlay, getResources().getDrawable(R.drawable.pp));
               System.out.println("My loc: " + currentlocation.getLatitude() *1E6 + "  " + currentlocation.getLongitude() *1E6);
                showOverlay();
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
//       button = (Button) findViewById(R.id.Button);
       // adds edittext box
  //     editTe = (EditText) findViewById(R.id.eText);
       // binds listener to the button
    //   button.setOnClickListener(new OnClickListener() {
           /* public void onClick(View v) {
    	    	new OracleThread(getApplicationContext()).execute();
            }
        });*/
    }
    
    public void drawPath(int stopid){
    	GeoPoint busStop = findStopInCl(stopid, cl);
    	if(p!=null && cl!=null)mapView.getOverlays().add(new DirectionPathOverlay(p, cl[0].getPoint()));
        else System.out.println("INGEN PUNKTER MOTHERFUCKER");
    }
    
    public GeoPoint findStopInCl(int stopid, ClosestHolder[] cl){
    	for(ClosestHolder closest : cl){
    		if(closest.getBusStopID()==stopid) return closest.getPoint();
    	}
    	return null;
    }
    
    public void onBackPressed()
    {
    	
    	this.finish();
    }
    @SuppressWarnings("static-access")
  	@Override
  	protected void onResume() {
  		super.onResume();
  	    // Sets the restrictions on the location update. 
  		locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 100, 1, locationListener);

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
          mapOverlay = new MapOverlay(tmp, this,realTimeCodes, cl);        
         
      }
      
      /*
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
      }*/
      /*
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
      }*/
      
      
      
      // Thread classes //
      // Thread starting the oracle queries
     /* class OracleThread extends AsyncTask<Void, Void, Void>
      {
          private Context context;    
          Route [] foundRoutes;
          StringBuffer buf;// = new StringBuffer();
          public OracleThread(Context context)
          {
          	
              this.context = context;
          }

          @Override
          protected Void doInBackground(Void... params)
          {
          	long time = System.nanoTime();
          	buf = Helpers.run(editTe.getText().toString(),tSetExclude, locationsArray,k_browser, realTimeCodes);
          	long newTime = System.nanoTime() - time;
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
        	  if(buf != null)
        	  {
            	//myLocationText.setText(buf.toString());
        	  }
            	editTe.setEnabled(true);
                button.setEnabled(true);
            	
          }
      }  */
   
      public class DirectionPathOverlay extends Overlay {

    	    private GeoPoint gp1;
    	    private GeoPoint gp2;

    	    public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) {
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
    	            Point point2 = new Point();
    	            projection.toPixels(gp2, point2);
    	            paint.setStrokeWidth(2);
    	            canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,
    	                    (float) point2.y, paint);
    	        }
    	        return super.draw(canvas, mapView, shadow, when);
    	    }

    	    @Override
    	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	        // TODO Auto-generated method stub

    	        super.draw(canvas, mapView, shadow);
    	    }

    	}
  	}