package test.BusTUC.Speech;

import test.BusTUC.Stops.ClosestStopOnMap;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author Runar Andersstuen
 */
public class CBRAnswer implements Parcelable
{

	private String answer;
	private double score;
	
	public CBRAnswer(Parcel source)
	{
		readFromParcel(source);
	}
	
	public CBRAnswer()
	{
		
	}

	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	
		dest.writeString(answer);
		dest.writeDouble(score);

		
	}
	
	private void readFromParcel(Parcel source) {
		 
		//GeoPoint geo = new GeoPoint(source.readInt(), source.readInt());
		answer = source.readString();
    	score = source.readDouble();
	} 
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new Parcelable.Creator() {
	            public CBRAnswer createFromParcel(Parcel in) {
	                return new CBRAnswer(in);
	            }
	 
	            public CBRAnswer[] newArray(int size) {
	                return new CBRAnswer[size];
	            }
	        };

}