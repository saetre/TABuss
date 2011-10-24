package test.BusTUC.Main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import test.BusTUC.R;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RealTimeListFromMenu extends ListActivity
{
	public static String ID;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	  super.onCreate(savedInstanceState);
	  Bundle extras = getIntent().getExtras();
	  String stopName = extras.getString("tag");
	  ArrayList <String> foundStopsList = extras.getStringArrayList("test");
	
	  ListView lv = getListView();
	  TextView text = new TextView(this);
	  text.setText(stopName +"\n");
	  lv.addHeaderView(text);
	  lv.setTextFilterEnabled(true);
	  try
	  {
		 
		  // Show in list
		  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, foundStopsList));
	  }
	  catch(Exception e)
	  {
		  System.out.println("No routes found");
		  Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT);
	  }
	}



	
}
