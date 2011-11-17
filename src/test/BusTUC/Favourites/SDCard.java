package test.BusTUC.Favourites;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Environment;

public class SDCard 
{

    
    public static boolean deleteFileFromSD(String fileName, String folderName)
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
    public static  ArrayList <String> getFilesFromSD(String folderName)
    {
    	ArrayList <String> fileContent = new ArrayList<String>();
    	FileInputStream fu;
    	String readString = "";
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
    				//File file = new File(files[i].listFiles()[j].getName());
    				
    				try {
    					System.out.println("Streaming files: " + files[i].listFiles()[j].getName());
						fu = new FileInputStream(files[i].listFiles()[j]);
						BufferedReader buf = new BufferedReader(new InputStreamReader(fu));
						// Stream file content. Change to file name when file contains other properties
						while((readString = buf.readLine()) != null)
						{
							//System.out.println("FOUND: " + readString);
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
    
    public static boolean generateNoteOnSD(String sFileName, ArrayList <String> sBody, String folderName)
    {
        try
        {
        	System.out.println("SAVE FILE");
            File root = new File(Environment.getExternalStorageDirectory(), folderName);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            System.out.println("FOUND DIRECTORY TO STORE IN");
            if(root.listFiles().length == 0)
            {
            	System.out.println("TRYING TO WRITE");
        		FileWriter writer = new FileWriter(gpxfile);
        		for(int i=0; i<sBody.size(); i++)
        		{
        			writer.append(sBody.get(i));
        		}
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
            		for(int i=0; i<sBody.size(); i++)
            		{
            			writer.append(sBody.get(i));
            		}
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
