package test.BusTUC.Calc;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import test.BusTUC.R;
import test.BusTUC.Main.Route;

import android.util.Log;

public class Calculate {

	 private JSONObject json_obj; 
	 private JSONArray json_arr; 

	 public Calculate()
	 {
	 }
	 public Route[] createRoutes(String jsonString)
	 {
		Route[] routeSuggestions = null; 
		try {
			json_obj = new JSONObject(jsonString);
			json_arr = new JSONArray(json_obj.getString("departures"));
		if(json_arr != null)
		{
		int ArrayLength = json_arr.length()-1; 
		Log.v("arraylength","ar:"+ArrayLength);
		routeSuggestions = new Route[ArrayLength];
		for(int i = 0;i<ArrayLength;i++)
		{
			Log.v("i","i:"+Integer.parseInt(json_arr.getJSONObject(i).getString("busstopnumber")));
			Log.v("i","i-TRANSFER:"+json_obj.get("transfer"));
			routeSuggestions[i] = new Route();
			routeSuggestions[i].setTransfer(Boolean.parseBoolean(json_obj.get("transfer").toString()));
			routeSuggestions[i].setBusStopNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busstopnumber")));
			routeSuggestions[i].setArrivalTime(json_arr.getJSONObject(i).getString("time"));
			routeSuggestions[i].setBusStopName(json_arr.getJSONObject(i).getString("busstopname"));
			routeSuggestions[i].setDestination(json_arr.getJSONObject(i).getString("destination"));
			routeSuggestions[i].setTravelTime(json_arr.getJSONObject(i).getString("duration"));
			routeSuggestions[i].setBusNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busnumber")));
		}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.v("jsonError","e:"+e.toString());
			//e.printStackTrace();
		}
		return routeSuggestions;
		
	 }
	 public Route[] suggestRoutes(Route[] routelist)
	 {
		 Route[] before = routelist; 
		 HashMap<Integer,HashMap<Integer,Integer>> distanceSort = new HashMap<Integer,HashMap<Integer,Integer>>();
		 for(int i = 0;i<before.length;i++)
		 {
			 int wD = before[i].getWalkingDistance();
			 int bussNr = before[i].getBusNumber();
			 HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
			 if(distanceSort.containsKey(bussNr))
			 {
				 Object[] keys = distanceSort.get(bussNr).keySet().toArray();
				 if(distanceSort.get(bussNr).get(keys[0]) > wD && wD != -1)
				 {
					 temp.put(i, wD);
					 distanceSort.put(bussNr, temp);
				 }
			 }
			 else
			 {
				 temp.put(i, wD);
				 distanceSort.put(bussNr, temp);
			 }
			
		 }
		 Route[] after = new Route[distanceSort.size()];
		 Object[] newkeys = distanceSort.keySet().toArray();
		 Arrays.sort(newkeys);
		 for(int i = 0;i<after.length;i++)
		 {
			 after[i] = new Route();
			 Object[] keys2 = distanceSort.get(newkeys[i]).keySet().toArray();
			 after[i] = before[Integer.parseInt(keys2[0].toString())];
		 }
		 return after; 
	 }
	 public void printOutRoutes(String FLAG, Route[] finalRoutes, boolean totaltimeDone)
	 {
		 for(int i = 0;i<finalRoutes.length;i++)
		 {
		  Log.v(FLAG+" "+i,"Line:"+finalRoutes[i].getBusNumber());
       	  Log.v(FLAG+" "+i,"ArrivalTime:"+finalRoutes[i].getArrivalTime());
       	  Log.v(FLAG+" "+i,"BusStopName:"+finalRoutes[i].getBusStopName());
       	  Log.v(FLAG+" "+i,"BusStopNumber:"+finalRoutes[i].getBusStopNumber());
       	  Log.v(FLAG+" "+i,"Destination:"+finalRoutes[i].getDestination());
       	  Log.v(FLAG+" "+i,"TravelTime:"+finalRoutes[i].getTravelTime());
       	  Log.v(FLAG+" "+i,"WalkingDistance:"+finalRoutes[i].getWalkingDistance());
       	  if(totaltimeDone)
       	  {
       	  Log.v(FLAG+" "+i,"TotalTime:"+finalRoutes[i].getTotalTime());
       	  }
		 }
	 }
	 public int calculateTotalTime(String arrival, String totalTime)
	 {
		 int tt = 0; 
		 int k_tt = Integer.parseInt(totalTime);
		 Date now = new Date(); 
		 String hours = arrival.substring(0, 2);
	//	 Log.v("HOURS","hours:"+hours);
		 String minutes = arrival.substring(2, 4);
	//	 Log.v("MINUTES","minutes:"+minutes);
		 int nowHours,nowMinutes,aHours,aMinutes = 0; 
		 nowHours = now.getHours();
		 nowMinutes = now.getMinutes();
	//	 Log.v("NOW","NOW:"+nowHours+":"+nowMinutes);
		 aHours = Integer.parseInt(hours);
		 aMinutes = Integer.parseInt(minutes);
	//	 Log.v("ARRIVAL","ARRIVAL:"+aHours+":"+aMinutes);
		 int difference = 0;
         if(nowHours == aHours)
         {
        	 difference = aMinutes - nowMinutes; 
         }
         else 
         {
        	 int nowint = nowHours * 60 + nowMinutes; 
        	 int aint = aHours * 60 + aMinutes; 
        	 difference = aint - nowint; 
         }
      //   Log.v("NOW","DIFF:"+difference+"- TOTAL:"+k_tt);
		tt = difference + k_tt; 
		return tt; 
	 }
	 public Route[] sortByTotalTime(Route[] routeSuggestion)
	 {
		 HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
		 
		 Route[] rs = routeSuggestion; 
		 Route[] newroutes = new Route[rs.length];
		 for(int i = 0;i<rs.length;i++)
		 {
			 System.out.println("In for loop: " + i);
			 temp.put(rs[i].getTotalTime(), i);
			 
		 }
		
		 Object[] keys = temp.keySet().toArray();
		 System.out.println("Temp size: " + temp.size());
		 System.out.println("Keys size: " + keys.length);
		 System.out.println("RS length: " + rs.length);
		 Arrays.sort(keys);
		 for(int y = 0;y<rs.length;y++)
		 {
			 newroutes[y] = new Route();
			 System.out.println("NewRoute: " + temp.get(keys[y]));
			 newroutes[y] = rs[temp.get(keys[y])];
			
		 }
		 return newroutes; 
	 }
}
