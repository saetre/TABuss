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
    			}
    			else
    			{
	    			System.out.println("Comparing: " + foo);
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
	    			   System.out.println("Go first");
	    			   
	    				minValues.add(currentValue);    		   
	    			   finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	    		   }
	    			else System.out.println("Val exists");
	    			exists = false;
    			}
    		}
    		else if(!maxLoc && currentValue < m && i != 0)
    		{
    			if(showOnMap)
    			{
    				minValues.add(currentValue);     			
     				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
    			}
    			else
    			{
	    			System.out.println("Comparing: " + foo);
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
	    				 System.out.println("Go second");
	     				minValues.add(currentValue);     			
	     				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	     				
	    			}
	    			else System.out.println("Val exists");
	    			exists = false;
    			}
    	
    		}
    		else if(maxLoc)
    		{
    			if(currentValue < minValues.last())
    			{
    				if(showOnMap)
        			{
    					 System.out.println("Go third");
 	    				minValues.remove(minValues.last());
 	    				finalMap.remove(minValues.last());
 	    				minValues.add(currentValue); 
 	    				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
        			}
    				else
    				{
	    				System.out.println("Comparing: " + foo);
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
	    					 System.out.println("Go third");
		    				minValues.remove(minValues.last());
		    				finalMap.remove(minValues.last());
		    				minValues.add(currentValue); 
		    				finalMap.put(currentValue, newMap.get(keys[y]).get(currentValue));
	    				}
	        			else System.out.println("Val exists");
	        			exists = false;
	    			}
    			}
    			
    		}

    	
    	if(i != 0 && m != 0)
    	{
    		Object[] newkeys = finalMap.keySet().toArray();
    		Arrays.sort(newkeys);
    		for(int k = finalMap.size()-1;k>=i;k--)
    		{
    			finalMap.remove(newkeys[k]);
    		}
    	}
    	
    	}
    	return finalMap; 
    }
}
    
  