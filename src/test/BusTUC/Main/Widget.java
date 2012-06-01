package test.BusTUC.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import test.BusTUC.R;
import test.BusTUC.Speech.DummyObj;
import test.BusTUC.Speech.HTTP;
import test.BusTUC.Speech.MfccMaker;
import test.BusTUC.Speech.SpeechAnswer;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider
{

	public static final String DEBUG_TAG = "Widget";
	private static final int MY_NOTIFICATION_ID = 1234;

	// private PendingIntent service = null;
	public static String MY_WIDGET_UPDATE = "MY_OWN_WIDGET_UPDATE";
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
	

	@Override
	public void onReceive(Context context, Intent intent)
	{

		System.out.println("ONRECEIVE " + intent.getAction());
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action))
		{
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
			{
				this.onDeleted(context, new int[]
				{ appWidgetId });
			}
		} else
		{

			
			super.onReceive(context, intent);

		}

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		System.out.println("ONUPDATE");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		Intent configIntent = new Intent(context, WidgetClick.class);
		configIntent.setAction(ACTION_WIDGET_CONFIGURE);
		// configIntent.putExtra("which", "speech");

		Intent active2 = new Intent(context, Homescreen.class);
		active2.setAction(ACTION_WIDGET_RECEIVER);
		active2.putExtra("which", "answer");

		PendingIntent actionPendingIntent2 = PendingIntent.getActivity(
				context, 0, active2, 0);
		PendingIntent configPendingIntent = PendingIntent.getActivity(context,
				0, configIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.button, configPendingIntent);
		 remoteViews.setOnClickPendingIntent(R.id.text, actionPendingIntent2);
		Typeface clock = Typeface.createFromAsset(
				context.getAssets(),
				"dotmatrix.ttf");
		Bitmap foo = Widget.buildUpdate("TABuss", clock);
		remoteViews.setImageViewBitmap(R.id.text, foo);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

	}



	public static Bitmap buildUpdate(String input, Typeface clock)
	{
		Bitmap myBitmap = Bitmap
				.createBitmap(600, 300, Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(myBitmap);

		Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(clock);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.parseColor("#ffaa0e"));
		paint.setTextSize(150);
		paint.setTextAlign(Align.CENTER); 
		
		String[] s = input.split(" ");
		if (s.length > 1)
		{
			int offset = 80;
			for (String f : s)
			{
				myCanvas.drawText(f, 300, offset, paint);
				offset += 70;
			}

		} else
		{
			myCanvas.drawText("", 300, 80, paint);
			myCanvas.drawText(input, 300, 130, paint);
		}

		
	  //appWidgetManager.updateAppWidget(thisWidget,
		//		remoteViews);
		return myBitmap;
	}


}