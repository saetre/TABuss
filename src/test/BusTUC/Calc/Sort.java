package test.BusTUC.Calc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import android.location.Location;
import android.util.Log;

public class Sort 
{
	
    // Finds either N closest bus stops or the bus stops within a radius M
    public HashMap<Integer,Location> m_partialSort(HashMap<Integer,HashMap<Integer,Location>> lHMap, int i, int m, boolean maxLoc, boolean showOnMap)
    {
    	HashMap<Integer,HashMap<Integer,Location>> newMap = lHMap;
    	Log.v("sort","lHMap:"+lHMap.size());
    	HashMap<Integer,Location> finalMap = new HashMap<Integer,Location>();
    	TreeSet<Integer> minValues = new TreeSet<Integer>();
    	Object[] keys = newMap.keySet().toArray();
    	int currentValue = 0;
    	String check = "";
    	for(int y = 0;y<newMap.size();y++)
    	{    		
   			currentValue = Integer.parseInt(newMap.get(keys[y]).keySet().toArray()[0].toString());    
   			String foo = newMap.get(keys[y]).get(currentValue).getProvider();
   		//	System.out.println("CURRENT: " + newMap.get(keys[y]).get(currentValue).getProvider());
    		if(minValues.size() < i && maxLoc && currentValue < m)
    		{
    			if(showOnMap)
    			{
    				minValues.add(currentValue);    		   
     			   finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
     			//   System.out.println("Added: " + newMap.get(keys[y]).get(currentValue).getProvider());
    			}
    			else
    			{
	    			Object [] len = finalMap.keySet().toArray();
	    			boolean exists = false;
	    			for(int k = 0; k <finalMap.size(); k++)
	    			{
	    				if(finalMap.get(len[k]).getProvider().equalsIgnoreCase(foo))
	    				{
	    					exists = true;
	    				}
	    			}
	    			
	    			if(!exists)
	    			{
	    			   
	    				minValues.add(currentValue);    		   
	    			   finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	    		   }
	    			exists = false;
    			}
    		}
    		else if(!maxLoc && currentValue < m && i != 0)
    		{
    			if(showOnMap)
    			{
    				//System.out.println("TRYING VALUE: " + currentValue + "   " + newMap.get(keys[y]).get(currentValue));
    				minValues.add(currentValue);     			
     				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
      			//   System.out.println("Added: " + currentValue + "    "  + newMap.get(keys[y]).get(currentValue).getProvider() );

    			}
    			else
    			{
	    			Object [] len = finalMap.keySet().toArray();
	    			boolean exists = false;
	    			for(int k = 0; k <finalMap.size(); k++)
	    			{
	    				if(finalMap.get(len[k]).getProvider().equalsIgnoreCase(foo))
	    				{
	    					exists = true;
	    				}
	    			}
	    			
	    			if(!exists)
	    			{
	     				minValues.add(currentValue);     			
	     				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	     				
	    			}
	    			exists = false;
    			}
    	
    		}
    		else if(maxLoc)
    		{
    			if(currentValue < minValues.last())
    			{
    				if(showOnMap)
        			{
 	    				minValues.remove(minValues.last());
 	    				finalMap.remove(minValues.last());
 	    				minValues.add(currentValue); 
 	    				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
 	     		  // System.out.println("Added: " + newMap.get(keys[y]).get(currentValue).getProvider());

        			}
    				else
    				{
	        			Object [] len = finalMap.keySet().toArray();
	        			boolean exists = false;
	        			for(int k = 0; k <finalMap.size(); k++)
	        			{
	        				if(finalMap.get(len[k]).getProvider().equalsIgnoreCase(foo))
	        				{
	        					exists = true;
	        				}
	        			}
	        			
	        			if(!exists)
	        			{
		    				minValues.remove(minValues.last());
		    				finalMap.remove(minValues.last());
		    				minValues.add(currentValue); 
		    				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	    				}
	        			exists = false;
	    			}
    			}
    			
    		}

    	
    	/*if(i != 0 && m != 0)
    	{
    		Object[] newkeys = finalMap.keySet().toArray();
    		Arrays.sort(newkeys);
    		
    		for(int fu = 0; fu<finalMap.size(); fu++)
    		{
    			System.out.println("Sorted: " + newkeys[fu] + "   " +finalMap.get(newkeys[fu]).getProvider() );
    		}
    		for(int k = finalMap.size()-1;k>=i;k--)
    		{
    			//System.out.println("Removed: " +finalMap.get(newkeys[k]).getProvider());
    			finalMap.remove(newkeys[k]);


    		}
    	}*/
    	
    	}
    	
    	Object[] newkeys = finalMap.keySet().toArray();
		Arrays.sort(newkeys);
		
		for(int k=m; k<finalMap.size(); k++)
		{
			finalMap.remove(newkeys[k]);
		}
		
		for(int j =0; j<finalMap.size(); j++)
		{
			System.out.println("Elementer i lista: " + newkeys[j] + "  "  +finalMap.get(newkeys[j]).getProvider());
		}
    	return finalMap; 
    }
    
    
}
    
  