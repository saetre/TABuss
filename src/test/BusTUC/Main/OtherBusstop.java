package test.BusTUC.Main;

import java.util.ArrayList;

import test.BusTUC.R;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Main.Homescreen.OracleThread;
import test.BusTUC.Stops.BusStop;

public class OtherBusstop extends Activity {
	public static String[][] gpsCords;
	AutoCompleteTextView textView;
	Button button;
	Context context;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otherbusstop);
		String[] gpsCoordinates = getResources().getStringArray(R.array.coords4);
		gpsCords = GPS.formatCoordinates(gpsCoordinates);
		button = (Button) findViewById(R.id.goButton2);
		ArrayList <String> dictionary = Helpers.createDictionary(gpsCords);
		textView = (AutoCompleteTextView) findViewById(R.id.autocomplete2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, dictionary);
		textView.setAdapter(adapter);
		context = this;
		
		
		

		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context, RealTimeListFromMenu.class);
				int code = findCodeFromStopName(textView.getText().toString());
				
				int outgoing = Integer.parseInt(Homescreen.realTimeCodes.get(code).toString());
				
				intent.putExtra("stopId", outgoing);
				intent.putExtra("key", code);
				intent.putExtra("stopName", textView.getText().toString());
				
				startActivity(intent);

			}
		});
		
	}
	private int findCodeFromStopName(String stopname){
		for(BusStop s : Homescreen.allStops){
			if(s.name.equalsIgnoreCase(stopname)) return s.stopID;
		}
		return -1;
	}
}
