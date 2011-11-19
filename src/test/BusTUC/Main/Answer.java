package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import test.BusTUC.R;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.Stops.BusSuggestion;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Answer extends  ListActivity{

	private ArrayAdapter<String> ad;
	private ArrayList <Route> value;
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

	public Answer()
	{

	}



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//showButton = (Button)this.findViewById(R.id.showinmap);
		super.onCreate(savedInstanceState);
		context = this;
		value = new ArrayList<Route>();
		String textContent;
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		//setContentView(R.layout.list_item);

		Bundle extras = getIntent().getExtras();

		if(extras !=null)  
		{

			value = extras.getParcelableArrayList("test");	
			textContent = extras.getString("text");
			// Parse extracted into answer

			


			if(value != null)

			{
				ArrayList <BusSuggestion> sug = Helpers.parseDataObject(value);

				busSuggestions = new ArrayList<HashMap<String,Object>>();

				for(BusSuggestion bs : sug)
				{

					hm = new HashMap<String, Object>();
					hm.put("busNumber", bs.line);
					hm.put("origin", bs.origin);
					hm.put("destination",bs.destination);
					hm.put("departuretime", bs.departuretime);
					hm.put("arrivaltime",bs.arrivaltime);
					hm.put("transfer",bs.isTransfer);
					busSuggestions.add(hm);
				}


			setListAdapter(new SimpleAdapter(context, busSuggestions, R.layout.suggestion,
			new String[]{"busNumber","origin","destination","departuretime","arrivaltime","transfer"}, new int[]{R.id.answerrouteNumber,R.id.answerorigin, R.id.answerdestination, R.id.origintime,
			R.id.arrivaltime, R.id.isTransfer}));
			}

			else if(!textContent.equals(""))
			{
				standardOracle = true;
				String[] tmp = new String[1];
				tmp[0] = textContent;
				ad = new ArrayAdapter<String>(this, R.layout.list_item, tmp);
				setListAdapter(ad);
			}




		}
		else
		{
			returnHome();
			this.finish();
		}


		//lv1.setAdapter(new ArrayAdapter<String>(this,R.layout.row, R.id.label,lv_arr));
		//selection=(TextView)findViewById(R.id.selection);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		if(!standardOracle)
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
			}
			catch(Exception e)
			{
				myDialog.dismiss();
				ArrayList <String> err = new ArrayList <String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorMapThreadFromAnswer", err, "errors");
				Toast.makeText(context, "Something shitty happened", Toast.LENGTH_LONG).show();

			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			intent = new Intent(context, BusTUCApp.class);
			intent.putParcelableArrayListExtra("test", value);
			intent.putExtra("pos", positionInTable);
			// 	intent.putExtra("test", value);
			myDialog = ProgressDialog.show(context, "Loading", "Vent nu!");

		}

		@Override
		protected void onPostExecute(Void unused)
		{
			myDialog.dismiss();

		}
	}






}
