package test.BusTUC.Main;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView.LayoutParams;

import test.BusTUC.R;
import test.BusTUC.Calc.Sort;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.ClosestHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.VideoView;

public class Homescreen extends Activity {
	private String [] bgColors = {"#3C434A","#A3AB19","#F66F89","#D9F970"};
	private int currentBgColor = 0;
	private int numButtons = 6;
	//private int[] buttons = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5, R.id.button6};
	private Button[] buttons;
	private Button goButton; 
	//private EditText editText;
	
	// Global variables that need to be accessed from other contexts
	// No prob to let them stay public static, as they anyway are accessed by the same process
	public static String[][] gpsCords;  // Array containing bus stops
	public static Location currentlocation, busLoc; // Location objects
	public static HashMap<Integer,HashMap<Integer,Location>> locationsArray; // GPS coordinates
	public static Browser k_browser; // Object doing communation with bussTUC and Real-Time system
	public static HashMap <Integer,Location> tSetAllStops;
	public static ClosestHolder [] cl; // Object containing geopoint of closest stops. 

	// End of global variables
	AutoCompleteTextView textView;
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
    GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
    GPS k_gps; // Object of the GetGPS class. 

    // Static because of access from BusList
    HashMap<Integer,Location> tSetExclude; // HashMap used for finding closest locations. Ignores stops at both sides of the road
    LocationManager locationManager; // Location Manager
    String provider; // Provider 
    LocationListener locationListener;
    
    HashMap realTimeCodes; 
    String[] busStop = new String[numButtons];
    ArrayList <String> favorites;
    // adds edittext box
    Context context;
    
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	LinearLayout line;


