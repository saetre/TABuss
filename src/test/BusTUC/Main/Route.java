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

package test.BusTUC.Main;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import test.BusTUC.R;

// Parcelable will assure Route objects can be sent as extra, to
// other activities. Can be seen as marshalling
public class Route implements Comparable<Route>, Parcelable
{

	private String arrivalTime; 
	private String busStopName; 
	private int busStopNumber; 
	private int busNumber; 
	private String travelTime; 
	private String destination; 
	private boolean transfer; 
	private int walkingDistance; 
	private int totalTime; 
	private GeoPoint location;
	public Route(String k_a, String k_be, int k_br, String k_tt, String k_d, GeoPoint location)
	{
		arrivalTime = k_a; 
		busStopName = k_be; 
		busStopNumber = k_br;
		travelTime = k_tt; 
		destination = k_d; 
		this.location = location;
	}
	public Route(Parcel in)
	{
		readFromParcel(in);
	}	

	public Route()
	{

	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public int getWalkingDistance() {
		return walkingDistance;
	}
	public void setWalkingDistance(int walkingDistance) {
		this.walkingDistance = walkingDistance;
	}
	public boolean isTransfer() {
		return transfer;
	}
	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}
	public int getBusNumber() {
		return busNumber;
	}
	public void setBusNumber(int busNumber) {
		this.busNumber = busNumber;
	}
	public String getBusStopName() {
		return busStopName;
	}
	public void setBusStopName(String busStopName) {
		this.busStopName = busStopName;
	}
	public int getBusStopNumber() {
		return busStopNumber;
	}
	public void setBusStopNumber(int busStopNumber) {
		this.busStopNumber = busStopNumber;
	}
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public int compareTo( Route otherRoute ) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		if ( this == otherRoute ) return EQUAL;

		if (this.totalTime < otherRoute.totalTime) return BEFORE;
		if (this.totalTime > otherRoute.totalTime) return AFTER;

		return EQUAL;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(arrivalTime);
		dest.writeString(busStopName);
		dest.writeInt(busStopNumber);
		dest.writeInt(busNumber);
		dest.writeString(travelTime);
		dest.writeString(destination);
		dest.writeString(String.valueOf(transfer));
		dest.writeInt(walkingDistance);
		dest.writeInt(totalTime);

	}

	private void readFromParcel(Parcel source) {

		arrivalTime = source.readString();
		busStopName = source.readString();
		busStopNumber = source.readInt(); 
		busNumber = source.readInt();
		travelTime = source.readString();
		destination = source.readString(); 
		transfer = Boolean.parseBoolean(source.readString()); 
		walkingDistance = source.readInt(); 
		totalTime = source.readInt(); 
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<?> CREATOR =
			new Parcelable.Creator() {
		public Route createFromParcel(Parcel in) {
			return new Route(in);
		}

		public Route[] newArray(int size) {
			return new Route[size];
		}
	};



}
