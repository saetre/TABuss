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

package org.ubicompforall.BusTUC.Speech;

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
