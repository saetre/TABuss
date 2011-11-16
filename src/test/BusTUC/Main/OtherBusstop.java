package test.BusTUC.Main;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import test.BusTUC.R;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Main.Homescreen.OracleThread;
import test.BusTUC.Stops.BusStop;

public class OtherBusstop extends Activity {
	public static String[][] gpsCords;
	AutoCompleteTextView textView;
	Button button;
	Context context;
	String[] items;
	ArrayList<Integer> code;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otherbusstop);
		String[] gpsCoordinates;
		try {
			gpsCoordinates = Helpers.readLines(context.getAssets().open("gps3Mod.xml"));
			gpsCords = GPS.formatCoordinates(gpsCoordinates);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
				
				code = findCodeFromStopName(textView.getText().toString());				
				items=new String[code.size()];
				for(int i=0;i<code.size();i++){
					if(Integer.parseInt(String.valueOf(code.get(i)).substring(4, 5)) == 1){
						items[i] = "Mot sentrum";
					}else{
						items[i] = "Fra sentrum";
					}
				}
				//final String [] items=new String []{"Item 1","Item 2","Item 3","Item 4"};
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
				builder.setTitle(textView.getText().toString());
				
				builder.setItems(items, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					int outgoing = Integer.parseInt(Homescreen.realTimeCodes.get(code.get(which)).toString());
					Intent intent = new Intent(context, RealTimeListFromMenu.class);
					intent.putExtra("stopId", outgoing);
					intent.putExtra("key", code.get(which));
					intent.putExtra("stopName", textView.getText().toString());
					
					startActivity(intent);
				}
				});
				
				builder.show();
				/*int outgoing = Integer.parseInt(Homescreen.realTimeCodes.get(code).toString());
				
				intent.putExtra("stopId", outgoing);
				intent.putExtra("key", code);
				intent.putExtra("stopName", textView.getText().toString());
				
				startActivity(intent);*/

			}
		});
		
	}
	private ArrayList<Integer> findCodeFromStopName(String stopname){
		ArrayList<Integer> stops = new ArrayList<Integer>();
		for(BusStop s : Homescreen.allStops){
			if(s.name.equalsIgnoreCase(stopname)) stops.add(s.stopID);
		}
		return stops;
	}
}
