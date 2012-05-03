package test.BusTUC.Main;

import java.util.Calendar;

import test.BusTUC.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider {

	public static final String DEBUG_TAG = "TutWidgetProvider";
	private PendingIntent service = null;  
	public static String MY_WIDGET_UPDATE = "MY_OWN_WIDGET_UPDATE";
	public static Context c = null;
	
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);

		if (MY_WIDGET_UPDATE.equals(intent.getAction())) {
			Toast.makeText(context, "onReceiver()", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		 System.out.println("UPDATE WIDGET");
	        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
	        final Calendar TIME = Calendar.getInstance();
	        TIME.set(Calendar.MINUTE, 0);
	        TIME.set(Calendar.SECOND, 0);
	        TIME.set(Calendar.MILLISECOND, 0);

	        final Intent i = new Intent(context, MyService.class);  
	  
	        if (service == null)  
	        {  
	            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);  
	        }  
	  
	        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);  
	    
	  
	   
	
	//	super.onUpdate(context, appWidgetManager, appWidgetIds);

		/*try {
			updateWidgetContent(context, appWidgetManager);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

@Override  
public void onDisabled(Context context)  
{  
    final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  

    m.cancel(service);  
}  

	public static Bitmap buildUpdate(String input, Typeface clock) {
		Bitmap myBitmap = Bitmap.createBitmap(600, 500, Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(myBitmap);
		Paint paint = new Paint();
	
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(clock);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.parseColor("#ffaa0e"));
		paint.setTextSize(95);
		paint.setTextAlign(Align.CENTER);
		String[] s = input.split(" ");
		if(s.length > 1){
			int offset = 60;
			for(String f : s)
			{
				myCanvas.drawText(f,300, offset, paint);
				offset +=60;
			}
			
		}
		else myCanvas.drawText(input, 300, 60, paint);
		return myBitmap;
	}

	/*public static void updateWidgetContent(Context context,
			AppWidgetManager appWidgetManager) {

		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		// remoteView.setTextViewText(R.id.label, "Test");
		remoteView.setImageViewBitmap(R.id.label,
				//buildUpdate("Gløshaugen"));
		/*
		 * Intent launchAppIntent = new Intent(context, TutListActivity.class);
		 * PendingIntent launchAppPendingIntent =
		 * PendingIntent.getActivity(context, 0, launchAppIntent,
		 * PendingIntent.FLAG_UPDATE_CURRENT);
		 * remoteView.setOnClickPendingIntent(R.id.full_widget,
		 * launchAppPendingIntent);
		 * 
		 * ComponentName tutListWidget = new ComponentName(context,
		 * TutWidgetProvider.class);
		 * appWidgetManager.updateAppWidget(tutListWidget, remoteView);
		 */
	//}
}