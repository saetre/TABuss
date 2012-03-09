package test.BusTUC.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import test.BusTUC.Database.DatabaseHelper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import test.BusTUC.R;
import test.BusTUC.Database.Query;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Speech.DummyObj;
import test.BusTUC.Speech.ExtAudioRecorder;
import test.BusTUC.Speech.HTTP;
import test.BusTUC.Stops.BusStop;
import test.BusTUC.Stops.ClosestStopOnMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Homescreen extends Activity
{
	private String[] bgColors =
	{ "#3C434A", "#A3AB19", "#F66F89", "#D9F970" };
	private int currentBgColor = 0;
	private int numButtons = 6;
	private int numStops;
	private int numStopsOnMap;
	private int dist;
	private boolean fancyOracle;
	// private int[] buttons =
	// {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,
	// R.id.button6};
	private Button[] buttons;
	private Button goButton, amazeButton;
	// private EditText editText;
	// Global variables that need to be accessed from other contexts
	// No prob to let them stay public static, as they anyway are accessed by
	// the same process
	public static String[][] gpsCords, gpsCords2; // Array containing bus stops
	public static Location currentlocation; // Location objects
	// public static HashMap<Integer,HashMap<Integer,Location>> locationsArray;
	// // GPS coordinates
	public static Browser k_browser; // Object doing communation with bussTUC
										// and Real-Time system
	// public static HashMap <Integer,Location> tSetAllStops;
	public static ClosestStopOnMap[] cl; // Object containing geopoint of
											// closest stops.
	// Changed to avoid raw type
	public static HashMap<Integer, Integer> realTimeCodes;
	public static ArrayList<BusStop> allStops;
	ArrayList<BusStop> busStops, busStopsNoDuplicates;
	// Switch server on or off
	boolean server = true;
	// Send sms, or query via net
	boolean sms = false;
	DatabaseHelper dbHelper;
	// End of global variables
	AutoCompleteTextView textView;
	MapController mc; // Controller for the map
	List<String> prov; // List of providers
	GeoPoint p, p2; // p is current location, p2 is closest bus stop.
	GPS k_gps; // Object of the GetGPS class.

	// Static because of access from BusList
	// HashMap<Integer,Location> tSetExclude; // HashMap used for finding
	// closest locations. Ignores stops at both sides of the road
	LocationManager locationManager; // Location Manager
	String provider; // Provider
	LocationListener locationListener;

	String[] busStop = new String[numButtons];
	ArrayList<String> favorites;
	// adds edittext box
	static Context context;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int REQUEST_CODE = 1234;

	private Spinner mSupportedLanguageView;

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	LinearLayout line;
	private TextView title;
	private ImageView icon;

	/*
	 * 
	 * 
	 * Speech test
	 */

	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int CALLBACK_PERIOD = 4000;
	byte[] myBuffer;
	final ExtAudioRecorder ext = ExtAudioRecorder.getInstance(false);
	private String filePath = "";
	private String filePath2 = "";
	RandomAccessFile randomAccessWriter;
	private short nChannels = 1;
	private short bSamples = 16; // 16 bit
	private int sRate = 16000;
	private int framePeriod = 40;
	private int payloadSize = 0;
	private int cAmplitude = 0;
	private SpeechRecognizer sr;
	boolean stopRecording = false;
	boolean recorder = true;

	public void updateButtons(String[] busStop, Button[] buttons)
	{
		// Add some dummy stops to fill lists
		busStop[0] = "Buenget";
		busStop[1] = "Tiller";
		busStop[2] = "Moholt";
		busStop[3] = "Pirbadet";
		busStop[4] = "Dragvoll";
		busStop[5] = "Ilsvika";
		ArrayList<String> favorites;
		try
		{
			favorites = SDCard.getFilesFromSD("fav_routes");
		} catch (Exception e)
		{
			e.printStackTrace();
			favorites = new ArrayList<String>();
		}
		List<String> temp = Arrays.asList(busStop);
		int addedfavorites = 0;
		// Set font
		// Typeface font = Typeface.createFromAsset(getAssets(),
		// "dotmatrix.ttf");
		// Iterate through stops, and add from SD-card
		for (int i = 0; i < busStop.length && i < favorites.size(); i++)
		{
			if (!Helpers.containsIgnoreCase(temp, favorites.get(i)))
			{
				System.out.println("Does not contain ");
				busStop[addedfavorites] = favorites.get(i);
				addedfavorites++;
			}
		}
		// Connect buttons with corresponding XML-files
		if (busStop != null)
		{
			buttons[0] = (Button) this.findViewById(R.id.button1);
			buttons[1] = (Button) this.findViewById(R.id.button2);
			buttons[2] = (Button) this.findViewById(R.id.button3);
			buttons[3] = (Button) this.findViewById(R.id.button4);
			buttons[4] = (Button) this.findViewById(R.id.button5);
			buttons[5] = (Button) this.findViewById(R.id.button6);

		}
		// Finally set button text according to bus stop
		for (int i = 0; i < numButtons && i < busStop.length; i++)
		{
			buttons[i].setText(busStop[i]);
			// buttons[i].setTypeface(font);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.homescreen);
		new StartUpThread(context).execute();
		dbHelper = new DatabaseHelper(context);
		// this.setRequestedOrientation(
		// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Set properties according to existing preferences
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.layout.preference, false);
		String foo = preferences.getString("num1", "");
		numStops = Integer.parseInt(foo);
		String foo2 = preferences.getString("num2", "");
		numStopsOnMap = Integer.parseInt(foo2);
		String foo3 = preferences.getString("num3", "");
		dist = Integer.parseInt(foo3);
		fancyOracle = preferences.getBoolean("Orakelvalg", fancyOracle);

		buttons = new Button[6];
		goButton = (Button) this.findViewById(R.id.goButton);
		amazeButton = (Button) this.findViewById(R.id.amazebutton);
		// editText = (EditText)this.findViewById(R.id.editText);

		loadDictionaries();

		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(
				this);
		line = (LinearLayout) this.findViewById(R.id.homelayout);
		line.setOnTouchListener(activitySwipeDetector);
		// Hide keyboard on start
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		updateButtons(busStop, buttons);

		if (currentlocation != null)
			this.getSuggestionBasedOnPosition();
		// binds listener to the button
		goButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				queryOrSMS();
			}
		});
		amazeButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final String whereTo = getSuggestionBasedOnPosition();

				DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
						case DialogInterface.BUTTON_POSITIVE:
							textView.setText(whereTo);
							Toast.makeText(context, "I'm awesome!",
									Toast.LENGTH_SHORT).show();
							queryOrSMS();

							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Do nothing
							break;

						}

					}

				};
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Gjettet jeg riktig?")
						.setPositiveButton("Ja", dc)
						.setNegativeButton("Nei", dc).show();

			}
		});

		createButtonListeners();

	}

	private void queryOrSMS()
	{

		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		// First input dialog
		alert.setTitle("Velg kjøring. SMS koster 1 kr");
		alert.setMessage("Query via nett eller sms til orakel");

		alert.setPositiveButton("SMS", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				sms = true;
				new OracleThread(context).execute();

			}
		});
		alert.setNegativeButton("Nett", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				sms = false;
				new OracleThread(context).execute();

			}
		});

		alert.show();
	}

	private void loadDictionaries()
	{
		System.out.println("Loading dictionaries...");
		// Gets the coordinates from the bus XML file
		long f = System.nanoTime();
		String[] gpsCoordinates;
		String[] gpsCoordinates2;

		try
		{
			gpsCoordinates2 = Helpers.readLines(getAssets().open("gps3.xml"));
			gpsCords2 = GPS.formatCoordinates(gpsCoordinates2);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		long s = System.nanoTime() - f;
		System.out
				.println("TIME SPENT FINDING LOCATION: " + s / (1000000000.0));
		// Autocompletion
		ArrayList<String> dictionary;
		// Check if SD-card is present

		try
		{
			dictionary = Helpers.getDictionary("dictionary_finalv2",
					"dictionary");

		}

		// TODO:
		/*
		 * Move out of onCreate(), as modifying UI during onCreate() is
		 * unpopular..
		 */
		catch (Exception e)
		{
			e.printStackTrace();
			dictionary = new ArrayList<String>();
			Toast.makeText(
					context,
					"Fant ikke SD-kort. Sjekk innstillnger, og start app på nytt",
					Toast.LENGTH_LONG).show();
		}

		// If no dictionary present, load stops from xml-file.
		// Need separate file, as this only includes stops working with BussTUC
		try
		{

			if (dictionary.size() == 0 || !server)
			{
				System.out.println("No dictionary present!");
				gpsCoordinates = Helpers.readLines(getAssets().open(
						"gps3Mod.xml"));
				gpsCords = GPS.formatCoordinates(gpsCoordinates);
				dictionary = Helpers.createDictionary(gpsCords, "dictionary");
			}

		} catch (Exception e)
		{
			// System.exit(0);
		}

		textView = (AutoCompleteTextView) findViewById(R.id.autocomplete);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, dictionary);
		textView.setAdapter(adapter);
		textView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				queryOrSMS();

			}

		});

		textView.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				switch (keyCode)
				{
				case KeyEvent.KEYCODE_ENTER:
					if (!textView.getText().toString().equals(""))
					{
						queryOrSMS();

					}
				}
				return false;
			}
		});
	}

	private void createButtonListeners()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			final Button shortcutButtons = buttons[i];
			// If click, set text to text field, and start thread
			shortcutButtons.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					textView.setText(shortcutButtons.getText());
					queryOrSMS();

				}
			});

			// If long click, delete item
			shortcutButtons.setOnLongClickListener(new OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
							case DialogInterface.BUTTON_POSITIVE:
								String tmp = (String) shortcutButtons.getText();
								if (SDCard.deleteFileFromSD(tmp, "fav_routes"))
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
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage("Slette fil?")
							.setPositiveButton("Ja", dc)
							.setNegativeButton("Nei", dc).show();
					return true;
				}
			});

		}
	}

	private void updateTextViewHint()
	{
		if (fancyOracle)
		{
			textView.setHint("Kun destinasjon");
		}

		else
		{
			textView.setHint("Fullstendig setning");
		}
	}

	private void loadStops()
	{
		System.out.println("numstops: " + numStops + " numstopsonmap: "
				+ numStopsOnMap + " dist: " + dist);
		if (gpsCords != null)
		{
			busStopsNoDuplicates = Helpers.getLocationsArray(gpsCords,
					provider, currentlocation, dist, numStops, false);
		} else
			busStopsNoDuplicates = new ArrayList<BusStop>();

		long first = System.nanoTime();
		// Not needed when running on ReTro's server. Switched on or of by a
		// bool val
		// if(!server)busStopsNoDuplicates = Helpers.getLocationsArray(gpsCords,
		// provider, currentlocation, dist,numStops,false);

		// All stops
		allStops = Helpers.getAllLocations(gpsCords2, provider);
		long second = System.nanoTime() - first;
		System.out.println("LAT:" + currentlocation.getLatitude() + "LONG:"
				+ currentlocation.getLongitude());
		System.out.println("TIME SPENT SORTING SHIT: " + second
				/ (1000000000.0));

		System.out.println("USING " + numStops + " STOPS");
		// For use with the map, and real-time functionality
		cl = Helpers.getList(gpsCords2, provider, numStopsOnMap, dist,
				currentlocation);
		updateTextViewHint();
	}

	/*
	 * Validate input before sending to server
	 */
	public boolean validate(String input)
	{
		boolean check = false;
		for (int i = 0; i < allStops.size(); i++)
		{
			if (allStops.get(i).name.trim().equalsIgnoreCase(input.trim()))
			{
				check = true;
				break;
			}
		}

		return check;
	}

	private void createLocationListener()
	{
		locationListener = new LocationListener()
		{

			// This method runs whenever the criteria for change is met.
			@Override
			public void onLocationChanged(Location location)
			{
				System.out.println("LOCATIONLISTENER CALLED IN HOMESCREEN");
				currentlocation = location;
				// currentlocation.setLatitude(59.77075);
				// currentlocation.setLongitude(9.91087);
				// ila 10.367672,63.429256
				// 10.394555,63.43109
				// getSuggestionBasedOnPosition();
				loadStops();
				Toast.makeText(context, "Lokasjon oppdatert",
						Toast.LENGTH_SHORT).show();
				Log.v("currentLoc", "PROV:LOC=" + currentlocation.getLatitude()
						+ ":" + currentlocation.getLongitude());

			}

			@Override
			public void onProviderDisabled(String provider)
			{
				Log.v("PROV", "PROV:DISABLED");
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider)
			{
				Log.v("PROV", "PROV:ENABLED");

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{
				Log.v("PROV", "PROV:STATUSCHANGE");

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
			if (!server)
			{
				realTimeCodes = k_browser.realTimeData();

			}

			long rt2 = System.nanoTime() - rt;
			System.out.println("TIME SPENT REALTIMECODES: "
					+ (rt2 / 1000000000.0));
			// System.out.println("Realtinmecodessizefirst: " +
			// realTimeCodes.size());
			// Log.v("provider","provider:"+ provider);
		} catch (Exception e)
		{
			System.out.println("ERROR");
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			// First input dialog
			alert.setTitle("Tilkoblingsproblem");
			alert.setMessage("Ingen tilkobling, har du nettilgang?");
			alert.setPositiveButton("Avslutt",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton)
						{
							System.exit(0);

						}
					});
			alert.show();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		System.out.println("OnActivityResult()");
		super.onActivityResult(requestCode, resultCode, data);
		boolean change = false;
		if (resultCode == Activity.RESULT_OK)
		{
			Bundle extras = data.getExtras();
			int m_numStops = extras.getInt("num1");
			int m_numStopsOnMap = extras.getInt("num2");
			int m_dist = extras.getInt("num3");
			boolean m_fancy = extras.getBoolean("Orakelvalg");

			if (numStops != m_numStops && m_numStops <= 5)
			{
				numStops = extras.getInt("num1");
				change = true;
			}
			if (m_numStopsOnMap != numStopsOnMap && m_numStopsOnMap <= 20)
			{
				numStopsOnMap = extras.getInt("num2");
				change = true;
			}
			if (m_dist != dist && m_dist <= 1000)
			{
				dist = extras.getInt("num3");
				change = true;
			}
			if (!fancyOracle && m_fancy || fancyOracle && !m_fancy)
			{
				fancyOracle = m_fancy;
				change = true;
			}

			if (change)
				Toast.makeText(context,
						"Endringer trer i kraft ved neste lokasjonssjekk",
						Toast.LENGTH_LONG).show();

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
			alert.setPositiveButton("Lagre",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton)
						{

							String value = input.getText().toString();
							ArrayList<String> queries = new ArrayList<String>();
							fav.setQuery(value);
							queries.add(value);
							// For now, store query as filename, as the file
							// does not contain anything else
							try
							{
								if (SDCard.generateNoteOnSD(fav.getQuery(),
										queries, "fav_routes"))
								{
									updateButtons(busStop, buttons);
								}
							}

							catch (Exception e)
							{
								e.printStackTrace();
								Toast.makeText(
										context,
										"Fikk ikke skrevet til SD-kort. Er det montert?",
										Toast.LENGTH_LONG).show();
							}

						}
					});
			alert.show();
			return true;

		case R.id.map:
			new MapThread(context).execute();
			return true;

		case R.id.realtime:
			try
			{
				long time = System.nanoTime();
				ArrayList<ClosestStopOnMap> holder = new ArrayList<ClosestStopOnMap>();
				for (int i = 0; i < cl.length; i++)
				{
					holder.add(cl[i]);
				}
				Intent intent = new Intent(context, RealTimeList.class);
				intent.putParcelableArrayListExtra("test", holder);
				context.startActivity(intent);
				Long newTime = System.nanoTime() - time;
				System.out.println("TIME LOOKUP: " + newTime / 1000000000.0);
			} catch (Exception e)
			{
				Toast.makeText(context, "Kunne ikke hente ut sanntid",
						Toast.LENGTH_LONG).show();
			}
			return false;

		case R.id.speech:
			try
			{
			
					startVoiceRecognitionActivity();

			} catch (Exception e)
			{
				Toast.makeText(context, "Kunne ikke hente ut sanntid",
						Toast.LENGTH_LONG).show();
			}
			return false;

		case R.id.history:
			try
			{
				Intent history = new Intent(context, History.class);
				context.startActivity(history);
			} catch (Exception e)
			{
				Toast.makeText(context, "Klarte ikke hente ut historie",
						Toast.LENGTH_LONG).show();
			}
			return false;

		case R.id.setting:
			Intent intent = new Intent(context, Settings.class);
			startActivityForResult(intent, REQUEST_CODE);
			return false;
			// startVoiceRecognitionActivity();

		case R.id.about:
			try
			{
				Intent about = new Intent(context, About.class);
				context.startActivity(about);
			} catch (Exception e)
			{
				Toast.makeText(context, "Could not start activity",
						Toast.LENGTH_LONG).show();
			}
			return false;

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
		ArrayList<Route> buf;
		StringBuffer buffer = new StringBuffer();

		// StringBuffer buf = new StringBuffer();
		// ArrayList <String> buf = new ArrayList <String>();
		ProgressDialog myDialog = null;
		String noLoc = "Ingen lokasjon tilgjengelig. Sjekk dine innstillinger";
		String noRoutes = "Fant ingen ruter for søkekriterie. Sjekk søkeord";
		String noInternet = "Ingen internettilgang, har du skrudd av Wifi/3G?";
		boolean noLocCheck = false;
		boolean validated = false;
		boolean empty = false;

		public OracleThread(Context context)
		{
			this.context = context;

		}

		@Override
		protected Void doInBackground(Void... params)
		{
			long time = System.nanoTime();
			if (currentlocation == null)
			{
				noLocCheck = true;
				myDialog.dismiss();
			} else if (sms && fancyOracle)
			{

				Helpers.sendSMS("2027", "rute "
						+ cl[0].getStopName().toString() + " til "
						+ textView.getText().toString(), context);
				System.out.println("SMS " + cl[0].getStopName().toString()
						+ " til " + textView.getText().toString());

			}

			else if (sms && !fancyOracle)
			{
				Helpers.sendSMS("2027",
						"rute " + textView.getText().toString(), context);
				System.out.println("SMS " + cl[0].getStopName().toString()
						+ " til " + textView.getText().toString());

			}

			else
			{
				try
				{
					String query = textView.getText().toString().trim();
					if (!server && fancyOracle)
					{
						// System.out.println("Her skal vi ikke havne");
						if (!query.equals(""))
						{
							buf = Helpers.run(query, busStopsNoDuplicates,
									k_browser, realTimeCodes);
						} else
						{
							empty = true;
						}
					} else if (!fancyOracle)
					{
						// System.out.println("Her skal vi ikke havne");
						if (!query.equals(""))
						{
							System.out.println("FUBAR");
							buffer = Helpers.runStandard(query);
						} else
							empty = true;

					} else
					{
						if (!query.equals(""))
						{
							long pre = System.nanoTime();
							buf = Helpers.runServer(query, k_browser,
									currentlocation, numStops, dist, context);
							long post = System.nanoTime() - pre;
							System.out.println("POST-TIME: "
									+ (post / 1000000000.0));
							validated = true;
						} else
							empty = true;

					}
					long newTime = System.nanoTime() - time;
					System.out
							.println("TIME ORACLE: " + newTime / 1000000000.0);

				} catch (Exception e)
				{
					myDialog.dismiss();
					e.printStackTrace();
					ArrayList<String> err = new ArrayList<String>();
					err.add(e.toString());
					SDCard.generateNoteOnSD("errorHomeScreen::ORACLETHREAD",
							err, "errors");
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
			if (sms)
				Toast.makeText(context, "Venter på svar...", Toast.LENGTH_LONG)
						.show();

			myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");
			myDialog.setCancelable(true);
			myDialog.setOnCancelListener(new OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					finish();
					System.exit(0);

				}
			});
			textView.setEnabled(false);
			goButton.setEnabled(false);

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			myDialog.dismiss();

			if (empty)
			{
				Toast.makeText(context, "Tom input!", Toast.LENGTH_SHORT)
						.show();

			} else if (buf != null && fancyOracle && !sms)
			{
				// Error returned from bussTUC
				if (buf.get(0).getBusStopName()
						.equalsIgnoreCase("Bussorakelet"))
				{
					Toast.makeText(context, noRoutes, Toast.LENGTH_LONG).show();
				}

				// No interenet connection
				else if (buf.get(0).getBusStopName()
						.equalsIgnoreCase("Nettilgang"))
				{
					Toast.makeText(context, noInternet, Toast.LENGTH_LONG)
							.show();

				}
				// No location
				else if (noLocCheck)
				{
					Toast.makeText(context, noLoc, Toast.LENGTH_LONG).show();

				} else
				{
					double lat = currentlocation.getLatitude();
					double lon = currentlocation.getLongitude();

					Cursor areas = dbHelper.getAreaId(lat, lon);
					int area = 0;
					if (areas.getCount() == 0)
					{
						area = dbHelper.AddArea(lat + 0.01, lat - 0.01,
								lon + 0.01, lon - 0.01);
					} else
					{
						areas.moveToFirst();
						area = areas.getInt(0);
					}
					Cursor a = dbHelper.getArea(area);
					a.moveToFirst();
					System.out.println(a.getDouble(1) + "-" + a.getDouble(2));
					dbHelper.AddQuery(new Query(area, textView.getText()
							.toString(), Helpers.minutesFromDate(new Date()),
							new Date().getDay()));
					System.out.println("Starting activity");
					Intent intent = new Intent(getApplicationContext(),
							Answer.class);
					intent.putParcelableArrayListExtra("test", buf);

					// intent.putExtra("test", buf);
					context.startActivity(intent);
				}
			}

			else if (!fancyOracle && buffer != null && !sms)
			{
				Intent intent = new Intent(getApplicationContext(),
						Answer.class);
				intent.putExtra("text", buffer.toString());
				System.out.println("Started activity");
				// intent.putExtra("test", buf);
				context.startActivity(intent);
			} else
			{
				myDialog.dismiss();

				if (server && !validated && !sms)
				{

					Toast.makeText(
							context,
							"Ugyldig input, query ikke stilt. Sjekk orakelinnstillinger i menyen",
							Toast.LENGTH_LONG).show();

				}

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
			} catch (Exception e)
			{
				e.printStackTrace();
				ArrayList<String> err = new ArrayList<String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorHomeScreen:MapTHREAD", err,
						"errors");
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
		boolean check = false;

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

			} catch (Exception e)
			{
				System.out.println("Exception creating locManager");
				myDialog.dismiss();
				check = true;
			}

			return null;
		}

		@Override
		protected void onPreExecute()
		{

			try
			{
				myDialog = ProgressDialog.show(context, "Loading!",
						"Laster holdeplasser");
				myDialog.setCancelable(true);
				myDialog.setOnCancelListener(new OnCancelListener()
				{

					@Override
					public void onCancel(DialogInterface dialog)
					{
						finish();
						System.exit(0);

					}
				});
				createLocationListener();

				// Only request updates if > 500 ms and 10 m
			} catch (Exception e)
			{
				e.printStackTrace();
				myDialog.dismiss();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{

			myDialog.dismiss();
			if (check)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				// First input dialog
				alert.setTitle("Tilkoblingsproblem");
				alert.setMessage("Ingen tilkobling, har du nettilgang?");
				alert.setPositiveButton("Avslutt",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								System.exit(0);

							}
						});
				alert.show();
			} else
			{
				locationManager.requestLocationUpdates(provider, 500, 10,
						locationListener);
				new LocationListenerThread(context).execute();
			}

		}
	}

	/*
	 * Make sure we get location before we can continue. Is in separate thread
	 * to ensure all necessary objects are created on forehand. If not, we can
	 * end up in an eternal loop.
	 */
	class LocationListenerThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		Intent intent;
		ProgressDialog myDialog = null;
		boolean noLoc = false;

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
					if (currentlocation != null)
					{
						locCheck = true;
					}

				}

			} catch (Exception e)
			{
				myDialog.dismiss(); //
			}

			return null;
		}

		@Override
		protected void onPreExecute()
		{

			try
			{
				myDialog = ProgressDialog.show(context, "Loading!",
						"Setter lokasjon");
				myDialog.setCancelable(true);
				myDialog.setOnCancelListener(new OnCancelListener()
				{

					@Override
					public void onCancel(DialogInterface dialog)
					{
						finish();
						System.exit(0);

					}
				});

			} catch (Exception e)
			{
				e.printStackTrace();
				myDialog.dismiss();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{

			myDialog.dismiss();
			if (!server)
				busStopsNoDuplicates = Helpers.getLocationsArray(gpsCords,
						provider, currentlocation, dist, numStops, false);

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
				switch (which)
				{
				case DialogInterface.BUTTON_POSITIVE:
					((Activity) context).finish();
					ext.release();
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

	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		int c = dbHelper.getQueryCount();
		this.setTitle("AndroidAmble - " + c + " Søk gjort");
		super.onResume();
		// editText.setEnabled(true);
		textView.setEnabled(true);
		goButton.setEnabled(true);

		try
		{
			// Sets the restrictions on the location update. If no
			// locationmanager object exists, error message is sent to user,
			// requiring closing of app
			locationManager.requestLocationUpdates(
					locationManager.NETWORK_PROVIDER, 10, 1, locationListener);
		} catch (Exception e)
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			// First input dialog
			alert.setTitle("Tilkoblingsproblem");
			alert.setMessage("Ingen tilkobling, har du nettilgang?");
			alert.setPositiveButton("Avslutt",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton)
						{
							System.exit(0);

						}
					});
			alert.show();
		}

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}

	private String getSuggestionBasedOnPosition()
	{
		double lat = currentlocation.getLatitude();
		double lon = currentlocation.getLongitude();
		int time = Helpers.minutesFromDate(new Date());
		int day = new Date().getDay();
		Cursor areas = dbHelper.getQueryFromArea(lat, lon, time + 120,
				time - 120);

		System.out.println("FOUND " + areas.getCount() + " AREAS COVERING"
				+ lat + " - " + lon);
		String whereTo = "";
		ArrayList<Query> destinations = new ArrayList<Query>();

		if (areas.getCount() > 0)
		{
			areas.moveToFirst();
			while (!areas.isLast())
			{
				destinations.add(new Query(areas.getInt(2), areas.getString(1),
						areas.getInt(3), areas.getInt(4)));
				areas.moveToNext();
			}
			;
			destinations.add(new Query(areas.getInt(2), areas.getString(1),
					areas.getInt(3), areas.getInt(4)));
			whereTo += areas.getString(1);
		} else
		{
			whereTo = " et annet sted";
		}
		areas.close();
		for (Query q : destinations)
		{
			q.setEuclideanDistance(time, day);
		}
		Collections.sort(destinations);

		for (Query q : destinations)
		{
			System.out.println(q.toString());
		}
		// HashMap<String,Integer> sortedDestinations =
		// Helpers.getMostFrequentDestination(destinations);
		// System.out.println("NUMBER OF DESTINATIONS:" +
		// sortedDestinations.size());
		// for (Entry<String, Integer> entry : sortedDestinations.entrySet())
		// {
		// System.out.println("Key = " + entry.getKey() + ", Value = " +
		// entry.getValue());
		// }

		Toast.makeText(context, "Jeg tror du vil til: " + whereTo,
				Toast.LENGTH_LONG).show();
		return whereTo;
	}

	public class ActivitySwipeDetector implements View.OnTouchListener
	{

		static final String logTag = "ActivitySwipeDetector";
		private Activity activity;
		static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;
		LinearLayout ll = (LinearLayout) findViewById(R.id.homelayout);

		public ActivitySwipeDetector(Activity activity)
		{
			this.activity = activity;
		}

		public void onRightToLeftSwipe()
		{
			Log.i(logTag, "RightToLeftSwipe!");
			if (currentBgColor > 0)
				currentBgColor--;
			line.setBackgroundColor(Color.parseColor(bgColors[currentBgColor]));
		}

		public void onLeftToRightSwipe()
		{
			Log.i(logTag, "LeftToRightSwipe!");
			if (currentBgColor < (bgColors.length - 1))
				currentBgColor++;
			line.setBackgroundColor(Color.parseColor(bgColors[currentBgColor]));
		}

		public void onTopToBottomSwipe()
		{
			Log.i(logTag, "onTopToBottomSwipe!");

		}

		public void onBottomToTopSwipe()
		{
			Log.i(logTag, "onBottomToTopSwipe!");

		}

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
			{
				downX = event.getX();
				downY = event.getY();
				return true;
			}
			case MotionEvent.ACTION_UP:
			{
				upX = event.getX();
				upY = event.getY();

				float deltaX = downX - upX;
				float deltaY = downY - upY;

				// swipe horizontal?
				if (Math.abs(deltaX) > MIN_DISTANCE)
				{
					// left or right
					if (deltaX < 0)
					{
						this.onLeftToRightSwipe();
						return true;
					}
					if (deltaX > 0)
					{
						this.onRightToLeftSwipe();
						return true;
					}
				} else
				{
					Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
							+ " long, need at least " + MIN_DISTANCE);
				}

				// swipe vertical?
				if (Math.abs(deltaY) > MIN_DISTANCE)
				{
					// top or down
					if (deltaY < 0)
					{
						this.onTopToBottomSwipe();
						return true;
					}
					if (deltaY > 0)
					{
						this.onBottomToTopSwipe();
						return true;
					}
				} else
				{
					Log.i(logTag, "Swipe was only " + Math.abs(deltaX)
							+ " long, need at least " + MIN_DISTANCE);
				}

				return true;
			}
			}
			return false;
		}

	}

	private void startVoiceRecognitionActivity()
	{
		final HTTP http = new HTTP();
		final String cbr = getSuggestionBasedOnPosition();
		AlertDialog.Builder alert = new AlertDialog.Builder(this); // First
		alert.setTitle("Snakk i vei");
		alert.setMessage("Trykk når du er ferdig");
		alert.setPositiveButton("Avslutt",
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int whichButton)
					{
						stopRecording = true;
						ext.stop();
						long first = System.nanoTime();
						DummyObj dummy = http.sendPost(filePath2, cbr);
						long sec = System.nanoTime() - first;
						Toast.makeText(
								context,
								"You said: " + dummy.getAnswer() + " " + sec
										/ 1000000000.0 + " sec",
								Toast.LENGTH_LONG).show();
						textView.setText(dummy.getAnswer());
						new OracleThread(context).execute();

					}
				});
		alert.show();

		stopRecording = false;

		new RecordThread(context).execute();
	}

	class RecordThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		Intent intent;
		ProgressDialog myDialog = null;
		boolean check = false;

		public RecordThread(Context context)
		{

			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			while (!stopRecording)
				ext.record();
			return null;

		}

		@Override
		protected void onPreExecute()
		{

			try
			{
				filePath = "/sdcard/dictionary/liverpool.wav";
				filePath2 = "/dictionary/liverpool.wav";

				ext.setOutputFile(filePath);
				ext.prepare();
				ext.start();

			} catch (Exception e)
			{
				e.printStackTrace();
				myDialog.dismiss();

			}

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			ext.reset();
		}
	}
}
