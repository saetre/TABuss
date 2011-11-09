package test.BusTUC.Splash;


import test.BusTUC.Main.Homescreen;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class Splash extends Activity {
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);	    
	    //setContentView(R.layout.splash);

	    new HomeThread().execute();
	}

	
 private class HomeThread extends AsyncTask<Void, Void, Void>
    {
        Intent intent;
		ProgressDialog myDialog = null;
        public HomeThread()
        {
        	
        }

        @Override
        protected Void doInBackground(Void... params)
        {

        	intent = new Intent(Splash.this, Homescreen.class);        
        	
        	return null;
        }
        
        @Override
        protected void onPreExecute()
        {
			myDialog = ProgressDialog.show(Splash.this, "Loading", "Vent nu!");

        }

        @Override
       protected void onPostExecute(Void unused)
        {
        	startActivity(intent);
        	myDialog = null;
        	finish();
        }
    }  
	
}