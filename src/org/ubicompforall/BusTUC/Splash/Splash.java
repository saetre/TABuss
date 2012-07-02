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

package org.ubicompforall.BusTUC.Splash;


import org.ubicompforall.BusTUC.Main.Homescreen;
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