    public void updateButtons(String[] busStop, Button[]buttons)
    {
    	// Add some dummy stops to fill lists
    	 busStop[0] = "Buenget";
         busStop[1] = "Tiller";
         busStop[2] = "Moholt";
         busStop[3] = "Pirbadet";
         busStop[4] = "Dragvoll";
         busStop[5] = "Ilsvika";
         
        ArrayList <String> favorites = SDCard.getFilesFromSD("fav_routes");
    	List <String> temp = Arrays.asList(busStop);
    	int addedfavorites = 0;
    	// Set font
        Typeface font = Typeface.createFromAsset(getAssets(), "dotmatrix.ttf");  
        // Iterate through stops, and add from SD-card
        for(int i=0; i<busStop.length && i< favorites.size(); i++)
        {
           if(!Helpers.containsIgnoreCase(temp, favorites.get(i)))
           {
        	   System.out.println("Does not contain ");
        	   busStop[addedfavorites] = favorites.get(i);
        	   addedfavorites++;
           }
        }
        // Connect buttons with corresponding XML-files
        if(busStop != null)
        {
        	buttons[0] =  (Button)this.findViewById(R.id.button1);
            buttons[1] =  (Button)this.findViewById(R.id.button2);
            buttons[2] =  (Button)this.findViewById(R.id.button3);
            buttons[3] =  (Button)this.findViewById(R.id.button4);
            buttons[4] =  (Button)this.findViewById(R.id.button5);
            buttons[5] =  (Button)this.findViewById(R.id.button6);      	       	
        	
        }
        // Finally set button text according to bus stop
        for(int i = 0; i<numButtons && i<busStop.length; i++)
    	{
    		buttons[i].setText(busStop[i]);
    		buttons[i].setTypeface(font);
    	}
        
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        this.setRequestedOrientation(
        		ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.homescreen);
        buttons = new Button[6];
        goButton = (Button)this.findViewById(R.id.goButton);
        //editText = (EditText)this.findViewById(R.id.editText);
        
        // Gets the coordinates from the bus XML file
        long f = System.nanoTime();              
        String[] gpsCoordinates = getResources().getStringArray(R.array.coords3);      

		// creates a HashMap containing all the location objects 

        // Formats the bus coordinates
    	// 1 - navn
		// 2 - lat
		// 3 - long
        gpsCords = GPS.formatCoordinates(gpsCoordinates);
        long s = System.nanoTime() - f;
        System.out.println("TIME SPENT FINDING LOCATION: " + s /(1000000000.0));
     // Autocompletion
        ArrayList <String> dictionary = Helpers.createDictionary(gpsCords);
        textView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, dictionary);
        textView.setAdapter(adapter);

        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        line = (LinearLayout)this.findViewById(R.id.homelayout);
        line.setOnTouchListener(activitySwipeDetector);
        // Hide keyboard on start
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Retrieve favourites
        // Can have 6 favourites
        // Bind listeners to favourites
        updateButtons(busStop,buttons);      
       
       
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
	        long rt = System.nanoTime();
	        realTimeCodes = k_browser.realTimeData();
	        long rt2 = System.nanoTime() - rt;
	        System.out.println("TIME SPENT REALTIMECODES: " + (rt2/1000000000.0));
	        System.out.println("Realtinmecodessizefirst: " + realTimeCodes.size());
	        //    Log.v("provider","provider:"+ provider);
        }
        catch(Exception e)
        {
        	//Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
            System.exit(0);
        	
        }       
        
       
       System.out.println("Setting up locationListener!!");
        // Creates a locationListener
        locationListener = new LocationListener() {
           
        	// This method runs whenever the criteria for change is met. 
            public void onLocationChanged(Location location) {
            	 System.out.println("LOCATIONLISTENER");
            	currentlocation = location; 
        		Log.v("currentLoc","PROV:LOC=" + currentlocation.getLatitude()+":"+currentlocation.getLongitude());
        		
        		   long f = System.nanoTime();              
                 
        		// creates a HashMap containing all the location objects
                locationsArray = Helpers.getLocations(gpsCords,provider, currentlocation);
                long s = System.nanoTime() - f;
              //  System.out.println("TIME SPENT FINDING LOCATION: " + s /(1000000000.0));
                //System.out.println("REALTIMEX: " + realTimeCodes.size());
                Log.v("sort","returnedHmap:"+locationsArray.size());	
                // creates a HashMap with all the relevant bus stops
                Sort sort = new Sort();
                
                // One stop per group
                long first = System.nanoTime();
                tSetExclude = sort.m_partialSort(locationsArray,5,500,false, false);
                // All stops. Necessary for usage with map
                tSetAllStops = sort.m_partialSort(locationsArray,10,1000,false, true); 
                long second = System.nanoTime() - first;
                System.out.println("TIME SPENT SORTING SHIT: " + second /(1000000000.0));
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
                System.out.println("TSET SET: " + tSetExclude.size());

                Log.v("sort","returnedtSet"+tSetExclude.size());	
                // adds the closest bus stop as a GeoPoint
  
                
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
        // Only request updates if > 500 ms and 10 m
        locationManager.requestLocationUpdates(provider, 500, 10, locationListener);

        
       // binds listener to the button
       goButton.setOnClickListener(new OnClickListener() 
       {
            public void onClick(View v) 
            {
    	    	new OracleThread(context).execute();    	  
            }
        });
       
      for(int i=0; i<buttons.length; i++)
      {
    	  final Button butz = buttons[i];
    	  // If click, set text to text field, and start thread
	      butz.setOnClickListener(new OnClickListener() 
	       {
	    	  public void onClick(View v) 
	          {
	    		  textView.setText(butz.getText());
		    	    new OracleThread(context).execute();
	    	  
	          }
	       });
	      // If long click, delete item
	      butz.setOnLongClickListener(new OnLongClickListener()
	 	  {
			@Override
			public boolean onLongClick(View v) {
				DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						switch(which)
						{
						case DialogInterface.BUTTON_POSITIVE:
							String tmp = (String) butz.getText();
				   			if(SDCard.deleteFileFromSD(tmp,"fav_routes"))
				   			{
				   				updateButtons(busStop, buttons);
				   			}
						case DialogInterface.BUTTON_NEGATIVE:
							// Do nothing

						}
							
					}
						
				};	
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Slette fil?").setPositiveButton("Ja", dc)
				    .setNegativeButton("Nei", dc).show();			
		   			
	   			return true;
			}
	   	  });
	      
       }    
  	
  }
	
	// Menu properties
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu2, menu);
        return true;
    }   
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
    	
        switch (item.getItemId())
        {
        case R.id.addnew:
        	// Prompt user regarding destination
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	
        	final Favourite fav = new Favourite();
        	// First input dialog 
        	alert.setTitle("Destinasjon");
        	alert.setMessage("Skriv inn destinasjon");        	
        	final EditText input = new EditText(this);
        	alert.setView(input);
        	alert.setPositiveButton("Lagre", new DialogInterface.OnClickListener() 
        	{
	        	public void onClick(DialogInterface dialog, int whichButton) 
	        	{
	        	
	        	  String value = input.getText().toString();
	        	  ArrayList <String> queries = new ArrayList <String>();
	        	  fav.setQuery(value);
	        	  queries.add(value);
	        	  // For now, store query as filename, as the file does not contain anything else
	        	  if(SDCard.generateNoteOnSD(fav.getQuery(), queries,"fav_routes"))
	        	  {
	        		  updateButtons(busStop,buttons);
	        	  }
	        	  
	          	
	        	 }
        	});
        	alert.show();
            return true;
            
        case R.id.map:
        	new MapThread(context).execute();
        	
        
        // Add other menu items
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
    // Thread classes //
    // Thread starting the oracle queries
    class OracleThread extends AsyncTask<Void, Void, Void>
    {
        private Context context;    
        ArrayList <Route> buf;
    //    StringBuffer buf = new StringBuffer();
      //  ArrayList <String> buf = new ArrayList <String>();
        ProgressDialog myDialog = null;
        public OracleThread(Context context)
        {
        	
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
        	long time = System.nanoTime();
        	buf = Helpers.run(textView.getText().toString(),tSetExclude, locationsArray,k_browser, realTimeCodes);
        	long newTime = System.nanoTime() - time;
			System.out.println("TIME ORACLE: " +  newTime/1000000000.0);
			return null;
        }
        
        @Override
        protected void onPreExecute()
        {
           InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        	imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        	myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");
        	textView.setEnabled(false);
        	goButton.setEnabled(false);
        }

        @Override
       protected void onPostExecute(Void unused)
        {
    		myDialog.dismiss();

        	if(buf != null)
	      	{
        		System.out.println("Starting activity");
	          	Intent intent = new Intent(getApplicationContext(), Answer.class);
	          	intent.putParcelableArrayListExtra("test", buf);
	          	
	          	//intent.putExtra("test", buf);
	          	context.startActivity(intent);
	        	
		    }
        	
        	
         textView.setEnabled(true);
      	  goButton.setEnabled(true);
        }
    }  
    
    
    
    // Thread classes //
    // Thread starting the oracle queries
  class MapThread extends AsyncTask<Void, Void, Void>
    {
        private Context context;    
        Intent intent;
        ProgressDialog myDialog = null;
        public MapThread(Context context)
        {
        	
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
        	intent = new Intent(getApplicationContext(), BusTUCApp.class);
        	context.startActivity(intent);
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
    
	    public void onBackPressed()
	    {
	    	this.finish();
	    	System.exit(0);
	    }
	    @SuppressWarnings("static-access")
	  	@Override
	  	protected void onResume() 
	    {
	  		super.onResume();
	  	//	editText.setEnabled(true);
	  		 textView.setEnabled(true);
	      	  goButton.setEnabled(true);
	  	    // Sets the restrictions on the location update. 
	  		locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 100, 1, locationListener);
	
	  	}
    

public class ActivitySwipeDetector implements View.OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	private Activity activity;
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	LinearLayout ll = (LinearLayout)findViewById(R.id.homelayout);
	public ActivitySwipeDetector(Activity activity){
	    this.activity = activity;
	}

	public void onRightToLeftSwipe(){
	    Log.i(logTag, "RightToLeftSwipe!");
	    if(currentBgColor>0) currentBgColor--;
	    line.setBackgroundColor(Color.parseColor(bgColors[currentBgColor]));
	}

	public void onLeftToRightSwipe(){
	    Log.i(logTag, "LeftToRightSwipe!");
	    if(currentBgColor<(bgColors.length-1)) currentBgColor++;
	    line.setBackgroundColor(Color.parseColor(bgColors[currentBgColor]));
	}

	public void onTopToBottomSwipe(){
	    Log.i(logTag, "onTopToBottomSwipe!");
	    
	}

	public void onBottomToTopSwipe(){
	    Log.i(logTag, "onBottomToTopSwipe!");
	    
	}

	public boolean onTouch(View v, MotionEvent event) {
	    switch(event.getAction()){
	        case MotionEvent.ACTION_DOWN: {
	            downX = event.getX();
	            downY = event.getY();
	            return true;
	        }
	        case MotionEvent.ACTION_UP: {
	            upX = event.getX();
	            upY = event.getY();

	            float deltaX = downX - upX;
	            float deltaY = downY - upY;

	            // swipe horizontal?
	            if(Math.abs(deltaX) > MIN_DISTANCE){
	                // left or right
	                if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
	                if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
	            } else { Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE); }

	            // swipe vertical?
	            if(Math.abs(deltaY) > MIN_DISTANCE){
	                // top or down
	                if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
	                if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
	            } else { Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE); }

	            return true;
	        }
	    }
	    return false;
	}

	}
}
	

