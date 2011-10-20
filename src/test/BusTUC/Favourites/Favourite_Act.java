package test.BusTUC.Favourites;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import test.BusTUC.R;
import test.BusTUC.R.id;
import test.BusTUC.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class Favourite_Act extends ListActivity
{
	private  static String ID;
	private Favourite fav;
	private ArrayAdapter <String> ad;
	private List <String> itemList;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	  super.onCreate(savedInstanceState);
	  itemList =  new ArrayList<String>();
	  
	  for(int i=0; i< getFilesFromSD().size(); i++)
	  {
		  System.out.println("ADDING: " + getFilesFromSD().get(i));
		  itemList.add(getFilesFromSD().get(i));
	  }
	  

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);	
	  ad = new ArrayAdapter<String>(this, R.layout.list_item, itemList);
	  setListAdapter(ad);
	  System.out.println("LIST SET!");
	  lv.setOnItemLongClickListener(new OnItemLongClickListener()
	  {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// Delete from sd-card
			String tmp = (String) arg0.getItemAtPosition(arg2);
			if(deleteFileFromSD(tmp))
			{
				System.out.println("DELETED: " + tmp);
				 ad.remove(tmp);
	        	 ad.notifyDataSetChanged();
	          	setListAdapter(ad); 
				return true;
			}
			System.out.println("NOT DELETED: " + tmp);
			return false;
		}
	  });

	  //TextView text = new TextView(this);
	//  text.setText(ID +"\n" + "Kommende busser:");
	  //lv.addHeaderView(text);
	 
	 
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("test", o.toString());
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	
	// Menu properties
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu2, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	
        switch (item.getItemId()) {
        case R.id.addnew:
        	// Prompt user regarding destination
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	
        	fav = new Favourite();
        	// First input dialog 
        	alert.setTitle("Destinasjon");
        	alert.setMessage("Skriv inn destinasjon");        	
        	final EditText input = new EditText(this);
        	alert.setView(input);
        	alert.setPositiveButton("Lagre", new DialogInterface.OnClickListener() 
        	{
	        	public void onClick(DialogInterface dialog, int whichButton) 
	        	{
	        	
	        	  String value = input.getText().toString();
	        	  fav.setQuery(value);
	        	  // For now, store query as filename, as the file does not contain anything else
	        	  if(generateNoteOnSD(fav.getQuery(), fav.getQuery()))
	        	  {
		        	  ad.add(fav.getQuery());
		        	  ad.notifyDataSetChanged();
	        	  }
	          	  setListAdapter(ad);  
	          	
	        	 }
        	});
        	alert.show();
            return true;
        // Add other menu items
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
    	Intent returnIntent = new Intent();
		returnIntent.putExtra("test", "");
		setResult(RESULT_OK, returnIntent);
		finish();
    }
    
    
    public static boolean deleteFileFromSD(String fileName)
    {
    	
    	boolean deleted = false;
    	File[]files =  Environment.getExternalStorageDirectory().listFiles();
    	for(int i=0; i<files.length; i++)
    	{
    		if(files[i].getName().equalsIgnoreCase("fav_routes") && files[i].isDirectory())
    		{
    			System.out.println("FOUND DIRECTORY " + files[i].listFiles().length);
    			// In directory, now find files
    			//File dir = new File(files[i].getName());
    			for(int j=0; j<files[i].listFiles().length; j++)
    			{    				
    					
    				if(files[i].listFiles()[j].getName().equals(fileName))
    				{
    					deleted = files[i].listFiles()[j].delete();
    				}
						
				} 
					
    			
    		}
    	}
		return deleted;
    }
    public static  ArrayList <String> getFilesFromSD()
    {
    	ArrayList <String> fileContent = new ArrayList<String>();
    	FileInputStream fu;
    	String readString = "";
    	File[]files =  Environment.getExternalStorageDirectory().listFiles();
    	for(int i=0; i<files.length; i++)
    	{
    		if(files[i].getName().equalsIgnoreCase("fav_routes") && files[i].isDirectory())
    		{
    			System.out.println("FOUND DIRECTORY " + files[i].listFiles().length);
    			// In directory, now find files
    			//File dir = new File(files[i].getName());
    			for(int j=0; j<files[i].listFiles().length; j++)
    			{
    				//File file = new File(files[i].listFiles()[j].getName());
    				
    				try {
    					System.out.println("Streaming files: " + files[i].listFiles()[j].getName());
						fu = new FileInputStream(files[i].listFiles()[j]);
						BufferedReader buf = new BufferedReader(new InputStreamReader(fu));
						// Stream file content. Change to file name when file contains other properties
						while((readString = buf.readLine()) != null)
						{
							System.out.println("FOUND: " + readString);
							fileContent.add(readString);
						}
						buf.close();
						fu.close();
						
					} catch (FileNotFoundException e) {
						//System.out.println("DID NOT FIND: " + file.getAbsolutePath());
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
    				
    				
    			}
    			
    		}
    	}
    	return fileContent;
    }
    
    public static boolean generateNoteOnSD(String sFileName, String sBody)
    {
        try
        {
        	System.out.println("SAVE FILE");
            File root = new File(Environment.getExternalStorageDirectory(), "fav_routes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            System.out.println("FOUND DIRECTORY TO STORE IN");
            if(root.listFiles().length == 0)
            {
            	System.out.println("TRYING TO WRITE");
        		FileWriter writer = new FileWriter(gpxfile);
                writer.append(sBody);
                writer.flush();
                writer.close();
                //Toast.makeText(this, "Saved first if", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
            {
            	boolean exists= false;
	            for(int i=0; i<root.listFiles().length; i++)
	            {
	            	if(root.listFiles()[i].getName().equalsIgnoreCase(sFileName)) exists = true;                        	
	            	
	            }
	            if(!exists)
            	{
            		System.out.println("TRYING TO WRITE");
            		FileWriter writer = new FileWriter(gpxfile);
                    writer.append(sBody);
                    writer.flush();
                    writer.close();
                   // Toast.makeText(this, "Saved in else", Toast.LENGTH_SHORT).show();
                    return true;
            	}
            	
            	else
            	{
            		//Toast.makeText(this, "Already exists", Toast.LENGTH_SHORT).show();
            		return false;
            	}
            }

        }
        catch(IOException e)
        {
             e.printStackTrace();
            
        }
        return false;
       } 
	
	
	 

}
