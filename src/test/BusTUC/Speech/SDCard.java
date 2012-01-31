package test.BusTUC.Speech;

import java.io.File;

import android.os.Environment;

public class SDCard
{
	public boolean deleteFileFromSD(String fileName, String folderName)
    {
    	
    	boolean deleted = false;
    	File[]files =  Environment.getExternalStorageDirectory().listFiles();
    	for(int i=0; i<files.length; i++)
    	{
    		if(files[i].getName().equalsIgnoreCase(folderName) && files[i].isDirectory())
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
}
