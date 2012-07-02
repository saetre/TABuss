package org.ubicompforall.BusTUC.Speech;

import java.util.ArrayList;

import org.ubicompforall.BusTUC.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String>
{
	private ArrayList<String> users;
	private Context context;
	public CustomAdapter(Context context, int textViewResourceId,
			ArrayList<String> users)
	{
		super(context, textViewResourceId, users);
		this.users = users;
		this.context = context;
	}

	@Override  
	public View getView(int position, View view, ViewGroup viewGroup)
	{
	 View v = super.getView(position, view, viewGroup);
     Typeface font = Typeface.createFromAsset(context.getAssets(), "dotmatrix.ttf");  
	 ((TextView)v).setTypeface(font);
	 return v;
	}

}
