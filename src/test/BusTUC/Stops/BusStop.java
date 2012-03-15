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

package test.BusTUC.Stops;

import test.BusTUC.Main.Route;
import android.location.Location;

public class BusStop implements Comparable<BusStop>{
	public Location location;
	public float distance;
	public int stopID;
	public String name;
	
	public BusStop(Location location, float distance, int stopID, String name){
		this.location = location;
		this.distance = distance;
		this.stopID = stopID;
		this.name = name;
	}
	
	public BusStop()
	{
		
	}
	public int compareTo(BusStop otherBusStop)
	{
		if(this.distance < otherBusStop.distance) return -1;
		else if(this.distance > otherBusStop.distance) return 1;
		else return 0;
		
	}
}
