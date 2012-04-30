package test.BusTUC.Speech;

import java.io.File;
import java.util.ArrayList;

import test.BusTUC.R;
import test.BusTUC.Main.Homescreen;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SpeechAnswer extends ListActivity
{
	private Context context;
	private ArrayList<String> answer;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		//setContentView(R.layout.list_item);

		// Get the extras from the Homescreen activity
		final Bundle extras = getIntent().getExtras();
		final String speechAnsw = extras.getString("speech");
		answer = new ArrayList <String>();
	
		// Set up list adapter
		/*final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
				context, R.layout.list_item, answer);*/
		
		
		ListView lv = getListView();//(ListView) findViewById(R.layout.list);//
		final CustomAdapter listAdapter = new CustomAdapter(this,
				R.layout.list_item, answer);
		setupList(answer, speechAnsw, listAdapter);

		lv.setTextFilterEnabled(true);
		final HTTP http = new HTTP();
	
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// Get the user's coordinates from the Homescreen activity
			//	final double[] coords = extras.getDoubleArray("coords");
				//System.out.println("POS: " + position + " sAnsw: " + speechAnsw + " coords: " + coords);
				if (position == 0 && speechAnsw != null)// && coords != null)
				{
					AlertDialog.Builder alert2 = new AlertDialog.Builder(
							context);
					alert2.setMessage("U cool?");

					alert2.setPositiveButton("Yeah boy!",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									Intent intent = new Intent(context, Homescreen.class);
									intent.putExtra("newSpeechQuery", false);
									intent.putExtra("speechAnswer", speechAnsw);
									setResult(Activity.RESULT_OK, intent);
								//	context.startActivity(intent);
									finish();
								}
							});
					alert2.setNegativeButton("Hell no!",
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									Intent intent = new Intent(context, Homescreen.class);
									intent.putExtra("newSpeechQuery", true);
									setResult(Activity.RESULT_OK, intent);
								//	context.startActivity(intent);
									finish();
									
									// Black list unwanted suggestion
								/*CBRAnswer newAnswer = blackList(coords[0],
											coords[1], context, http);
									if (newAnswer != null)
									{
										// Update list
										listAdapter.clear();
										setupList(answer, speechAnsw,
												newAnswer, listAdapter);
										listAdapter.notifyDataSetChanged();
									}*/
								}
							});

					alert2.show();
				}

			}
		});
		

	}


	
	
	public void setupList(ArrayList<String> answer, String speechAnsw,
			ArrayAdapter<String> listAdapter)
	{
		if (speechAnsw != null  && answer != null)
		{
			answer.clear();
			answer.add(speechAnsw);
			setListAdapter(listAdapter);
		}
	}

	/*public CBRAnswer blackList(double lat, double lon, Context context,
			HTTP http)
	{
		return http.blackList(lat, lon, current.getAnswer(), context);
	}*/
}
