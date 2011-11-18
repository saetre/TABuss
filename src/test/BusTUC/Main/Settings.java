package test.BusTUC.Main;
import test.BusTUC.R;
import test.BusTUC.Database.DatabaseHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	// Default values. Same as set in Homescreen
	int numStops ;
	int numStopsOnMap ;
	int radius ;
	boolean fancyOracle = true;
	Context context;
	Button deletelog, deletert;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		context = this;
		// Set properties according to existing preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(Settings.this, R.layout.preference, false);
		String foo  = preferences.getString("num1", "");
		numStops = Integer.parseInt(foo);
		String foo2  = preferences.getString("num2", "");
		numStopsOnMap = Integer.parseInt(foo2);
		String foo3  = preferences.getString("num3", "");
		radius = Integer.parseInt(foo3);
		addPreferencesFromResource(R.layout.preference);
		setContentView(R.layout.deletelog);
		deletelog = (Button) findViewById(R.id.slettlogg);
		deletert = (Button) findViewById(R.id.slettandre);
		deletelog.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DatabaseHelper dbHelper = new DatabaseHelper(context);
				dbHelper.clearLog();
				Toast.makeText(context, "Slettet logg", Toast.LENGTH_SHORT).show();
			}
			
		});
		deletert.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DatabaseHelper dbHelper = new DatabaseHelper(context);
				dbHelper.clearRealtime();
				Toast.makeText(context, "Slettet sanntidshistorie", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		  final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("Orakelvalg");

		    checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() 
		    {            
		        public boolean onPreferenceChange(Preference preference, Object newValue) 
		        {
		        	System.out.println("CHANGED " + newValue);
		        	fancyOracle = (Boolean) newValue;
		            return true;
		        }
		    }); 
	}



	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) 
	{
		try
		{
			if(key.equals("num1"))
			{
				String foo  = arg0.getString(key, "");
				int temp = Integer.parseInt(foo);
				if(temp <=5)numStops = temp;
			}

			else if(key.equals("num2"))
			{
				String foo  = arg0.getString(key, "");
				int temp = Integer.parseInt(foo);
				if(temp <= 50)numStopsOnMap = temp;
			}

			else if(key.equals("num3"))
			{
				String foo  = arg0.getString(key, "");
				int temp = Integer.parseInt(foo);
				if(temp <= 10000)radius = Integer.parseInt(foo);
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
		.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onBackPressed()
	{
		System.out.println("BACKPRESSED");
		Intent intent = new Intent(context, Homescreen.class);
		intent.putExtra("num1", numStops);
		intent.putExtra("num2", numStopsOnMap);
		intent.putExtra("num3", radius);
		intent.putExtra("num4", fancyOracle);
		setResult(Activity.RESULT_OK, intent);
		super.onBackPressed();

	}

}
