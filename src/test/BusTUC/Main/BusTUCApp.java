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
import test.BusTUC.Favourites.SDCard;
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
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
    GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
    GPS k_gps; // Object of the GetGPS class. 
    HashMap<Integer,Location> tSetAllStops; // HashMap used for finding closest locations. Adds stops from both sides of the road. For use on map w
    String provider; // Provider 
    HashMap realTimeCodes; 
    ClosestHolder [] cl; // Object containing geopoint of closest stops. 
    StringBuffer presentation; // String which contain answer from bussTUC

    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapView); 
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
        // Gets the coordinates from the bus XML file
        
        
		// Test database stuff
        /*Database myDb = new Database(this);
        SQLiteDatabase db = myDb.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT destination FROM test", new String[]{""+1});
        System.out.println("Found in db: " + cursor.getCount());*/
        // End database stuff

    
        View zoomView = mapView.getZoomControls(); 
 
        zoomLayout.addView(zoomView, 
            new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);
        
        mc = mapView.getController();
        System.out.println("Sjekker lengde: " +Homescreen.gpsCords.length);
        System.out.println("Sjekker browser: " +Homescreen.k_browser.toString());

    
        try
        {

	        realTimeCodes = Homescreen.k_browser.realTimeData();
	        System.out.println("Realtinmecodessizefirst: " + realTimeCodes.size());
	        Log.v("provider","provider:"+ provider);
        }
        catch(Exception e)
        {
        	//Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
            System.exit(0);
        	
        }       
        
        Sort sort = new Sort();
        //tSetExclude = sort.m_partialSort(locationsArray,5,500,false, false);
        tSetAllStops = sort.m_partialSort(Homescreen.locationsArray,10,1000,false, true);
        int numberofStops = tSetAllStops.size();
        cl = new ClosestHolder[numberofStops];
        
     //   Log.v("sort","returnedtSet"+tSetExclude.size());	
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
                (int) (Homescreen.currentlocation.getLatitude() * 1E6), 
                (int) (Homescreen.currentlocation.getLongitude() * 1E6));

       Helpers.addUser(p,mapOverlay, getResources().getDrawable(R.drawable.pp));
       System.out.println("My loc: " + Homescreen.currentlocation.getLatitude() *1E6 + "  " + Homescreen.currentlocation.getLongitude() *1E6);
        showOverlay();
        mc.animateTo(p);
        mc.setZoom(16);
        Bundle extras = getIntent().getExtras();
        ArrayList <Route> foundRoutes = new ArrayList <Route>();
        String value = "";
        ArrayList <Integer> id = new ArrayList <Integer>();
		if(extras !=null) 
		{
		   foundRoutes = extras.getParcelableArrayList("test");
		 //  value = extras.getString("test");
		   for(int i=0; i<cl.length; i++)
		   {
			   for(int j = 0; j<foundRoutes.size(); j++)
			   {
				   if(cl[i].getBusStopID() == foundRoutes.get(j).getBusStopNumber())
				   {
					   id.add(cl[i].getBusStopID());
					   System.out.println("FOUND ID: " + id);
				   }
			   }
		   }
           drawPath(id);

	
		}
    }

    public void drawPath(ArrayList<Integer> id){
    	ArrayList <GeoPoint> busStop = findStopInCl(id, cl);
    	System.out.println("Found point: " + busStop);
    	if(p!=null && cl!=null)mapView.getOverlays().add(new DirectionPathOverlay(p, busStop));
        else System.out.println("INGEN PUNKTER MOTHERFUCKER");
    }
    
    public ArrayList <GeoPoint> findStopInCl(ArrayList <Integer> id, ClosestHolder[] cl){
    	ArrayList <GeoPoint> retList = new ArrayList<GeoPoint>();
    	for(int i=0; i<cl.length; i++)
    	{
    		for(int j=0; j<id.size(); j++)
    		{
    			if(cl[i].getBusStopID() == id.get(j))
    			{
    				retList.add(cl[i].getPoint());
    			}
    		}
    	}
    	return retList;
    }
    
    public void onBackPressed()
    {
    	
    	finish();
    }
    @SuppressWarnings("static-access")
  	@Override
  	protected void onResume() {
  		super.onResume();
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
    	            System.out.println("Size of point: " + point2.length);
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
  	}