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

package org.ubicompforall.BusTUC.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.ubicompforall.BusTUC.R;
import org.ubicompforall.BusTUC.Favourites.SDCard;
import org.ubicompforall.BusTUC.Speech.HTTP;
import org.ubicompforall.BusTUC.Stops.BusSuggestion;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Answer extends ListActivity
{

	private ArrayAdapter<String> ad;
	private ArrayList<Route> value;
	Object o;
	private Context context;

	// Variables needed for Parcelable
	private String arrivalTime;
	private String busStopName;
	private int busStopNumber;
	private int busNumber;
	private String travelTime;
	private String destination;
	private boolean transfer;
	private int walkingDistance;
	private int totalTime;
	private int positionInTable;
	private ArrayList<HashMap<String, Object>> busSuggestions;
	private HashMap<String, Object> hm;
	boolean standardOracle = false;
	private String sms;
	// SpeechSynthesis synthesis;
	private String answerText = "";
	private boolean speech = false;
	public Answer()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		String textContent;
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setBackgroundColor(Color.parseColor("#FFFFFF"));
		
		Bundle extras = getIntent().getExtras();

		if (extras != null)
		{

			value = extras.getParcelableArrayList("test");
			textContent = extras.getString("text");
			sms = extras.getString("sms");
			speech = extras.getBoolean("speech");
			// Parse extracted into answer

			if (value != null)

			{
				ArrayList<BusSuggestion> sug = Helpers.parseDataObject(value);

				busSuggestions = new ArrayList<HashMap<String, Object>>();

				for (BusSuggestion bs : sug)
				{

					hm = new HashMap<String, Object>();
					hm.put("busNumber", bs.line);
					hm.put("origin", bs.origin + "("+bs.walkingDistance+"m)");
					hm.put("destination", bs.destination);
					hm.put("departuretime", bs.departuretime);
					hm.put("arrivaltime", bs.arrivaltime);
					hm.put("transfer", bs.isTransfer);
					busSuggestions.add(hm);
					answerText += "Buss " + bs.line + " går fra " + bs.origin
							+ " klokka "
							+ bs.arrivaltime + ", og ankommer " + bs.destination + " klokka " + bs.departuretime + ".";

				}

				setListAdapter(new SimpleAdapter(context, busSuggestions,
						R.layout.suggestion, new String[]
						{ "busNumber", "origin", "destination",
								"departuretime", "arrivaltime", "transfer" },
						new int[]
						{ R.id.answerrouteNumber, R.id.answerorigin,
								R.id.answerdestination, R.id.origintime,
								R.id.arrivaltime, R.id.isTransfer }));
				String ttsText = answerText;
		
			}

			else if (textContent != null)
			{
				standardOracle = true;
				String[] tmp = new String[1];
				tmp[0] = textContent;
				ad = new ArrayAdapter<String>(this, R.layout.list_item, tmp);
				setListAdapter(ad);
			}

			else if (sms != null)
			{
				standardOracle = true;
				String[] tmp = new String[1];
				tmp[0] = sms;
				ad = new ArrayAdapter<String>(this, R.layout.list_item, tmp);
				setListAdapter(ad);
			}

		} else
		{
			returnHome();
			this.finish();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		if (!standardOracle && !speech)
		{
			o = this.getListAdapter().getItem(position);
			System.out.println("TRYKKET PÅ POSISJON: " + position);
			positionInTable = position;
			new MapThread(context).execute();
		}

	}

	public void returnHome()
	{
		Intent intent = new Intent(context, Homescreen.class);
		context.startActivity(intent);
	}

	@Override
	public void onBackPressed()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/tts");
		File file = new File(dir, "tmp.wav");
		file.delete();
		this.finish();
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
				context.startActivity(intent);
			} catch (Exception e)
			{
				myDialog.dismiss();
				ArrayList<String> err = new ArrayList<String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorMapThreadFromAnswer", err,
						"errors");
				Toast.makeText(context, "Something shitty happened",
						Toast.LENGTH_LONG).show();

			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			intent = new Intent(context, BusTUCApp.class);
			intent.putParcelableArrayListExtra("test", value);
			intent.putExtra("pos", positionInTable);
			// intent.putExtra("test", value);
			myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			myDialog.dismiss();

		}
	}

	// Menu properties
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu3, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection

		switch (item.getItemId())
		{
		case R.id.tts:
			new ttsThread(context).execute();
			return true;

			// Add other menu items
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	class ttsThread extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		Intent intent;
		ProgressDialog myDialog = null;
		HTTP http;
		MediaPlayer mp;
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/tts");
		File file = new File(dir, "tmp.wav");

		public ttsThread(Context context)
		{

			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				System.out.println("ANSWER: " + answerText);
				if(!file.exists())http.sendGetTTS(answerText);
			} catch (Exception e)
			{
				myDialog.dismiss();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			http = new HTTP();
			myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			myDialog.dismiss();
			
			try
			{
				mp = new MediaPlayer();
				if (mp.isPlaying())
					mp.reset();
				mp.setDataSource(file.getAbsolutePath());
				mp.prepare();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener()
			{

				@Override
				public void onCompletion(MediaPlayer mp)
				{
					// TODO Auto-generated method stub
					mp.release();
				}

			});

		}
	}

}
