/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package test.BusTUC.Main;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import test.BusTUC.R;
import test.BusTUC.Database.DatabaseHelper;
import test.BusTUC.GPS.GPS;
import test.BusTUC.Main.Homescreen.OracleThread;
import test.BusTUC.Stops.BusStop;

public class OtherBusstop extends Activity
{
	public static String[][] gpsCords;
	AutoCompleteTextView textView;
	Button button;
	Context context;
	String[] items;
	ArrayList<Integer> code;
	LinearLayout ll;
	ListView lv;
	DatabaseHelper dbHelper;
	String[] columns;
	SimpleCursorAdapter mAdapter;
	int[] to;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.otherbusstop);
		lv = (ListView) findViewById(R.id.listViewMongo);
		lv.setBackgroundColor(Color.parseColor("#FFFFFF"));
		lv.setCacheColorHint(Color.parseColor("#FFFFFF"));
		lv.setClickable(true);
		// Hide keyboard on start
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// ll = (LinearLayout) findViewById(R.layout.realtimestop);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3)
			{
				TextView tv = (TextView) v.findViewById(R.id.stopName);
				TextView tv2 = (TextView) v.findViewById(R.id.toFrom);
				ArrayList<Integer> stopName = findCodeFromStopName(tv.getText()
						.toString());
				int realtimecode = 0;

				for (int code : stopName)
				{
					System.out.println("KODE:" + code);
					if (Integer.parseInt(String.valueOf(code).substring(4, 5)) == 1
							&& tv2.getText().toString()
									.equalsIgnoreCase("Mot Sentrum"))
					{
						realtimecode = code;
					} else if (Integer.parseInt(String.valueOf(code).substring(
							4, 5)) == 0
							&& tv2.getText().toString()
									.equalsIgnoreCase("Fra Sentrum"))
					{
						realtimecode = code;
					}
				}
				Intent intent = new Intent(context, RealTimeListFromMenu.class);
				int outgoing = 0;

				System.out
						.println(realtimecode + ":" + tv.getText().toString());
				intent.putExtra("key", realtimecode);
				intent.putExtra("stopName", tv.getText().toString());
				startActivity(intent);

				// TODO Auto-generated method stub

			}
		});

		String[] gpsCoordinates;
		try
		{
			if (Homescreen.gpsCords2 != null)
			{
				gpsCords = Homescreen.gpsCords2;
			} else
			{
				gpsCoordinates = Helpers.readLines(this.getAssets().open(
						"gps3.xml"));
				gpsCords = GPS.formatCoordinates(gpsCoordinates);
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		button = (Button) findViewById(R.id.goButton2);
		ArrayList<String> dictionary = Helpers.getDictionary("dictionary2",
				"dictionaryAll");
		if (dictionary.size() == 0)
		{
			dictionary = Helpers.createDictionary(gpsCords, "dictionaryAll");
			// Only for oracle. Uncomment if system is not used with ReTro's
			// server
		}
		textView = (AutoCompleteTextView) findViewById(R.id.autocomplete2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, dictionary);
		textView.setAdapter(adapter);
		textView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				run();
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
						run();
					}
				}
				return false;
			}
		});
		context = this;

		dbHelper = new DatabaseHelper(this);
		Cursor cursor = dbHelper.getAllRealtime();
		startManagingCursor(cursor);
		// the desired columns to be bound
		columns = new String[]
		{ DatabaseHelper.stopName, DatabaseHelper.toFrom };
		// the XML defined views which the data will be bound to
		to = new int[]
		{ R.id.stopName, R.id.toFrom };

		// create the adapter using the cursor pointing to the desired data as
		// well as the layout information
		mAdapter = new SimpleCursorAdapter(this, R.layout.realtimestop, cursor,
				columns, to);
		// set this adapter as your ListActivity's adapter
		lv.setAdapter(mAdapter);

		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				run();
			}
		});

	}

	private void run()
	{
		code = findCodeFromStopName(textView.getText().toString());
		items = new String[code.size()];
		for (int i = 0; i < code.size(); i++)
		{
			if (Integer.parseInt(String.valueOf(code.get(i)).substring(4, 5)) == 1)
			{
				items[i] = "Mot sentrum";
			} else
			{
				items[i] = "Fra sentrum";
			}
		}
		// final String [] items=new String
		// []{"Item 1","Item 2","Item 3","Item 4"};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(textView.getText().toString());

		builder.setItems(items, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				DatabaseHelper dbHelper = new DatabaseHelper(context);
				dbHelper.addRealTime(textView.getText().toString(),
						items[which]);
				Intent intent = new Intent(context, RealTimeListFromMenu.class);
				int outgoing = 0;

				intent.putExtra("key", code.get(which));
				intent.putExtra("stopName", textView.getText().toString());

				startActivity(intent);
			}
		});

		builder.show();
		/*
		 * int outgoing =
		 * Integer.parseInt(Homescreen.realTimeCodes.get(code).toString());
		 * 
		 * intent.putExtra("stopId", outgoing); intent.putExtra("key", code);
		 * intent.putExtra("stopName", textView.getText().toString());
		 * 
		 * startActivity(intent);
		 */

	}

	private ArrayList<Integer> findCodeFromStopName(String stopname)
	{
		ArrayList<Integer> stops = new ArrayList<Integer>();
		for (BusStop s : Homescreen.allStops)
		{
			if (s.name.equalsIgnoreCase(stopname.trim()))
				stops.add(s.stopID);
		}
		return stops;
	}
}
