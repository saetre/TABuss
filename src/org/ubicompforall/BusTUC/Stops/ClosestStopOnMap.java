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

package org.ubicompforall.BusTUC.Stops;

import org.ubicompforall.BusTUC.Main.Route;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

/*
 * Class containing properties for the closest stops.
 * This to avoid doing huge lookup regarding lat/long
 * foooo
 */
public class ClosestStopOnMap implements Comparable <ClosestStopOnMap>, Parcelable
{
	private GeoPoint point;
	private int busStopID;
	private String stopName;
	
	public ClosestStopOnMap(GeoPoint point, int busStopID, String stopName)
	{	
		this.point = point;
		this.busStopID = busStopID;
		this.stopName = stopName;
	}
	
	public ClosestStopOnMap(Parcel source)
	{
		readFromParcel(source);

	}
	public ClosestStopOnMap()
	{
		
	}
	
	public String getStopName()
	{
		return stopName;
	}
	
	public void setStopName(String newName)
	{
		stopName = newName;
	}
	
	public GeoPoint getPoint()
	{
		return point;
	}
	
	public int getBusStopID()
	{
		return busStopID;
	}
	
	public void setPoint(GeoPoint newPoint)
	{
		point = newPoint;
	}
	
	public void setBusStopID(int newID)
	{
		busStopID = newID;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 @Override
	public int compareTo( ClosestStopOnMap otherHolder ) {
	   return this.getStopName().compareTo(otherHolder.getStopName());
	  }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	
		dest.writeInt(busStopID);
		dest.writeString(stopName);

		
	}
	
	private void readFromParcel(Parcel source) {
		 
		//GeoPoint geo = new GeoPoint(source.readInt(), source.readInt());
		busStopID = source.readInt();
    	stopName = source.readString();
	} 
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new Parcelable.Creator() {
	            public ClosestStopOnMap createFromParcel(Parcel in) {
	                return new ClosestStopOnMap(in);
	            }
	 
	            public ClosestStopOnMap[] newArray(int size) {
	                return new ClosestStopOnMap[size];
	            }
	        };

}
