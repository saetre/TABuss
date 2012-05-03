package test.BusTUC.Main;

import test.BusTUC.R;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class MyService extends Service {

	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("ONSTARTCOMMAND");
		buildUpdate();

		return super.onStartCommand(intent, flags, startId);
	}

	private void buildUpdate() {

		RemoteViews remoteView = new RemoteViews(getPackageName(),
				R.layout.widget_layout);
		
		Typeface clock = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"dotmatrix.ttf");
		String input =  "Einar Tambarskjelves gate";
	
		Bitmap foo = Widget.buildUpdate(input, clock);
		//remoteView.setBitmap(R.id.text, "setBackgroundDrawable", foo);
		// remoteView.setTextViewText(R.id.label, "Test");
		
		remoteView.setImageViewBitmap(R.id.text,foo);

		//remoteView.setTextViewText(R.id.label, "GLØSHAUGEN");

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, Widget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, remoteView);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
