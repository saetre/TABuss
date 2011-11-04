package test.BusTUC.Main;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import test.BusTUC.Database.DatabaseHelper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import test.BusTUC.R;
import test.BusTUC.Calc.Sort;
import test.BusTUC.Database.Database;
import test.BusTUC.Database.DatabaseAdapter;
import test.BusTUC.Database.Query;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.BusStop;
import test.BusTUC.Stops.ClosestStopOnMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

public class Homescreen extends Activity {
	private String [] bgColors = {"#3C434A","#A3AB19","#F66F89","#D9F970"};
	private int currentBgColor = 0;
	private int numButtons = 6;
	//private int[] buttons = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5, R.id.button6};
	private Button[] buttons;
	private Button goButton, amazeButton; 
	//private EditText editText;
	// Global variables that need to be accessed from other contexts
	// No prob to let them stay public static, as they anyway are accessed by the same process
	public static String[][] gpsCords, gpsCords2;  // Array containing bus stops
	public static Location currentlocation; // Location objects
	//public static HashMap<Integer,HashMap<Integer,Location>> locationsArray; // GPS coordinates
	public static Browser k_browser; // Object doing communation with bussTUC and Real-Time system
	//public static HashMap <Integer,Location> tSetAllStops;
	public static ClosestStopOnMap [] cl; // Object containing geopoint of closest stops. 
	public static 	HashMap realTimeCodes; 
	public static ArrayList <BusStop> allStops;
	ArrayList<BusStop> busStops, busStopsNoDuplicates;

	DatabaseHelper dbHelper;
	// End of global variables
	AutoCompleteTextView textView;
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
	GeoPoint p,p2; // p is current location, p2 is closest bus stop. 
	GPS k_gps; // Object of the GetGPS class. 

	// Static because of access from BusList
	//HashMap<Integer,Location> tSetExclude; // HashMap used for finding closest locations. Ignores stops at both sides of the road
	LocationManager locationManager; // Location Manager
	String provider; // Provider 
	LocationListener locationListener;


	String[] busStop = new String[numButtons];
	ArrayList <String> favorites;
	// adds edittext box
	Context context;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int REQUEST_CODE = 1234;

	private Spinner mSupportedLanguageView;

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
		new StartUpThread(context).execute();

