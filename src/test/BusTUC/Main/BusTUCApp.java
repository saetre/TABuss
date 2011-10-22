package test.BusTUC.Main;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import test.BusTUC.R;
import test.BusTUC.Calc.Calculate;
import test.BusTUC.Calc.Sort;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Path.NavigationDataSet;
import test.BusTUC.Path.NavigationSaxHandler;
import test.BusTUC.Path.RouteOverlay;
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
 //   HashMap<Integer,Location> tSetAllStops; // HashMap used for finding closest locations. Adds stops from both sides of the road. For use on map w
    String provider; // Provider 
    HashMap realTimeCodes; 
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
        initialize();
        for(int i=0; i<Homescreen.cl.length; i++)
        {
        	Helpers.addStops(Homescreen.cl[i],getResources().getDrawable(R.drawable.s_busstop2),mapOverlay);
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

		if(extras !=null) 
		{
	        ArrayList <Integer> id = new ArrayList <Integer>();
			ArrayList <Route> foundRoutes = new ArrayList <Route>();
	        String value = "";
			double[] dest = new double[2];
			// Extras will now contain an ArrayList<Route>
		   foundRoutes = extras.getParcelableArrayList("test");
		 //  value = extras.getString("test");
		   // Iterate through the closest stop, and match bus stop id
		   for(int i=0; i<Homescreen.cl.length; i++)
		   {
			   for(int j = 0; j<foundRoutes.size(); j++)
			   {
				   // If found, add to both id list, and lat/long list
				   if(Homescreen.cl[i].getBusStopID() == foundRoutes.get(j).getBusStopNumber())
				   {
					   id.add(Homescreen.cl[i].getBusStopID());
					   dest[0] = Homescreen.cl[i].getPoint().getLatitudeE6();
					   dest[1] = Homescreen.cl[i].getPoint().getLongitudeE6();
					   System.out.println("FOUND ID: " + Homescreen.cl[i].getBusStopID() + Homescreen.cl[i].getStopName());
				   }
				   else
				   {
					   System.out.println("DID not find ID: " +foundRoutes.get(j).getBusStopNumber());
				   }
			   }
		   }  
           drawPath(id);

		   
		   
		   // Create driving path
		    Location lastKnownLocation = Homescreen.currentlocation;
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
	        urlString.append("&ie=UTF8&0&om=0&output=kml");

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
	        }

	    }  
	

	
	}	
		

		
    public void drawPath(NavigationDataSet navSet, int color, MapView mMapView01) {

      //  Log.d(myapp.APP, "map color before: " + color);        

        // color correction for dining, make it darker
        if (color == Color.parseColor("#add331")) color = Color.parseColor("#6C8715");
   //     Log.d(myapp.APP, "map color after: " + color);

        Collection overlaysToAddAgain = new ArrayList();
        for (Iterator iter = mMapView01.getOverlays().iterator(); iter.hasNext();) {
            Object o = iter.next();
            //Log.d(myapp.APP, "overlay type: " + o.getClass().getName());
            if (!RouteOverlay.class.getName().equals(o.getClass().getName())) {
                // mMapView01.getOverlays().remove(o);
                overlaysToAddAgain.add(o);
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
    	ArrayList <GeoPoint> busStop = findStopInCl(id,Homescreen.cl);
    	System.out.println("Found point: " + busStop);
    	if(p!=null && Homescreen.cl!=null)mapView.getOverlays().add(new DirectionPathOverlay(p, busStop));
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
          mapOverlay = new MapOverlay(tmp, this,realTimeCodes, Homescreen.cl);        
         
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