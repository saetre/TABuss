package test.BusTUC.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/*import org.ispeech.SpeechSynthesis;
 import org.ispeech.SpeechSynthesisEvent;
 import org.ispeech.error.BusyException;
 import org.ispeech.error.InvalidApiKeyException;
 import org.ispeech.error.NoNetworkException;
 */
import test.BusTUC.R;
import test.BusTUC.Favourites.Favourite;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.Main.Homescreen.MapThread;
import test.BusTUC.Speech.HTTP;
import test.BusTUC.Speech.Listener;
import test.BusTUC.Stops.BusSuggestion;
import test.BusTUC.Stops.ClosestStopOnMap;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

	public Answer()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// showButton = (Button)this.findViewById(R.id.showinmap);
		super.onCreate(savedInstanceState);
		context = this;
		// value = new ArrayList<Route>();
		String textContent;
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// prepareTTSEngine();
		// setContentView(R.layout.list_item);

		Bundle extras = getIntent().getExtras();

		if (extras != null)
		{

			value = extras.getParcelableArrayList("test");
			textContent = extras.getString("text");
			sms = extras.getString("sms");

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
				answerText += ", Fred ut!";

				setListAdapter(new SimpleAdapter(context, busSuggestions,
						R.layout.suggestion, new String[]
						{ "busNumber", "origin", "destination",
								"departuretime", "arrivaltime", "transfer" },
						new int[]
						{ R.id.answerrouteNumber, R.id.answerorigin,
								R.id.answerdestination, R.id.origintime,
								R.id.arrivaltime, R.id.isTransfer }));
				String ttsText = answerText;
				/*
				 * try {
				 * synthesis.getTTSEngine().setVoice("eurnorwegianfemale");
				 * synthesis.speak(ttsText); } catch (BusyException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } catch
				 * (NoNetworkException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } finally { if(synthesis != null)
				 * synthesis.stop(); }
				 */
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

		// lv1.setAdapter(new ArrayAdapter<String>(this,R.layout.row,
		// R.id.label,lv_arr));
		// selection=(TextView)findViewById(R.id.selection);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		if (!standardOracle)
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

	/*
	 * private void prepareTTSEngine() { try { synthesis =
	 * SpeechSynthesis.getInstance(this); synthesis.setSpeechSynthesisEvent(new
	 * SpeechSynthesisEvent() {
	 * 
	 * public void onPlaySuccessful() { }
	 * 
	 * public void onPlayStopped() { }
	 * 
	 * public void onPlayFailed(Exception e) { e.printStackTrace(); }
	 * 
	 * public void onPlayStart() { }
	 * 
	 * @Override public void onPlayCanceled() { }
	 * 
	 * 
	 * });
	 * 
	 * //synthesis.setVoiceType("usenglishfemale1"); // All the values available
	 * to you can be found in the developer portal under your account
	 * 
	 * } catch (InvalidApiKeyException e) { Toast.makeText(context,
	 * "ERROR: Invalid API key", Toast.LENGTH_LONG).show(); }
	 * 
	 * }
	 * 
	 * private class OnSpeakListener implements OnClickListener {
	 * 
	 * public void onClick(View v) {
	 * 
	 * try { String ttsText = answerText; synthesis.speak(ttsText);
	 * 
	 * } catch (BusyException e) { e.printStackTrace(); Toast.makeText(context,
	 * "ERROR: SDK is busy", Toast.LENGTH_LONG).show(); } catch
	 * (NoNetworkException e) { Toast.makeText(context,
	 * "ERROR: Network is not available", Toast.LENGTH_LONG).show(); } } }
	 * 
	 * 
	 * 
	 * public class OnStopListener implements OnClickListener {
	 * 
	 * public void onClick(View v) { if (synthesis != null) { synthesis.stop();
	 * } } }
	 */

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
				if(!file.exists())http.sendPostTTS(answerText);
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