		dbHelper=new DatabaseHelper(context);
		int c= dbHelper.getQueryCount();
		this.setTitle("MapApp - "+c+" Søk gjort");
		this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.homescreen);
		buttons = new Button[6];
		goButton = (Button)this.findViewById(R.id.goButton);
		amazeButton = (Button)this.findViewById(R.id.amazebutton);
		//editText = (EditText)this.findViewById(R.id.editText);

		// Gets the coordinates from the bus XML file
		long f = System.nanoTime();              
		String[] gpsCoordinates = getResources().getStringArray(R.array.coords4);    
		String [] gpsCoordinates2 = getResources().getStringArray(R.array.coords3);

		// creates a HashMap containing all the location objects 

		// Formats the bus coordinates
		// 1 - navn
		// 2 - lat
		// 3 - long
		// Create locationmanager/listener, and retrieve real-time codes
		gpsCords = GPS.formatCoordinates(gpsCoordinates);
		gpsCords2 = GPS.formatCoordinates(gpsCoordinates2);
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

	

		/*  try {
			System.out.println("OVERSATT: " +Helpers.translateRequest("skole"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if(currentlocation!=null) this.getSuggestionBasedOnPosition();
		// binds listener to the button
		goButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				new OracleThread(context).execute();    	  
			}
		});
		amazeButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				getSuggestionBasedOnPosition();    	  
			}
		});

		createButtonListeners();



	}

	private void createButtonListeners() 
	{
		for(int i=0; i<buttons.length; i++)
		{
			final Button shortcutButtons = buttons[i];
			// If click, set text to text field, and start thread
			shortcutButtons.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					textView.setText(shortcutButtons.getText());
					new OracleThread(context).execute();

				}
			});

			// If long click, delete item
			shortcutButtons.setOnLongClickListener(new OnLongClickListener()
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
								String tmp = (String) shortcutButtons.getText();
								if(SDCard.deleteFileFromSD(tmp,"fav_routes"))
								{
									updateButtons(busStop, buttons);
								}
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Do nothing
								break;

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

	private void createLocationListener() {
		locationListener = new LocationListener() {

			// This method runs whenever the criteria for change is met. 
			@Override
			public void onLocationChanged(Location location) 
			{
				System.out.println("LOCATIONLISTENER");
				currentlocation = location; 
				// currentlocation.setLatitude(63.429256);
				//currentlocation.setLongitude(10.367672);
				// ila 10.367672,63.429256
			//	10.394555,63.43109
				//getSuggestionBasedOnPosition();

				Log.v("currentLoc","PROV:LOC=" + currentlocation.getLatitude()+":"+currentlocation.getLongitude());

				long first = System.nanoTime();
				
				// For use with the oracle and the gps2 file
				busStopsNoDuplicates = Helpers.getLocationsArray(gpsCords, provider, currentlocation, 1000,3,false);
				// For use with the map, and real-time functionality only
				busStops = Helpers.getLocationsArray(gpsCords2, provider, currentlocation, 1000,10, true);				
				// All stops
				allStops = Helpers.getAllLocations(gpsCords2, provider);
				long second = System.nanoTime() - first;
				System.out.println("TIME SPENT SORTING SHIT: " + second /(1000000000.0));
				int numStops = busStops.size();
				System.out.println("USING " + numStops + " STOPS");
				cl = new ClosestStopOnMap[numStops];

				for(int i = 0;i<numStops;i++)
				{
					cl[i] = new ClosestStopOnMap(new GeoPoint(
							(int)	(busStops.get(i).location.getLatitude()* 1E6),
							(int)	(busStops.get(i).location.getLongitude() * 1E6)),
							(int) busStops.get(i).stopID,
							busStops.get(i).name);

				}


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
 	}

	private void createLocationManager() 
	{
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
	}

	/*	private void startVoiceRecognitionActivity()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
		startActivityForResult(intent, REQUEST_CODE);
	}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			// Populate the wordsList with the String values the recognition engine thought it heard
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			for(int i=0; i<matches.size(); i++)
			{
				System.out.println("FOUND WORD: " + matches.get(i));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				@Override
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
			return true;

		case R.id.realtime:
			long time = System.nanoTime(); 
			ArrayList <ClosestStopOnMap> holder = new ArrayList<ClosestStopOnMap>();
			for(int i=0; i<cl.length; i++)
			{
				holder.add(cl[i]);
			}
			Intent intent = new Intent(context, RealTimeList.class);
			intent.putParcelableArrayListExtra("test", holder); 
			context.startActivity(intent);
			Long newTime = System.nanoTime() - time;
			System.out.println("TIME LOOKUP: " +  newTime/1000000000.0);

			return false;


		case R.id.history:
			Intent history = new Intent(context, History.class);
			context.startActivity(history);
			return false;

		case R.id.speech:
			return false;
			//	startVoiceRecognitionActivity();

		case R.id.about:
			Intent about = new Intent(context, About.class);
			context.startActivity(about);

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
			try
			{

				double lat = currentlocation.getLatitude();
				double lon = currentlocation.getLongitude();
				Cursor areas = dbHelper.getAreaId(lat, lon);
				int area = 0;
				if(areas.getCount()==0){
					area = dbHelper.AddArea(lat+0.01, lat-0.01, lon+0.01, lon-0.01);
				}else{
					areas.moveToFirst();
					area = areas.getInt(0);
				}
				Cursor a = dbHelper.getArea(area);
				a.moveToFirst();
				System.out.println(a.getDouble(1)+ "-" + a.getDouble(2));
				dbHelper.AddQuery(new Query(area ,textView.getText().toString(), Helpers.minutesFromDate(new Date()), new Date().getDay()));

				System.out.println("Objects hopefully init: " + busStopsNoDuplicates.size() + "  " + k_browser.toString() + "  " + realTimeCodes.size());
				buf = Helpers.run(textView.getText().toString(), busStopsNoDuplicates,k_browser, realTimeCodes);
				//buf = Helpers.runServer(textView.getText().toString(), k_browser, realTimeCodes, currentlocation);
				long newTime = System.nanoTime() - time;
				System.out.println("TIME ORACLE: " +  newTime/1000000000.0);
			}
			catch(Exception e)
			{
				myDialog.dismiss();
				e.printStackTrace();
				ArrayList <String> err = new ArrayList <String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorHomeScreen::ORACLETHREAD", err, "errors");
			}
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
			else
			{
				myDialog.dismiss();
				Toast.makeText(context, "Fant ingen ruter", Toast.LENGTH_LONG).show();
				
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
			try
			{
			intent = new Intent(getApplicationContext(), BusTUCApp.class);
			context.startActivity(intent);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ArrayList <String> err = new ArrayList <String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorHomeScreen:MapTHREAD", err, "errors");
			}
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


	class StartUpThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;    
		Intent intent;
		ProgressDialog myDialog = null;
		public StartUpThread(Context context)
		{

			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			
			try
			{
				createLocationManager();
			
			}
			catch(Exception e)
			{
				myDialog.dismiss();
			}
			
			return null;
		}

		@Override
		protected void onPreExecute()
		{

			try
			{
				myDialog = ProgressDialog.show(context, "Loading!", "Laster holdeplasser");
				createLocationListener();		

				// Only request updates if > 500 ms and 10 m
			}
			catch(Exception e)
			{
				e.printStackTrace();
				myDialog.dismiss();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			if(locationManager != null)locationManager.requestLocationUpdates(provider, 500, 10, locationListener);
			myDialog.dismiss();

		}
	}  




	@Override
	public void onBackPressed()
	{

		DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				switch(which)
				{
				case DialogInterface.BUTTON_POSITIVE:
					((Activity) context).finish();
					System.exit(0);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// Do nothing
					break;

				}

			}

		};	
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Avslutte?").setPositiveButton("Ja", dc)
		.setNegativeButton("Nei", dc).show();	

	}
	@Override
	protected void onStart()
	{
		super.onStart();

	}
	@SuppressWarnings("static-access")
	@Override
	protected void onResume() 
	{
		int c= dbHelper.getQueryCount();
		this.setTitle("MapApp - "+c+" Søk gjort");
		super.onResume();
		//	editText.setEnabled(true);
		textView.setEnabled(true);
		goButton.setEnabled(true);

		// Sets the restrictions on the location update. 
		//locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 100, 1, locationListener);

	}


	private void getSuggestionBasedOnPosition() {
		double lat = currentlocation.getLatitude();
		double lon = currentlocation.getLongitude();
		int time = Helpers.minutesFromDate(new Date());
		int day = new Date().getDay();
		Cursor areas = dbHelper.getQueryFromArea(lat, lon, time+120, time-120);

		System.out.println("FOUND "+areas.getCount()+" AREAS COVERING" + lat + " - " +lon);
		String whereTo = "";
		ArrayList<Query> destinations = new ArrayList<Query>();

		if(areas.getCount()>0){
			areas.moveToFirst();
			while(!areas.isLast()){
				destinations.add(new Query(areas.getInt(2), areas.getString(1), areas.getInt(3), areas.getInt(4)));
				areas.moveToNext();
			};
			destinations.add(new Query(areas.getInt(2), areas.getString(1), areas.getInt(3), areas.getInt(4)));
			whereTo += areas.getString(1);
		}else{
			whereTo=" et annet sted";
		}
		areas.close();
		for(Query q: destinations){
			q.setEuclideanDistance(time, day);
		}
		Collections.sort(destinations);

		for(Query q : destinations){
			System.out.println(q.toString());
		}
		//HashMap<String,Integer> sortedDestinations = Helpers.getMostFrequentDestination(destinations);
		//System.out.println("NUMBER OF DESTINATIONS:" + sortedDestinations.size());
		//for (Entry<String, Integer> entry : sortedDestinations.entrySet()) 
		//{ 
		//	System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		//}

		Toast.makeText(context, "Jeg tror du vil til: " + whereTo, Toast.LENGTH_LONG).show();
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

		@Override
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



